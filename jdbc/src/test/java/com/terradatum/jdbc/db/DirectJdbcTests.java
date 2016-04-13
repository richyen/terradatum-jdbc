package com.terradatum.jdbc.db;

import oracle.jdbc.driver.OracleConnection;

import java.math.BigDecimal;
import java.sql.*;

import static java.sql.Types.NULL;

/**
 * @author rbellamy@terradatum.com
 * @date 4/12/16
 */
public class DirectJdbcTests extends AbstractDbTest {

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

  public void insertNullIntegerInPreparedStatement(Connection connection) throws SQLException {
    String sql = "insert into " +
        "jdbc_test.child (parent_id, child_name, child_age) " +
        "values (?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setLong(1, 11L);
    preparedStatement.setString(2, "Eeyore");
    preparedStatement.setInt(3, NULL);
  }

  public void createObjectTypeAndBodyWithStatement(Connection connection) throws SQLException {
    String commandText = "CREATE OR REPLACE TYPE fact_row IS OBJECT (\n" +
        "    FACT_TIME      TIMESTAMP,\n" +
        "    DIMENSION_VALUE VARCHAR(4000),\n" +
        "    MEASURE_VALUE   NUMBER,\n" +
        "    MEMBER PROCEDURE display_fact_row (SELF IN OUT fact_row)\n" +
        "  );\n" +
        "/\n" +
        "\n" +
        "CREATE OR REPLACE TYPE BODY fact_row AS\n" +
        "  MEMBER PROCEDURE display_fact_row (SELF IN OUT fact_row) IS\n" +
        "  BEGIN\n" +
        "   DBMS_OUTPUT.PUT_LINE('FACT_TIME       :' || FACT_TIME);\n" +
        "   DBMS_OUTPUT.PUT_LINE('DIMENSION_VALUE :' || FACT_TIME);\n" +
        "   DBMS_OUTPUT.PUT_LINE('MEASURE_VALUE   :' || FACT_TIME);\n" +
        "  END;\n" +
        "END;\n" +
        "/";
    String[] statements = commandText.split("/");
    Statement statement = connection.createStatement();
    statement.execute(statements[0]);
    statement.execute(statements[1]);
  }
}
