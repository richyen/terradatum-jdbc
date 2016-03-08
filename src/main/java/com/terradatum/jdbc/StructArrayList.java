package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;
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
  public StructArrayList<?> setArray(Array array)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    if (array != null) {
      Object[] objects = (Object[]) array.getArray();
      for (Object object : objects) {
        add((Struct) object);
      }
    }
    return this;
  }

  @Override
  public StructArrayList<?> setElements(S[] elements) throws SQLException {
    if (elements != null) {
      Collections.addAll(this, elements);
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
