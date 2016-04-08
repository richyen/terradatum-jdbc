package com.terradatum.jdbc;

import com.edb.util.StructParser;
import com.google.common.base.Strings;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public abstract class StructArrayList<S extends DbStruct> extends JdbcArrayList<S> {

  protected StructArrayList() {
  }

  @Override
  public StructArrayList<S> setArray(Object array) throws InvocationTargetException, SQLException, NoSuchFieldException, IllegalAccessException,
      NoSuchMethodException {
    if (array instanceof String) {
      return setArray((String) array);
    } else {
      return setArray((Array) array);
    }
  }

  @SuppressWarnings("unchecked")
  private StructArrayList<S> setArray(String arrayAsString) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException {
    Object[] elements = buildArrayList(arrayAsString).toArray();
    for (Object element : elements) {
      if (element instanceof String) {
        List attributes = StructParser.parse((String) element, true);
        S dbStruct = getTypeToken().constructor(getTypeToken().getRawType().getConstructor()).invoke(null);
        dbStruct.setAttributes(attributes.toArray());
        add(dbStruct);
      } else {
        add((Struct) element);
      }
    }
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public StructArrayList<S> setArray(Array array) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException {
    if (array != null) {
      Object[] elements = (Object[]) array.getArray();
      for (Object element : elements) {
        add((Struct) element);
      }
    }
    return this;
  }

  @Override
  public StructArrayList<S> setElements(S[] elements) throws SQLException {
    if (elements != null) {
      Collections.addAll(this, elements);
    }
    return this;
  }

  public boolean add(Struct struct) throws SQLException, NoSuchMethodException, InvocationTargetException,
      IllegalAccessException, NoSuchFieldException {
    S dbStruct = createDbStruct(struct);
    return add(dbStruct);
  }

  private S createDbStruct(Struct struct) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
      SQLException, NoSuchFieldException {
    if (struct != null) {
      S dbStruct = getTypeToken().constructor(getTypeToken().getRawType().getConstructor()).invoke(null);
      dbStruct.setAttributes(struct.getAttributes());
      return dbStruct;
    }
    return null;
  }


  /**
   * This method is plucked directly from the PostgreSQL JDBC code from the private, synchronized, {@link com.edb.jdbc.PgArray}
   * {@code buildArrayList} method. The reason for this is that when returning Composite Structs with attributes that are {@link Array}'s
   * the PostgreSQL and EDB JDBC drivers fail to create those member attributes as even weakly referenced {@link Array}s. Instead
   * they are <b>{@link String}s</b>! In other words, you cannot do the following:
   * <code>
   * <pre>(Array) someStruct.getAttributes[6]</pre>
   * </code>
   * You will get a {@link ClassCastException} saying you cannot cast from {@link String} to {@link Array}.
   *
   * @param arrayAsString the {@link Array} in string form
   * @return the {@link ArrayList} of elements.
   * @throws SQLException
   */
  private ArrayList buildArrayList(String arrayAsString) throws SQLException {

    StructArrayList.DimensionalArrayList arrayList = new StructArrayList.DimensionalArrayList();

    char delim = getEdbArrayDelimiter();

    if (!Strings.isNullOrEmpty(arrayAsString)) {

      char[] chars = arrayAsString.toCharArray();
      StringBuilder buffer = null;
      boolean insideString = false;
      boolean wasInsideString = false; // needed for checking if NULL
      // value occurred
      List<StructArrayList.DimensionalArrayList> dims = new ArrayList<>(); // array dimension arrays
      StructArrayList.DimensionalArrayList curArray = arrayList; // currently processed array

      // Starting with 8.0 non-standard (beginning index
      // isn't 1) bounds the dimensions are returned in the
      // data formatted like so "[0:3]={0,1,2,3,4}".
      // Older versions simply do not return the bounds.
      //
      // Right now we ignore these bounds, but we could
      // consider allowing these index values to be used
      // even though the JDBC spec says 1 is the first
      // index. I'm not sure what a client would like
      // to see, so we just retain the old behavior.
      int startOffset = 0;
      {
        if (chars[0] == '[') {
          while (chars[startOffset] != '=') {
            startOffset++;
          }
          startOffset++; // skip =
        }
      }

      for (int i = startOffset; i < chars.length; i++) {

        // escape character that we need to skip
        if (chars[i] == '\\') {
          i++;
        } else if (!insideString && chars[i] == '{') {
          // subarray start
          if (dims.size() == 0) {
            dims.add(arrayList);
          } else {
            StructArrayList.DimensionalArrayList a = new StructArrayList.DimensionalArrayList();
            StructArrayList.DimensionalArrayList p = dims.get(dims.size() - 1);
            p.add(a);
            dims.add(a);
          }
          curArray = dims.get(dims.size() - 1);

          // number of dimensions
          {
            for (int t = i + 1; t < chars.length; t++) {
              if (chars[t] == '{') {
                curArray.dimensionsCount++;
              } else {
                break;
              }
            }
          }

          buffer = new StringBuilder();
          continue;
        } else if (chars[i] == '"') {
          // quoted element
          insideString = !insideString;
          wasInsideString = true;
          continue;
        } else if (!insideString && Character.isWhitespace(chars[i])) {
          // white space
          continue;
        } else if ((!insideString && (chars[i] == delim || chars[i] == '}'))
            || i == chars.length - 1) {
          // array end or element end
          // when character that is a part of array element
          if (chars[i] != '"' && chars[i] != '}' && chars[i] != delim && buffer != null) {
            buffer.append(chars[i]);
          }

          String b = buffer == null ? null : buffer.toString();

          // add element to current array
          if (b != null && (b.length() > 0 || wasInsideString)) {
            curArray.add(!wasInsideString ? null : b);
          }

          wasInsideString = false;
          buffer = new StringBuilder();

          // when end of an array
          if (chars[i] == '}') {
            dims.remove(dims.size() - 1);

            // when multi-dimension
            if (dims.size() > 0) {
              curArray = dims.get(dims.size() - 1);
            }

            buffer = null;
          }

          continue;
        }

        if (buffer != null) {
          buffer.append(chars[i]);
        }
      }
    }

    return arrayList;
  }

  /**
   * Array list implementation specific for storing PG array elements.
   */
  private class DimensionalArrayList extends ArrayList<Object> {
    /**
     * How many dimensions.
     */
    int dimensionsCount = 1;
  }
}
