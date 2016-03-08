package com.terradatum.jdbc;

import com.edb.jdbc4.Jdbc4Connection;
import oracle.jdbc.OracleConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 2/3/16
 */
public class JdbcConnectionAdapterFactory {

  public static @NotNull DbConnectionAdapter create(Connection connection, String searchPath) throws SQLException {
    Class<OracleConnection> oracleConnectionClass = OracleConnection.class;
    Class<Jdbc4Connection> jdbc4ConnectionClass = Jdbc4Connection.class;
    Class<? extends Connection> connectionClass = connection.getClass();

    if (oracleConnectionClass.isAssignableFrom(connectionClass)) {
      return getOracleConnectionAdapter((OracleConnection) connection);
    } else if (jdbc4ConnectionClass.isAssignableFrom(connectionClass)) {
      return getEdbConnectionAdapter((Jdbc4Connection) connection, searchPath);
    }

    try {
      if (connection.isWrapperFor(OracleConnection.class)) {
        return getOracleConnectionAdapter(connection.unwrap(OracleConnection.class));
      } else if (connection.isWrapperFor(Jdbc4Connection.class)) {
        return getEdbConnectionAdapter(connection.unwrap(Jdbc4Connection.class), searchPath);
      }
    } catch (SQLException e) {
      throw new IllegalArgumentException("The Connection of type " + connectionClass.getName() + " has no adapters.", e);
    }

    throw new IllegalArgumentException("The Connection of type " + connectionClass.getName() + " has no adapters.");
  }

  private static @NotNull EdbConnectionAdapter getEdbConnectionAdapter(Jdbc4Connection connection, String searchPath) throws
      SQLException {
    EdbConnectionAdapter edbConnectionAdapter = new EdbConnectionAdapter(connection);
    if (searchPath != null && !searchPath.equals("")) {
      edbConnectionAdapter.setSearchPath(searchPath);
    }
    return edbConnectionAdapter;
  }

  private static @NotNull OracleConnectionAdapter getOracleConnectionAdapter(OracleConnection connection) {
    return new OracleConnectionAdapter(connection);
  }
}
