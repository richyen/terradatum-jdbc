package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class EdbCallableStatementAdapter extends JdbcCallableStatementAdapter implements DbCallableStatementAdapter {
  private final EdbConnectionAdapter connectionAdapter;
  private final CallableStatement delegate;

  public EdbCallableStatementAdapter(EdbConnectionAdapter connectionAdapter, CallableStatement callableStatement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = callableStatement;
  }

  @Override
  protected CallableStatement delegate() {
    return delegate;
  }

  @Override
  public EdbConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }

  @Override
  public void registerArrayOutParameter(int index, String sqlTypeName) throws SQLException {
    registerParameterWithCorrectSearchPath(index, Types.ARRAY, sqlTypeName, delegate()::registerOutParameter);
  }

  @Override
  public void registerStructOutParameter(int index, String sqlTypeName) throws SQLException {
    registerParameterWithCorrectSearchPath(index, Types.STRUCT, sqlTypeName, delegate()::registerOutParameter);
  }

  public <S extends DbStruct> S getStruct(int index, Class<? extends DbStruct> type) throws SQLException, IllegalAccessException,
      InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
    Struct struct = (Struct) delegate().getObject(index);

    // noinspection unchecked
    S dbStruct = (S) type.newInstance();
    dbStruct.setAttributes(struct.getAttributes());

    return dbStruct;
  }

  public <A extends JdbcArrayList<?>> A getArray(int index, Class<? extends JdbcArrayList<?>> type) throws SQLException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    Array array = (Array) delegate().getObject(index);

    // noinspection unchecked
    A jdbcArrayList = (A) type.newInstance();
    for (Object element : (Object[]) array.getArray()) {
      if (element instanceof Struct) {
        Struct struct = (Struct) element;
        ((StructArrayList<?>) jdbcArrayList).add(struct);
      } else {
        jdbcArrayList.addObject(jdbcArrayList.getTypeToken().getRawType().cast(element));
      }
    }
    return jdbcArrayList;
  }

  @Override
  public <S extends DbStruct> void setStruct(int index, S struct) throws SQLException {
    delegate().setObject(index, struct, Types.STRUCT);
  }

  @Override
  public <A> void setArray(int index, JdbcArrayList<A> array) throws SQLException {
    Array newArray = getConnectionAdapter().createArrayOf(array.getSQLTypeName(), array.toArray());
    delegate().setObject(index, newArray, Types.OTHER);
  }

  private void registerParameterWithCorrectSearchPath(int parameterIndex, int sqlType, String typeName,
      EdbSearchPathParameterRegistrar creator) throws SQLException {
    String searchPath = getConnectionAdapter().getSearchPath();
    String name = getConnectionAdapter().setSearchPathToSchema(typeName);
    // noinspection unchecked
    creator.register(parameterIndex, sqlType, name.toLowerCase());
    getConnectionAdapter().setSearchPath(searchPath);
  }

  interface EdbSearchPathParameterRegistrar {
    void register(int parameterIndex, int sqlType, String typeName) throws SQLException;
  }
}
