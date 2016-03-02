package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class EdbStatementTest extends AbstractAdapterTest {

  @Test
  public void edbPreparedStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      DbStatementAdapter statementAdapter = dbConnectionAdapter.createStatementAdapter();
      Assert.assertTrue("Not an EDB prepared statement", EdbStatementAdapter.class.isAssignableFrom(statementAdapter.getClass()));
    }
  }
}
