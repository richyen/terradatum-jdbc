package com.terradatum.jdbc;

import java.sql.Statement;

/**
 * @author rbellamy@terradatum.com
 * @date 1/31/16
 */
public interface DbStatementAdapter extends Statement {

  DbConnectionAdapter getConnectionAdapter();
}
