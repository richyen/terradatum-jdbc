package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class OracleCallableStatementTest extends AbstractAdapterTest {

  @Test
  public void oracleCallableStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection(), null)) {
      DbCallableStatementAdapter callableStatementAdapter = dbConnectionAdapter.prepareCallAdapter("{? = call some_test()}");
      Assert.assertTrue("Not an OracleCallableStatementAdapter",
          OracleCallableStatementAdapter.class.isAssignableFrom(callableStatementAdapter.getClass()));
    }
  }

}
