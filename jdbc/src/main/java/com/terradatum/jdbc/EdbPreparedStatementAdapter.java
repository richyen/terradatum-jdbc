package com.terradatum.jdbc;

import java.sql.PreparedStatement;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class EdbPreparedStatementAdapter extends JdbcPreparedStatementAdapter implements DbPreparedStatementAdapter {
  private final EdbConnectionAdapter connectionAdapter;
  private final PreparedStatement delegate;

  public EdbPreparedStatementAdapter(EdbConnectionAdapter connectionAdapter, PreparedStatement preparedStatement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = preparedStatement;
  }

  protected PreparedStatement delegate() {
    return delegate;
  }

  @Override
  public EdbConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }
}
