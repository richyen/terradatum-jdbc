package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class OracleStatementTest extends AbstractAdapterTest {

  @Test
  public void oraclePreparedStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection())) {
      DbStatementAdapter statementAdapter = dbConnectionAdapter.createStatementAdapter();
      Assert.assertTrue("Not an OraclePreparedStatement",
          OracleStatementAdapter.class.isAssignableFrom(statementAdapter.getClass()));
    }
  }

}
