package com.terradatum.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public interface DbPreparedStatementAdapter extends DbStatementAdapter, PreparedStatement {

  DbConnectionAdapter getConnectionAdapter();

  void setNumeric(int parameterIndex, Integer numeric) throws SQLException;

}
