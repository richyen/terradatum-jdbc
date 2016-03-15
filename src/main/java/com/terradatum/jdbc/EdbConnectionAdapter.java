package com.terradatum.jdbc;

import com.edb.jdbc4.Jdbc4Connection;
import com.google.common.base.Strings;

import java.sql.*;
import java.util.List;
import java.util.Set;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class EdbConnectionAdapter extends JdbcConnectionAdapter implements DbConnectionAdapter {

  private final Jdbc4Connection delegate;

  public EdbConnectionAdapter(Jdbc4Connection connection, Set<SqlError> sqlErrors) throws SQLException {
    super(sqlErrors);
    this.delegate = connection;
  }

  @Override
  protected Jdbc4Connection delegate() {
    return delegate;
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    List elementList = unwindModel(elements);
    return createTypeWithCorrectSearchPath(typeName, elementList.toArray(), delegate()::createArrayOf);
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    List attributeList = unwindModel(attributes);
    return createTypeWithCorrectSearchPath(typeName, attributeList.toArray(), delegate()::createStruct);
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
   * Identifies a {@link SQLException} as resulting from a Custom Exception. Compared against the {@code List} of {@link SqlError}'s
   * used to construct the {@link DbConnectionAdapter}.
   *
   * @param sqlException
   *          the {@link SQLException} to test
   * @return returns true if the {@link SQLException} is caused a Custom Exception
   */
  @Override
  public boolean isCustomException(SQLException sqlException) {
    String sqlState = SqlError.getSqlState(sqlException);

    if (getSqlErrors() != null) {
      for (SqlError sqlError : getSqlErrors()) {
        if (!Strings.isNullOrEmpty(sqlState) && sqlError.getState().equals(sqlState)) {
          return true;
        }
      }
    }
    return false;
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

  private <T> T createTypeWithCorrectSearchPath(String typeName, Object[] objects, EdbConnectionAdapter.EdbSearchPathCreator<?> creator)
      throws SQLException {
    String searchPath = getSearchPath();
    String name = setSearchPathToSchema(typeName);
    // noinspection unchecked
    T ret = (T) creator.create(name.toLowerCase(), objects);
    setSearchPath(searchPath);
    return ret;
  }

  interface EdbSearchPathCreator<T> {
    T create(String typeName, Object[] objects) throws SQLException;
  }
}
