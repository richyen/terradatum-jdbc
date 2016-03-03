package com.terradatum.jdbc;

import oracle.jdbc.OracleStatement;

/**
 * @author rbellamy@terradatum.com
 * @date 1/31/16
 */
public class OracleStatementAdapter extends JdbcStatementAdapter implements DbStatementAdapter {

  private final OracleConnectionAdapter connectionAdapter;
  private final OracleStatement delegate;

  public OracleStatementAdapter(OracleConnectionAdapter connectionAdapter, OracleStatement statement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = statement;
  }

  @Override
  protected OracleStatement delegate() {
    return delegate;
  }

  @Override
  public OracleConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }

}
