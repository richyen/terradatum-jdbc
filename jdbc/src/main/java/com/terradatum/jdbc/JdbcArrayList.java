package com.terradatum.jdbc;

import com.google.common.collect.ForwardingList;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author rbellamy@terradatum.com
 * @date 2/8/16
 */
public abstract class JdbcArrayList<S> extends ForwardingList<S> implements DbData {
  private final TypeToken<S> typeToken = new TypeToken<S>(getClass()) {
  };

  protected JdbcArrayList() {
  }

  @SuppressWarnings("unchecked")
  public JdbcArrayList<?> setArray(Array array)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    if (array != null) {
      setElements((S[]) array.getArray());
    }
    return this;
  }

  public JdbcArrayList<?> setElements(S[] elements) throws SQLException {
    if (elements != null) {
      Collections.addAll(this, elements);
    }
    return this;
  }

  protected abstract ArrayList<S> delegate();

  public abstract String getSQLTypeName();

  public TypeToken<S> getTypeToken() {
    return typeToken;
  }

  /**
   * This method is required because of reflection gymnastics in {@link OracleCallableStatementAdapter} and
   * {@link EdbCallableStatementAdapter}. Basically I couldn't find a way to cast the {@param element} to the proper type from
   * outside this method, so the best I could do was pass it in as an object and let this method do the unchecked cast...
   *
   * @param element an element of the {@link Array}
   * @return true if success, false if not
   */
  public boolean addObject(Object element) {
    // noinspection unchecked
    return super.add((S) element);
  }
}
