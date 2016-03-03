package com.terradatum.jdbc.db;

import com.terradatum.jdbc.AbstractAdapterTest;
import com.terradatum.jdbc.DbConnectionAdapter;
import com.terradatum.jdbc.DbPreparedStatementAdapter;
import com.terradatum.jdbc.JdbcConnectionAdapterFactory;
import com.terradatum.jdbc.db.model.NumberTbl;
import oracle.jdbc.OracleConnection;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 2/8/16
 */
public class NumberTblTest extends AbstractAdapterTest {
  public static final Object[] NUMBERS = new Object[] { BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3),
      BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(10) };

  protected NumberTbl getNumbersViaStatement(Connection connection, String searchPath)
      throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
    String commandText = "select terradatum.number_tbl(1,2,3,4,5,10)";
    if (OracleConnection.class.isAssignableFrom(connection.getClass())) {
      commandText = commandText + " from dual";
    }
    DbConnectionAdapter connectionAdapter = JdbcConnectionAdapterFactory.create(connection, searchPath);
    DbPreparedStatementAdapter preparedStatementAdapter = connectionAdapter.prepareStatementAdapter(commandText);
    ResultSet rs = preparedStatementAdapter.executeQuery();
    NumberTbl numberTbl = null;
    if (rs.next()) {
      Array numberTblObject = (Array) rs.getObject(1);
      numberTbl = new NumberTbl();
      numberTbl.setArray(numberTblObject);
    }
    return numberTbl;
  }
}
