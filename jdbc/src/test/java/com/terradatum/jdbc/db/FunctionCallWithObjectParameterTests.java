package com.terradatum.jdbc.db;

import com.terradatum.jdbc.DbCallableStatementAdapter;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl;
import com.terradatum.jdbc.db.objects.jdbcTest.ParentObj;
import com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj;
import oracle.jdbc.driver.OracleConnection;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author rbellamy@terradatum.com
 * @date 3/26/16
 */
public class FunctionCallWithObjectParameterTests extends AbstractDbTest {

  public static final ParentObj CAESAR_NULL_ATTRIBUTES = new ParentObj(BigDecimal.ONE,
      "Caesar", null, null, null, null, null);

  public static final ParentObj CAESAR_DEFAULT_ATTRIBUTES = new ParentObj(BigDecimal.ONE,
      "Caesar",
      new SubParentObj(BigDecimal.ZERO, null),
      new Timestamp(0),
      new Timestamp(0),
      null,
      new ChildTbl());

  public int getChildCountByParentWithNullAttributes(DbConnectionAdapter dbConnectionAdapter) throws SQLException {
    return getChildCountByParent(dbConnectionAdapter, CAESAR_NULL_ATTRIBUTES);
  }

  public int getChildCountByParentWithDefaultAttributes(DbConnectionAdapter dbConnectionAdapter) throws SQLException {
    return getChildCountByParent(dbConnectionAdapter, CAESAR_DEFAULT_ATTRIBUTES);
  }

  private int getChildCountByParent(DbConnectionAdapter dbConnectionAdapter, ParentObj parentObj) throws
      SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_by_parent_obj(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.NUMERIC);

    dbCallableStatementAdapter.setStruct(2, parentObj);
    dbCallableStatementAdapter.execute();
    BigDecimal ret = dbCallableStatementAdapter.getBigDecimal(1);
    return ret.intValue();
  }

  public int getChildCountByParentWithOverload(DbConnectionAdapter dbConnectionAdapter) throws
      SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_by_parent(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.NUMERIC);

    dbCallableStatementAdapter.setStruct(2, CAESAR_DEFAULT_ATTRIBUTES);
    dbCallableStatementAdapter.execute();
    BigDecimal ret = dbCallableStatementAdapter.getBigDecimal(1);
    return ret.intValue();
  }

  public int getChildCountByParentUsingDirectJdbc(Connection connection) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_by_parent_obj(?)}";
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
