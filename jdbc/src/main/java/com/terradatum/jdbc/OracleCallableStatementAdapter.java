package com.terradatum.jdbc;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public class OracleCallableStatementAdapter extends JdbcCallableStatementAdapter implements DbCallableStatementAdapter {
  private final OracleConnectionAdapter connectionAdapter;
  private final OracleCallableStatement delegate;

  public OracleCallableStatementAdapter(OracleConnectionAdapter connectionAdapter, OracleCallableStatement callableStatement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = callableStatement;
  }

  @Override
  protected OracleCallableStatement delegate() {
    return delegate;
  }

  @Override
  public OracleConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }

  @Override
  public void registerArrayOutParameter(int index, String sqlTypeName) throws SQLException {
    delegate().registerOutParameter(index, OracleTypes.ARRAY, sqlTypeName.toUpperCase());
  }

  @Override
  public void registerStructOutParameter(int index, String sqlTypeName) throws SQLException {
    delegate().registerOutParameter(index, OracleTypes.STRUCT, sqlTypeName.toUpperCase());
  }

  public <S extends DbStruct> S getStruct(int index, Class<? extends DbStruct> type) throws SQLException, IllegalAccessException,
      InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
    STRUCT struct = (STRUCT) delegate().getObject(index);

    // noinspection unchecked
    S dbStruct = (S) type.newInstance();
    dbStruct.setAttributes(struct.getAttributes());

    return dbStruct;
  }

  public <A extends JdbcArrayList<?>> A getArray(int index, Class<? extends JdbcArrayList<?>> type) throws SQLException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    ARRAY array = (ARRAY) delegate().getObject(index);

    // noinspection unchecked
    A jdbcArrayList = (A) type.newInstance();
    for (Object element : (Object[]) array.getArray()) {
      if (element instanceof STRUCT) {
        STRUCT struct = (STRUCT) element;
        ((StructArrayList<?>) jdbcArrayList).add(struct);
      } else {
        jdbcArrayList.addObject(jdbcArrayList.getTypeToken().getRawType().cast(element));
      }
    }

    return jdbcArrayList;
  }

  @Override
  public <S extends DbStruct> void setStruct(int index, S struct) throws SQLException {
    STRUCT newStruct = (STRUCT) getConnectionAdapter().createStruct(struct.getSQLTypeName().toUpperCase(),
        struct.getAttributes());
    delegate().setSTRUCT(index, newStruct);
  }

  @Override
  public <A> void setArray(int index, JdbcArrayList<A> array) throws SQLException {
    ARRAY newArray = (ARRAY) getConnectionAdapter().createArrayOf(array.getSQLTypeName().toUpperCase(), array.toArray());
    delegate().setARRAY(index, newArray);
  }
}
