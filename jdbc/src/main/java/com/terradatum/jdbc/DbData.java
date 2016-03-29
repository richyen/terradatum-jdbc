package com.terradatum.jdbc;

import java.sql.SQLException;

/**
 * Marker interface used by {@link JdbcCallableStatementAdapter} to {@code setData}.
 * Created by rbellamy on 2/10/16.
 */
public interface DbData {
  String getSQLTypeName() throws SQLException;
}
