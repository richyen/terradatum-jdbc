package com.terradatum.jdbc;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleStatement;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.List;
import java.util.Set;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class OracleConnectionAdapter extends JdbcConnectionAdapter implements DbConnectionAdapter {

  private final OracleConnection delegate;

  public OracleConnectionAdapter(OracleConnection connection, Set<SqlError> sqlErrors) throws SQLException {
    super(sqlErrors);
    this.delegate = connection;
  }

  @Override
  protected OracleConnection delegate() {
    return delegate;
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    List elementList = unwindModel(elements);
    return delegate().createARRAY(typeName.toUpperCase(), elementList.toArray());
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    List attributeList = unwindModel(attributes);
    return delegate().createStruct(typeName.toUpperCase(), attributeList.toArray());
  }

  @Override
  public DbStatementAdapter createStatementAdapter() throws SQLException {
    return new OracleStatementAdapter(this, (OracleStatement) delegate().createStatement());
  }

  @Override
  public DbPreparedStatementAdapter prepareStatementAdapter(String sql) throws SQLException {
    return new OraclePreparedStatementAdapter(this, (OraclePreparedStatement) delegate().prepareStatement(sql));
  }

  @Override
  public DbCallableStatementAdapter prepareCallAdapter(String sql) throws SQLException {
    return new OracleCallableStatementAdapter(this, (OracleCallableStatement) delegate().prepareCall(sql));
  }

  /**
   * Identifies a {@link SQLException} as resulting from a Custom Exception. Compared against the {@code List} of {@link SqlError}'s
   * used to construct the {@link DbConnectionAdapter}.
   *
   * @param sqlException
   *          the {@link SQLException} to test
   * @return returns true if the {@link SQLException} is caused a Custom Exception
   */
  @Override
  public boolean isCustomException(SQLException sqlException) {
    int sqlErrorCode = SqlError.getSqlErrorCode(sqlException);

    if (getSqlErrors() != null) {
      for (SqlError sqlError : getSqlErrors()) {
        if (sqlError.getErrorCode() == sqlErrorCode) {
          return true;
        }
      }
    }
    return false;
  }

}
