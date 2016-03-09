package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public interface DbCallableStatementAdapter extends DbPreparedStatementAdapter, CallableStatement {

  DbConnectionAdapter getConnectionAdapter();

  /**
   * Get a {@link DbStruct} returned via this {@link DbCallableStatementAdapter}.
   * 
   * @param index
   *          the column index of the result
   * @param type
   *          the type sub-class of {@link DbStruct}
   * @param <S>
   *          the type of the sub-class of {@link DbStruct}
   * @return the fully hydrated object
   * @throws SQLException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws NoSuchFieldException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  <S extends DbStruct> S getStruct(int index, Class<? extends DbStruct> type) throws SQLException, IllegalAccessException,
      InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException;

  /**
   * Get a {@link JdbcArrayList} returned via this {@link DbCallableStatementAdapter}. If the elements of the array are
   * 'primitive' database types (e.g. {@code varchar}, {@code numeric}, {@code char}, etc) then the expectation is that the
   * corresponding Java types will be correctly mapped. If the elements are of type {@link Struct}, then returning type will a
   * {@link StructArrayList}. sub-classed from {@link DbStruct}
   * 
   * @param <A>
   *          the type of the sub-class of {@link JdbcArrayList}
   * @param index
   *          the column index of the result
   * @param type
   *          the concrete type of {@link JdbcArrayList}
   * @return the fully hydrated object of type {@link JdbcArrayList} or {@link StructArrayList}
   * @throws SQLException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws NoSuchFieldException
   * @throws InvocationTargetException
   */
  <A extends JdbcArrayList<?>> A getArray(int index, Class<? extends JdbcArrayList<?>> type) throws SQLException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException;

  <S extends DbStruct> void setStruct(int index, S struct) throws SQLException;

  <A> void setArray(int index, JdbcArrayList<A> array) throws SQLException;

  void setData(int index, DbData data) throws SQLException;
}
