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
public class OracleConnectionAdapterTest extends AbstractAdapterTest {

  @Test
  public void oracleConnectionAdapterIsSelected() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection())) {
      // jdbcConnectionManager.getConnection().getClass returns the package-private T4CConnection class
      // This means the normal type == comparison will not work.
      Assert.assertTrue("Not an OracleConnection",
          OracleConnectionAdapter.class.isAssignableFrom(dbConnectionAdapter.getClass()));
    }
  }

  @Test
  public void canCreateOracleStruct() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection())) {
      Object[] attributes = {new BigDecimal(1), "ABC123"};
      Struct childStruct = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes);
      Assert.assertTrue("Not an Oracle STRUCT", oracle.sql.STRUCT.class.isAssignableFrom(childStruct.getClass()));
      // NOTE: Oracle uses uppercase SQL type names
      Assert.assertEquals("Invalid SqlTypeName", "JDBC_TEST.CHILD_OBJ", childStruct.getSQLTypeName());
      Assert.assertArrayEquals("Invalid attributes", attributes, childStruct.getAttributes());
    }
  }

  @Test
  public void canCreateOracleArray() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection())) {
      Object[] attributes1 = {new BigDecimal(1), "ABC123"};
      Object[] attributes2 = {new BigDecimal(2), "456DEF"};
      Struct childStruct1 = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes1);
      Struct childStruct2 = dbConnectionAdapter.createStruct("jdbc_test.child_obj", attributes2);
      Struct[] elements = {childStruct1, childStruct2};
      Array idArray = dbConnectionAdapter.createArrayOf("jdbc_test.child_tbl", elements);
      Assert.assertTrue("Not an Oracle ARRAY", oracle.sql.ARRAY.class.isAssignableFrom(idArray.getClass()));
      // NOTE: Oracle uses uppercase SQL type names
      Assert.assertEquals("Invalid SqlTypeName", "JDBC_TEST.CHILD_OBJ", idArray.getBaseTypeName());
      Assert.assertThat("Invalid elements", elements,
          is(arrayContainingInAnyOrder(equalTo(childStruct1), equalTo(childStruct2))));
    }
  }

}
