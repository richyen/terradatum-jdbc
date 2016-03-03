package com.terradatum.jdbc;

import com.google.common.collect.ForwardingList;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author rbellamy@terradatum.com
 * @date 2/8/16
 */
public abstract class JdbcArrayList<S> extends ForwardingList<S> implements DbData {
  private final TypeToken<S> typeToken = new TypeToken<S>(getClass()) {
  };

  protected JdbcArrayList() {
  }

  public JdbcArrayList<?> setArray(Array array)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    if (array != null) {
      // noinspection unchecked
      setElements((S[]) array.getArray());
    } else {
      throw new IllegalArgumentException("The java.sql.Array cannot be null");
    }
    return this;
  }

  public JdbcArrayList<?> setElements(S[] elements) throws SQLException {
    if (elements != null) {
      for (S element : elements) {
        this.add(element);
      }
    } else {
      throw new IllegalArgumentException("The array of elements cannot be null");
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
   * @param element
   *          an element of the {@link Array}
   * @return true if success, false if not
   */
  public boolean addObject(Object element) {
    // noinspection unchecked
    return super.add((S) element);
  }
}
