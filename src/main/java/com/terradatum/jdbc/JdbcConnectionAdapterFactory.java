package com.terradatum.jdbc;

import com.edb.jdbc4.Jdbc4Connection;
import oracle.jdbc.OracleConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author rbellamy@terradatum.com
 * @date 2/3/16
 */
public class JdbcConnectionAdapterFactory {

  public static @NotNull DbConnectionAdapter create(Connection connection) throws SQLException {
    return create(connection, null, null);
  }

  public static @NotNull DbConnectionAdapter create(Connection connection, Set<SqlError> sqlErrors) throws SQLException {
    return create(connection, sqlErrors, null);
  }

  public static @NotNull DbConnectionAdapter create(Connection connection, String searchPath) throws SQLException {
    return create(connection, null, searchPath);
  }

  public static @NotNull DbConnectionAdapter create(Connection connection, Set<SqlError> sqlErrors, String searchPath) throws SQLException {
    Class<OracleConnection> oracleConnectionClass = OracleConnection.class;
    Class<Jdbc4Connection> jdbc4ConnectionClass = Jdbc4Connection.class;
    Class<? extends Connection> connectionClass = connection.getClass();

    if (oracleConnectionClass.isAssignableFrom(connectionClass)) {
      return getOracleConnectionAdapter((OracleConnection) connection, sqlErrors);
    } else if (jdbc4ConnectionClass.isAssignableFrom(connectionClass)) {
      return getEdbConnectionAdapter((Jdbc4Connection) connection, sqlErrors, searchPath);
    }

    try {
      if (connection.isWrapperFor(OracleConnection.class)) {
        return getOracleConnectionAdapter(connection.unwrap(OracleConnection.class), sqlErrors);
      } else if (connection.isWrapperFor(Jdbc4Connection.class)) {
        return getEdbConnectionAdapter(connection.unwrap(Jdbc4Connection.class), sqlErrors, searchPath);
      }
    } catch (SQLException e) {
      throw new IllegalArgumentException("The Connection of type " + connectionClass.getName() + " has no adapters.", e);
    }

    throw new IllegalArgumentException("The Connection of type " + connectionClass.getName() + " has no adapters.");
  }

  private static EdbConnectionAdapter getEdbConnectionAdapter(Jdbc4Connection connection, Set<SqlError> sqlErrors, String searchPath) throws
      SQLException {
    EdbConnectionAdapter edbConnectionAdapter = new EdbConnectionAdapter(connection, sqlErrors);
    if (searchPath != null && !searchPath.equals("")) {
      edbConnectionAdapter.setSearchPath(searchPath);
    }
    return edbConnectionAdapter;
  }

  private static OracleConnectionAdapter getOracleConnectionAdapter(OracleConnection connection, Set<SqlError> sqlErrors) throws
      SQLException {
    return new OracleConnectionAdapter(connection, sqlErrors);
  }
}
