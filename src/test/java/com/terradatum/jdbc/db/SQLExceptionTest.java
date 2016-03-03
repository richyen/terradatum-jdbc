package com.terradatum.jdbc.db;

import com.terradatum.jdbc.AbstractAdapterTest;
import com.terradatum.jdbc.DbCallableStatementAdapter;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.JdbcConnectionAdapterFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author rbellamy@terradatum.com
 * @date 2/18/16
 */
public class SQLExceptionTest extends AbstractAdapterTest {

  protected DbConnectionAdapter dbConnectionAdapter;

  public void throwSqlException(Connection connection, String searchPath) throws SQLException {
    String commandText = "{? = call metrics.test_error(?)}";
    dbConnectionAdapter = JdbcConnectionAdapterFactory.create(connection, searchPath);
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.INTEGER);
    dbCallableStatementAdapter.setString(2, "Some error message");
    dbCallableStatementAdapter.execute();
  }
}
