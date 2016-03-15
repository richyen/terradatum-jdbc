package com.terradatum.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public interface DbConnectionAdapter extends Connection {

  /**
   * Creates a {@link DbStatementAdapter} object for sending SQL statements to the database. SQL statements without parameters are
   * normally executed using {@link DbStatementAdapter} objects. If the same SQL statement is executed many times, it may be more
   * efficient to use a {@link DbPreparedStatementAdapter} object.
   * <p>
   * Result sets created using the returned <code>Statement</code> object will by default be type <code>TYPE_FORWARD_ONLY</code>
   * and have a concurrency level of <code>CONCUR_READ_ONLY</code>. The holdability of the created result sets can be determined
   * by calling {@link #getHoldability}.
   *
   * @return a new default {@link DbStatementAdapter} object
   * @throws SQLException
   *           if a database access error occurs or this method is called on a closed connection
   */
  DbStatementAdapter createStatementAdapter() throws SQLException;

  /**
   * Creates a {@link DbPreparedStatementAdapter} object for sending parameterized SQL statements to the database.
   * <p>
   * A SQL statement with or without IN parameters can be pre-compiled and stored in a {@link DbPreparedStatementAdapter} object.
   * This object can then be used to efficiently execute this statement multiple times.
   * <p>
   * <P>
   * <B>Note:</B> This method is optimized for handling parametric SQL statements that benefit from precompilation. If the driver
   * supports precompilation, the method <code>prepareStatementAdapter</code> will send the statement to the database for
   * precompilation. Some drivers may not support precompilation. In this case, the statement may not be sent to the database
   * until the {@link DbPreparedStatementAdapter} object is executed. This has no direct effect on users; however, it does affect
   * which methods throw certain {@link SQLException} objects.
   * <p>
   * Result sets created using the returned {@link DbPreparedStatementAdapter} object will by default be type
   * <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>. The holdability of the created
   * result sets can be determined by calling <code>#getHoldability</code>.
   *
   * @param sql
   *          an SQL statement that may contain one or more '?' IN parameter placeholders
   * @return a new default {@link DbPreparedStatementAdapter} object containing the pre-compiled SQL statement
   * @throws SQLException
   *           if a database access error occurs or this method is called on a closed connection
   */
  DbPreparedStatementAdapter prepareStatementAdapter(String sql) throws SQLException;

  /**
   * Creates a {@link DbCallableStatementAdapter} object for calling database stored procedures. The
   * {@link DbCallableStatementAdapter} object provides methods for setting up its IN and OUT parameters, and methods for
   * executing the call to a stored procedure.
   * <p>
   * <P>
   * <B>Note:</B> This method is optimized for handling stored procedure call statements. Some drivers may send the call statement
   * to the database when the method <code>prepareCallAdapter</code> is done; others may wait until the {@link CallableStatement}
   * object is executed. This has no direct effect on users; however, it does affect which method throws certain SQLExceptions.
   * <p>
   * Result sets created using the returned {@link DbCallableStatementAdapter} object will by default be type
   * <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>. The holdability of the created
   * result sets can be determined by calling <code>#getHoldability</code>.
   *
   * @param sql
   *          an SQL statement that may contain one or more '?' parameter placeholders. Typically this statement is specified
   *          using JDBC call escape syntax.
   * @return a new default {@link DbCallableStatementAdapter} object containing the pre-compiled SQL statement
   * @throws SQLException
   *           if a database access error occurs or this method is called on a closed connection
   */
  DbCallableStatementAdapter prepareCallAdapter(String sql) throws SQLException;

  /**
   * Identifies a {@link SQLException} as resulting from a Custom Exception. Compared against the {@code List} of {@link SqlError}'s
   * used to construct the {@link DbConnectionAdapter}.
   *
   * @param sqlException
   *          the {@link SQLException} to test
   * @return returns true if the {@link SQLException} is caused a Custom Exception
   */
  boolean isCustomException(SQLException sqlException);
}
