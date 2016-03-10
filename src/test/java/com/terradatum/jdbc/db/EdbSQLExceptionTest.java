package com.terradatum.jdbc.db;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * @author rbellamy@terradatum.com
 * @date 2/18/16
 */
public class EdbSQLExceptionTest extends SQLExceptionTest {
  private static final Logger logger = LoggerFactory.getLogger(EdbSQLExceptionTest.class);

  @Test
  public void canGetCustomSqlError() {
    try {
      throwSqlException(getEdbConnection(), searchPath);
    } catch (SQLException e) {
      logger.debug("SQLCode: " + e.getErrorCode());
      logger.debug("SQLState: " + e.getSQLState());
      logger.debug("Message: " + e.getMessage());
      logger.debug(e.getMessage(), e);
      assertTrue("Not a valid custom exception", dbConnectionAdapter.isCustomException(e));
    }
  }
}
