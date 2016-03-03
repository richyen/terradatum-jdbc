package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public abstract class StructArrayList<S extends DbStruct> extends JdbcArrayList<S> {

  protected StructArrayList() {
  }

  @Override
  public StructArrayList<?> setArray(Array array)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    if (array != null) {
      // noinspection unchecked
      Object[] objects = (Object[]) array.getArray();
      for (Object object : objects) {
        add((Struct) object);
      }
    } else {
      throw new IllegalArgumentException("The java.sql.Array cannot be null");
    }
    return this;
  }

  @Override
  public StructArrayList<?> setElements(S[] elements) throws SQLException {
    if (elements != null) {
      for (S element : elements) {
        this.add(element);
      }
    } else {
      throw new IllegalArgumentException("The array of elements cannot be null");
    }
    return this;
  }

  public boolean add(Struct struct)
      throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    S dbStruct = createDbStruct(struct);
    return add(dbStruct);
  }

  private S createDbStruct(Struct struct)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException, NoSuchFieldException {
    if (struct != null) {
      S dbStruct = getTypeToken().constructor(getTypeToken().getRawType().getConstructor()).invoke(null);
      dbStruct.setAttributes(struct.getAttributes());
      return dbStruct;
    }
    return null;
  }
}
