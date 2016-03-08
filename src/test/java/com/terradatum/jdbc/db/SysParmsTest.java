package com.terradatum.jdbc.db;

import com.terradatum.jdbc.AbstractAdapterTest;
import com.terradatum.jdbc.DbCallableStatementAdapter;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.JdbcConnectionAdapterFactory;
import com.terradatum.jdbc.db.model.SysParmsTbl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 3/7/16
 */
public class SysParmsTest extends AbstractAdapterTest {

  protected SysParmsTbl getSysParmsByDate(Connection connection, String searchPath) throws SQLException,
      InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
    String commandText = "{? = call definitions_pkg.get_sys_parms(?)}";
    java.sql.Date searchDate = java.sql.Date.valueOf("2006-02-28");

    DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(connection, searchPath);
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerArrayOutParameter(1, SysParmsTbl.SQL_TYPE_NAME);
    dbCallableStatementAdapter.setDate(2, searchDate);
    dbCallableStatementAdapter.execute();
    SysParmsTbl sysParmsTbl = dbCallableStatementAdapter.getArray(1, SysParmsTbl.class);
    return sysParmsTbl;
  }
}
