package com.terradatum.jdbc.db;

import com.terradatum.jdbc.AbstractAdapterTest;
import com.terradatum.jdbc.DbCallableStatementAdapter;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.db.model.SysParmsTbl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static com.terradatum.jdbc.JdbcConnectionAdapterFactory.create;
import static com.terradatum.jdbc.db.model.SysParmsTbl.SQL_TYPE_NAME;
import static java.sql.Date.valueOf;
import static java.sql.Types.ARRAY;

/**
 * @author rbellamy@terradatum.com
 * @date 3/7/16
 */
public class SysParmsTest extends AbstractAdapterTest {

  protected SysParmsTbl getSysParmsByDate(Connection connection, String searchPath) throws SQLException,
      InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
    String commandText = "{? = call definitions_pkg.get_sys_parms(?)}";
    Date searchDate = valueOf("2006-02-28");

    DbConnectionAdapter dbConnectionAdapter = create(connection, searchPath);
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, ARRAY, SQL_TYPE_NAME);
    dbCallableStatementAdapter.setDate(2, searchDate);
    dbCallableStatementAdapter.execute();
    SysParmsTbl sysParmsTbl = dbCallableStatementAdapter.getArray(1, SysParmsTbl.class);
    return sysParmsTbl;
  }
}
