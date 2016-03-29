package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class EdbCallableStatementTest extends AbstractAdapterTest {

  @Test
  public void edbCallableStatementIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      DbCallableStatementAdapter callableStatementAdapter = dbConnectionAdapter.prepareCallAdapter("{? = call some_test()}");
      Assert.assertTrue("Not an EDB callable statement",
          EdbCallableStatementAdapter.class.isAssignableFrom(callableStatementAdapter.getClass()));
    }
  }
}
