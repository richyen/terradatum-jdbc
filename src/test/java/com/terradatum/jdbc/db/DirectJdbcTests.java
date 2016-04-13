package com.terradatum.jdbc.db;

import oracle.jdbc.driver.OracleConnection;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author rbellamy@terradatum.com
 * @date 4/12/16
 */
public class DirectJdbcTests extends AbstractDbTest {

  public void canInsertNullIntegerInPreparedStatement(Connection connection) throws SQLException {
    String sql = "insert into " +
        "jdbc_test.child (parent_id, child_name, child_age) " +
        "values (?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setLong(1, 11L);
    preparedStatement.setString(2, "Eeyore");
    preparedStatement.setInt(3, null);
  }

  public Struct getParentStructByNameUsingDirectJdbc(Connection connection) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_parent_by_name(?)}";
    String typeName;
    if (OracleConnection.class.isAssignableFrom(connection.getClass()) ||
        connection.isWrapperFor(OracleConnection.class)) {
      typeName = "JDBC_TEST.PARENT_OBJ";
    } else {
      typeName = "parent_obj";
    }
    CallableStatement callableStatement = connection.prepareCall(commandText);
    callableStatement.registerOutParameter(1, Types.STRUCT, typeName);
    callableStatement.setString(2, "Caesar");
    callableStatement.execute();
    Struct ret = (Struct) callableStatement.getObject(1);
    return ret;
  }


  public int getChildCountByParentUsingDirectJdbcPreparedStatement(Connection connection) throws SQLException {
    String commandText = "SELECT parent_child_pkg.get_child_count_by_parent_obj(?)";
    String typeName;
    if (OracleConnection.class.isAssignableFrom(connection.getClass()) ||
        connection.isWrapperFor(OracleConnection.class)) {
      typeName = "JDBC_TEST.PARENT_OBJ";
    } else {
      typeName = "parent_obj";
    }
    Struct parent = connection.createStruct(typeName, new Object[]{
        BigDecimal.ONE, "Caesar", null, null, null, null, null
    });
    PreparedStatement preparedStatement = connection.prepareStatement(commandText);

    preparedStatement.setObject(1, parent);
    ResultSet resultSet = preparedStatement.executeQuery();
    BigDecimal ret = BigDecimal.ZERO;
    if (resultSet.next()) {
      ret = resultSet.getBigDecimal(1);
    }
    return ret.intValue();
  }

  public int getChildCountByParentUsingDirectJdbcWithOverload(Connection connection) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_by_parent(?)}";
    String typeName;
    if (OracleConnection.class.isAssignableFrom(connection.getClass()) ||
        connection.isWrapperFor(OracleConnection.class)) {
      typeName = "JDBC_TEST.PARENT_OBJ";
    } else {
      typeName = "parent_obj";
    }
    Struct parent = connection.createStruct(typeName, new Object[]{
        BigDecimal.ONE, "Caesar", null, null, null, null, null
    });
    CallableStatement callableStatement = connection.prepareCall(commandText);
    callableStatement.registerOutParameter(1, Types.NUMERIC);
    callableStatement.setObject(2, parent);
    callableStatement.execute();
    BigDecimal ret = callableStatement.getBigDecimal(1);
    return ret.intValue();
  }
}
