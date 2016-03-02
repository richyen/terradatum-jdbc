package com.terradatum.jdbc;

import java.sql.Statement;

/**
 * @author rbellamy@terradatum.com
 * @date 1/31/16
 */
public class EdbStatementAdapter extends JdbcStatementAdapter implements DbStatementAdapter {
  private final EdbConnectionAdapter connectionAdapter;
  private final Statement delegate;

  public EdbStatementAdapter(EdbConnectionAdapter connectionAdapter, Statement statement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = statement;
  }

  @Override
  protected Statement delegate() {
    return delegate;
  }

  @Override
  public EdbConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }
}
