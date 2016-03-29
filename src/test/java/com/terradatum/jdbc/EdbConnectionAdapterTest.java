package com.terradatum.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Struct;

import static org.hamcrest.Matchers.*;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public class EdbConnectionAdapterTest extends AbstractAdapterTest {

  @Test
  public void edbConnectionAdapterIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      Assert.assertTrue("Not a Jdbc4Connection", EdbConnectionAdapter.class.isAssignableFrom(dbConnectionAdapter.getClass()));
    }
  }

  @Test
  public void canCreateEdbStruct() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      Object[] attributes = { new BigDecimal(1), "ABC123" };
      Struct childStruct = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes);
      Assert.assertTrue("Not an Java Sql Struct", Struct.class.isAssignableFrom(childStruct.getClass()));
      // NOTE: EDB uses unqualified type names
      Assert.assertEquals("Invalid SqlTypeName", "child_obj", childStruct.getSQLTypeName());
      Assert.assertArrayEquals("Invalid attributes", attributes, childStruct.getAttributes());
    }
  }

  @Test
  public void canCreateEdbArray() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      Object[] attributes1 = { new BigDecimal(1), "ABC123" };
      Object[] attributes2 = { new BigDecimal(2), "456DEF" };
      Struct childStruct1 = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes1);
      Struct childStruct2 = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes2);
      Struct[] elements = { childStruct1, childStruct2 };
      Array idArray = dbConnectionAdapter.createArrayOf("jdbc_test.child_tbl", elements);
      Assert.assertTrue("Not a Java Sql Array", Array.class.isAssignableFrom(idArray.getClass()));
      // NOTE: EDB uses unqualified type names
      Assert.assertEquals("Invalid SqlTypeName", "child_obj", idArray.getBaseTypeName());
      Assert.assertThat("Invalid elements", elements,
          is(arrayContainingInAnyOrder(equalTo(childStruct1), equalTo(childStruct2))));
    }
  }
}
