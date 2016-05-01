package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public abstract class StructArrayList<S extends DbStruct> extends JdbcArrayList<S> {

  protected StructArrayList() {
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
   * Array list implementation specific for storing PG array elements.
   */
  private class DimensionalArrayList extends ArrayList<Object> {
    /**
     * How many dimensions.
     */
    int dimensionsCount = 1;
  }
}
