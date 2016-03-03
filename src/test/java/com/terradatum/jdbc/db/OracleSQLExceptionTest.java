package com.terradatum.jdbc.db;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 2/18/16
 */
public class OracleSQLExceptionTest extends SQLExceptionTest {

  @Test
  public void canGetCustomSqlError() {
    try {
      throwSqlException(getOracleConnection(), null);
    } catch (SQLException e) {
      System.out.println("SQLCode: " + e.getErrorCode());
      System.out.println("SQLState: " + e.getSQLState());
      System.out.println("Message: " + e.getMessage());
      e.printStackTrace();
      Assert.assertTrue("Not a valid custom exception", dbConnectionAdapter.isCustomException(e));
    }
  }
}
