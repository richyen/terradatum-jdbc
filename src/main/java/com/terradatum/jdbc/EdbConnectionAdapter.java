package com.terradatum.jdbc;

import com.edb.jdbc4.Jdbc4Connection;
import com.google.common.base.Strings;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class EdbConnectionAdapter extends JdbcConnectionAdapter implements DbConnectionAdapter {

  private final Jdbc4Connection delegate;

  private static final Set<String> CUSTOM_ERROR_CODES = new HashSet<>();

  static {
    CUSTOM_ERROR_CODES.add("TERR1"); // Unspecified failure
    CUSTOM_ERROR_CODES.add("TERR2"); // Login failure
  }

  public EdbConnectionAdapter(Jdbc4Connection connection) throws SQLException {
    this.delegate = connection;
  }

  @Override
  protected Jdbc4Connection delegate() {
    return delegate;
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    return createTypeWithCorrectSearchPath(typeName, elements, delegate()::createArrayOf);
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    return createTypeWithCorrectSearchPath(typeName, attributes, delegate()::createStruct);
  }

  @Override
  public DbStatementAdapter createStatementAdapter() throws SQLException {
    return new EdbStatementAdapter(this, delegate().createStatement());
  }

  @Override
  public DbPreparedStatementAdapter prepareStatementAdapter(String sql) throws SQLException {
    return new EdbPreparedStatementAdapter(this, delegate().prepareStatement(sql));
  }

  @Override
  public DbCallableStatementAdapter prepareCallAdapter(String sql) throws SQLException {
    return new EdbCallableStatementAdapter(this, delegate().prepareCall(sql));
  }

  /**
   * Identifies a {@link SQLException} as resulting from a Custom Exception
   *
   * @param sqlException
   *          the {@link SQLException} to test
   * @return returns true if the {@link SQLException} is caused a Custom Exception
   */
  @Override
  public boolean isCustomException(SQLException sqlException) {
    return isCustomException(sqlException, CUSTOM_ERROR_CODES);
  }

  /**
   * Get the search_path of the current connection.
   *
   * @return The search_path used by the underlying connection
   * @throws SQLException
   */
  public String getSearchPath() throws SQLException {
    String searchPath = null;
    try (Statement statement = delegate().createStatement()) {
      ResultSet rs = statement.executeQuery("show search_path");
      if (rs.next()) {
        searchPath = rs.getString(1);
      }
    }
    return searchPath;
  }

  /**
   * Set the search_path of the current connection.
   *
   * @param searchPath
   * @throws SQLException
   */
  public void setSearchPath(String searchPath) throws SQLException {
    try (Statement statement = delegate().createStatement()) {
      statement.execute("set search_path to " + searchPath);
    }
  }

  /**
   * Because the EDB drivers do not currently accept fully-qualified OBJECT or TABLE names, this method parses the SQLTypeName
   * string and sets the search path based on whether that string has a schema name prefix.
   *
   * @param sqlTypeName
   * @return The unqualified name of the database OBJECT or TABLE
   * @throws SQLException
   */
  public String setSearchPathToSchema(String sqlTypeName) throws SQLException {
    String name = sqlTypeName;
    int dotIndex = sqlTypeName.indexOf('.');
    if (dotIndex != -1) {
      String schema = sqlTypeName.substring(0, dotIndex);
      name = sqlTypeName.substring(dotIndex + 1);

      setSearchPath(schema);
    }

    return name;
  }

  private <T> T createTypeWithCorrectSearchPath(String typeName, Object[] objects, EdbSearchPathCreator<?> creator)
      throws SQLException {
    String searchPath = getSearchPath();
    String name = setSearchPathToSchema(typeName);
    // noinspection unchecked
    T ret = (T) creator.create(name.toLowerCase(), objects);
    setSearchPath(searchPath);
    return ret;
  }

  /**
   * Gets the SQL state code from the supplied {@link SQLException exception}.
   * <p>
   * Some JDBC drivers nest the actual exception from a batched update, so we might need to dig down into the nested exception.
   * 
   * @param sqlException
   *          the exception from which the {@link SQLException#getSQLState() SQL state} is to be extracted
   * @return the SQL state code
   */
  private String getSqlState(SQLException sqlException) {
    String sqlState = null;
    if (sqlException != null) {
      sqlState = sqlException.getSQLState();
      if (Strings.isNullOrEmpty(sqlState)) {
        return getSqlState(sqlException.getNextException());
      }
    }
    return sqlState;
  }

  private boolean isCustomException(SQLException sqlException, Set<?> customStates) {
    String sqlState = getSqlState(sqlException);
    if (!Strings.isNullOrEmpty(sqlState) && customStates.contains(sqlState)) {
      return true;
    }
    return false;
  }

  interface EdbSearchPathCreator<T> {
    T create(String typeName, Object[] objects) throws SQLException;
  }
}
