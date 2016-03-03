package com.terradatum.jdbc;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.NUMBER;

import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public class OraclePreparedStatementAdapter extends JdbcPreparedStatementAdapter implements DbPreparedStatementAdapter {
  private final OracleConnectionAdapter connectionAdapter;
  private final OraclePreparedStatement delegate;

  public OraclePreparedStatementAdapter(OracleConnectionAdapter connectionAdapter, OraclePreparedStatement preparedStatement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = preparedStatement;
  }

  @Override
  protected OraclePreparedStatement delegate() {
    return delegate;
  }

  @Override
  public OracleConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }

  @Override
  public void setNumeric(int parameterIndex, Integer numeric) throws SQLException {
    delegate().setNUMBER(parameterIndex, new NUMBER(numeric));
  }
}
