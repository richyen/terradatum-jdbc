package com.terradatum.jdbc.db;

import com.terradatum.jdbc.DbCallableStatementAdapter;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.db.objects.jdbcTest.ChildObj;
import com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl;
import com.terradatum.jdbc.db.objects.jdbcTest.ParentObj;
import oracle.jdbc.driver.OracleConnection;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;

/**
 * @author rbellamy@terradatum.com
 * @date 3/27/16
 */
public class FunctionCallTests extends AbstractDbTest {

  public BigDecimal getChildCountAsBigDecimalByParentId(DbConnectionAdapter dbConnectionAdapter) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_by_parent(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.NUMERIC);
    dbCallableStatementAdapter.setNumeric(2, 1);
    dbCallableStatementAdapter.execute();
    BigDecimal ret = dbCallableStatementAdapter.getBigDecimal(1);
    return ret;
  }

  public int getChildCountAsIntegerByParentId(DbConnectionAdapter dbConnectionAdapter) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_as_int(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.INTEGER);
    dbCallableStatementAdapter.setInt(2, 1);
    dbCallableStatementAdapter.execute();
    int ret = dbCallableStatementAdapter.getInt(1);
    return ret;
  }

  public BigDecimal getChildCountAsBigDecimalWhenReturnIsIntegerByParentId(DbConnectionAdapter dbConnectionAdapter) throws SQLException {
    String commandText = "{? = call parent_child_pkg.get_child_count_as_int(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.INTEGER);
    dbCallableStatementAdapter.setInt(2, 1);
    dbCallableStatementAdapter.execute();
    BigDecimal ret = dbCallableStatementAdapter.getBigDecimal(1);
    return ret;
  }

  public ChildObj getChildByName(DbConnectionAdapter dbConnectionAdapter) throws SQLException, InvocationTargetException,
      NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
    String commandText = "{? = call parent_child_pkg.get_child_by_name(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.STRUCT, ChildObj.SQL_TYPE_NAME);
    dbCallableStatementAdapter.setString(2, "Piglet");
    dbCallableStatementAdapter.execute();
    ChildObj ret = dbCallableStatementAdapter.getStruct(1, ChildObj.class);
    return ret;
  }

  public ChildTbl getChildrenByParentId(DbConnectionAdapter dbConnectionAdapter) throws SQLException, InvocationTargetException,
      NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
    String commandText = "{? = call parent_child_pkg.get_children_by_parent_id(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.ARRAY, ChildTbl.SQL_TYPE_NAME);
    dbCallableStatementAdapter.setNumeric(2, 1);
    dbCallableStatementAdapter.execute();
    ChildTbl ret = dbCallableStatementAdapter.getArray(1, ChildTbl.class);
    return ret;
  }

  public ParentObj getParentByName(DbConnectionAdapter dbConnectionAdapter) throws SQLException, InvocationTargetException,
      NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
    String commandText = "{? = call parent_child_pkg.get_parent_by_name(?)}";
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, Types.STRUCT, ParentObj.SQL_TYPE_NAME);
    dbCallableStatementAdapter.setString(2, "Caesar");
    dbCallableStatementAdapter.execute();
    ParentObj ret = dbCallableStatementAdapter.getStruct(1, ParentObj.class);
    return ret;
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
}
