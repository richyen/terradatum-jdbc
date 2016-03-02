package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class OraclePreparedStatementTest extends AbstractAdapterTest {

  @Test
  public void oraclePreparedStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection(), null)) {
      DbPreparedStatementAdapter preparedStatementAdapter = dbConnectionAdapter
          .prepareStatementAdapter("select count(1) from dual;");
      Assert.assertTrue("Not an OraclePreparedStatement",
          OraclePreparedStatementAdapter.class.isAssignableFrom(preparedStatementAdapter.getClass()));
    }
  }

}
