package com.terradatum.jdbc.db;

import com.terradatum.jdbc.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rbellamy@terradatum.com
 * @date 2/18/16
 */
public class SQLExceptionTest extends AbstractAdapterTest {

  protected DbConnectionAdapter dbConnectionAdapter;

  public void throwSqlException(Connection connection, String searchPath) throws SQLException {
    String commandText = "{? = call metrics.test_error(?)}";
    Set<SqlError> sqlErrors = new HashSet<>();
    sqlErrors.add(new SqlError(20000, "TERR1"));
    dbConnectionAdapter = JdbcConnectionAdapterFactory.create(connection, sqlErrors, searchPath);
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.INTEGER);
    dbCallableStatementAdapter.setString(2, "Some error message");
    dbCallableStatementAdapter.execute();
  }
}
