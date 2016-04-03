package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

import static java.sql.Types.REF;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
class EdbCallableStatementAdapter extends JdbcCallableStatementAdapter implements DbCallableStatementAdapter {
  private final EdbConnectionAdapter connectionAdapter;
  private final CallableStatement delegate;

  public EdbCallableStatementAdapter(EdbConnectionAdapter connectionAdapter, CallableStatement callableStatement) {
    this.connectionAdapter = connectionAdapter;
    this.delegate = callableStatement;
  }

  @Override
  protected CallableStatement delegate() {
    return delegate;
  }

  @Override
  public EdbConnectionAdapter getConnectionAdapter() {
    return connectionAdapter;
  }

  @Override
  public <S extends DbStruct> S getStruct(int index, Class<? extends DbStruct> type) throws SQLException, IllegalAccessException,
      InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
    Struct struct = (Struct) delegate().getObject(index);

    return getStruct(type, struct.getAttributes());

  }

  @Override
  public <A extends DbArray<?>> A getArray(int index, Class<? extends DbArray<?>> type) throws SQLException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    Array array = (Array) delegate().getObject(index);

    return getArray(type, (Object[]) array.getArray());
  }

  @Override
  public <S extends DbStruct> void setStruct(int index, S struct) throws SQLException {
    Struct newStruct = getConnectionAdapter().createStruct(struct.getSQLTypeName(), struct.getAttributes());
    delegate().setObject(index, newStruct);
  }

  @Override
  public <A> void setArray(int index, DbArray<A> array) throws SQLException {
    Array newArray = getConnectionAdapter().createArrayOf(array.getSQLTypeName(), array.toArray());
    delegate().setObject(index, newArray, Types.OTHER);
  }

  private void registerParameterWithCorrectSearchPath(
      int parameterIndex,
      int sqlType,
      String typeName,
      EdbCallableStatementAdapter.EdbSearchPathParameterRegistrar creator) throws SQLException {
    String searchPath = getConnectionAdapter().getSearchPath();
    String name = getConnectionAdapter().setSearchPathToSchema(typeName);
    // noinspection unchecked
    creator.register(parameterIndex, sqlType, name.toLowerCase());
    getConnectionAdapter().setSearchPath(searchPath);
  }

  /**
   * Registers the designated output parameter. This version of the method <code>registerOutParameter</code> should be used for a
   * user-defined or <code>REF</code> output parameter. Examples of user-defined types include: <code>STRUCT</code>,
   * <code>DISTINCT</code>, <code>JAVA_OBJECT</code>, and named array types.
   * <p>
   * All OUT parameters must be registered before a stored procedure is executed.
   * <p>
   * For a user-defined parameter, the fully-qualified SQL type name of the parameter should also be given, while a
   * <code>REF</code> parameter requires that the fully-qualified type name of the referenced type be given. A JDBC driver that
   * does not need the type code and type name information may ignore it. To be portable, however, applications should always
   * provide these values for user-defined and <code>REF</code> parameters.
   * <p>
   * Although it is intended for user-defined and <code>REF</code> parameters, this method may be used to register a parameter of
   * any JDBC type. If the parameter does not have a user-defined or <code>REF</code> type, the <i>typeName</i> parameter is
   * ignored.
   * <p>
   * <P>
   * <B>Note:</B> When reading the value of an out parameter, you must use the getter method whose Java type corresponds to the
   * parameter's registered SQL type.
   *
   * @param parameterIndex
   *          the first parameter is 1, the second is 2,...
   * @param sqlType
   *          a value from {@link Types}
   * @param typeName
   *          the fully-qualified name of an SQL structured type
   * @throws SQLException
   *           if the parameterIndex is not valid; if a database access error occurs or this method is called on a closed
   *           <code>CallableStatement</code>
   * @throws SQLFeatureNotSupportedException
   *           if <code>sqlType</code> is a <code>ARRAY</code>, <code>BLOB</code>, <code>CLOB</code>, <code>DATALINK</code>,
   *           <code>JAVA_OBJECT</code>, <code>NCHAR</code>, <code>NCLOB</code>, <code>NVARCHAR</code>, <code>LONGNVARCHAR</code>,
   *           <code>REF</code>, <code>ROWID</code>, <code>SQLXML</code> or <code>STRUCT</code> data type and the JDBC driver does
   *           not support this data type
   * @see Types
   * @since 1.2
   */
  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
    registerParameterWithCorrectSearchPath(parameterIndex, sqlType, typeName, delegate()::registerOutParameter);
  }

  /**
   * Registers the OUT parameter named <code>parameterName</code> to the JDBC type <code>sqlType</code>. All OUT parameters must
   * be registered before a stored procedure is executed.
   * <p>
   * The JDBC type specified by <code>sqlType</code> for an OUT parameter determines the Java type that must be used in the
   * <code>get</code> method to read the value of that parameter.
   * <p>
   * If the JDBC type expected to be returned to this output parameter is specific to this particular database,
   * <code>sqlType</code> should be <code>java.sql.Types.OTHER</code>. The method {@link #getObject} retrieves the value.
   *
   * @param parameterName
   *          the name of the parameter
   * @param sqlType
   *          the JDBC type code defined by <code>java.sql.Types</code>. If the parameter is of JDBC type <code>NUMERIC</code> or
   *          <code>DECIMAL</code>, the version of <code>registerOutParameter</code> that accepts a scale value should be used.
   * @throws SQLException
   *           if parameterName does not correspond to a named parameter; if a database access error occurs or this method is
   *           called on a closed <code>CallableStatement</code>
   * @throws SQLFeatureNotSupportedException
   *           if <code>sqlType</code> is a <code>ARRAY</code>, <code>BLOB</code>, <code>CLOB</code>, <code>DATALINK</code>,
   *           <code>JAVA_OBJECT</code>, <code>NCHAR</code>, <code>NCLOB</code>, <code>NVARCHAR</code>, <code>LONGNVARCHAR</code>,
   *           <code>REF</code>, <code>ROWID</code>, <code>SQLXML</code> or <code>STRUCT</code> data type and the JDBC driver does
   *           not support this data type or if the JDBC driver does not support this method
   * @see Types
   * @since 1.4
   */
  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    delegate().registerOutParameter(parameterName, sqlType);
  }

  /**
   * Registers the OUT parameter in ordinal position <code>parameterIndex</code> to the JDBC type <code>sqlType</code>. All OUT
   * parameters must be registered before a stored procedure is executed.
   * <p>
   * The JDBC type specified by <code>sqlType</code> for an OUT parameter determines the Java type that must be used in the
   * <code>get</code> method to read the value of that parameter.
   * <p>
   * If the JDBC type expected to be returned to this output parameter is specific to this particular database,
   * <code>sqlType</code> should be <code>java.sql.Types.OTHER</code>. The method {@link #getObject} retrieves the value.
   *
   * @param parameterIndex
   *          the first parameter is 1, the second is 2, and so on
   * @param sqlType
   *          the JDBC type code defined by <code>java.sql.Types</code>. If the parameter is of JDBC type <code>NUMERIC</code> or
   *          <code>DECIMAL</code>, the version of <code>registerOutParameter</code> that accepts a scale value should be used.
   * @throws SQLException
   *           if the parameterIndex is not valid; if a database access error occurs or this method is called on a closed
   *           <code>CallableStatement</code>
   * @throws SQLFeatureNotSupportedException
   *           if <code>sqlType</code> is a <code>ARRAY</code>, <code>BLOB</code>, <code>CLOB</code>, <code>DATALINK</code>,
   *           <code>JAVA_OBJECT</code>, <code>NCHAR</code>, <code>NCLOB</code>, <code>NVARCHAR</code>, <code>LONGNVARCHAR</code>,
   *           <code>REF</code>, <code>ROWID</code>, <code>SQLXML</code> or <code>STRUCT</code> data type and the JDBC driver does
   *           not support this data type
   * @see Types
   */
  @Override
  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    switch (sqlType) {
      case Types.REF_CURSOR:
        delegate().registerOutParameter(parameterIndex, REF);
        break;
      default:
        delegate().registerOutParameter(parameterIndex, sqlType);
        break;
    }
  }

  private interface EdbSearchPathParameterRegistrar {
    void register(int parameterIndex, int sqlType, String typeName) throws SQLException;
  }
}
