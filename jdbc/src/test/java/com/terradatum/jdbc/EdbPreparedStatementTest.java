package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class EdbPreparedStatementTest extends AbstractAdapterTest {

  @Test
  public void edbPreparedStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      DbPreparedStatementAdapter preparedStatementAdapter = dbConnectionAdapter.prepareStatementAdapter("select count(1);");
      Assert.assertTrue("Not an EDB prepared statement",
          EdbPreparedStatementAdapter.class.isAssignableFrom(preparedStatementAdapter.getClass()));
    }
  }
}
