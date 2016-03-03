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
      Object[] attributes = { "ABC123", new BigDecimal(1) };
      Struct agentIdStruct = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes);
      Assert.assertTrue("Not an Java Sql Struct", Struct.class.isAssignableFrom(agentIdStruct.getClass()));
      // NOTE: EDB uses unqualified type names
      Assert.assertEquals("Invalid SqlTypeName", "agent_id_obj", agentIdStruct.getSQLTypeName());
      Assert.assertArrayEquals("Invalid attributes", attributes, agentIdStruct.getAttributes());
    }
  }

  @Test
  public void canCreateEdbArray() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath)) {
      Object[] attributes1 = { "ABC123", new BigDecimal(1) };
      Object[] attributes2 = { "456DEF", new BigDecimal(2) };
      Struct agentIdStruct1 = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes1);
      Struct agentIdStruct2 = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes2);
      Struct[] elements = { agentIdStruct1, agentIdStruct2 };
      Array agentIdArray = dbConnectionAdapter.createArrayOf("metrics.agent_id_tbl", elements);
      Assert.assertTrue("Not a Java Sql Array", Array.class.isAssignableFrom(agentIdArray.getClass()));
      // NOTE: EDB uses unqualified type names
      Assert.assertEquals("Invalid SqlTypeName", "agent_id_tbl", agentIdArray.getBaseTypeName());
      Assert.assertThat("Invalid elements", elements,
          is(arrayContainingInAnyOrder(equalTo(agentIdStruct1), equalTo(agentIdStruct2))));
    }
  }
}
