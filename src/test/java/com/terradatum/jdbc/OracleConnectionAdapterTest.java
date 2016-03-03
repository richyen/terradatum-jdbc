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
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection(), null)) {
      // jdbcConnectionManager.getConnection().getClass returns the package-private T4CConnection class
      // This means the normal type == comparison will not work.
      Assert.assertTrue("Not an OracleConnection",
          OracleConnectionAdapter.class.isAssignableFrom(dbConnectionAdapter.getClass()));
    }
  }

  @Test
  public void canCreateOracleStruct() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection(), null)) {
      Object[] attributes = {"ABC123", new BigDecimal(1)};
      Struct agentIdStruct = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes);
      Assert.assertTrue("Not an Oracle STRUCT", oracle.sql.STRUCT.class.isAssignableFrom(agentIdStruct.getClass()));
      // NOTE: Oracle uses uppercase SQL type names
      Assert.assertEquals("Invalid SqlTypeName", "METRICS.AGENT_ID_OBJ", agentIdStruct.getSQLTypeName());
      Assert.assertArrayEquals("Invalid attributes", attributes, agentIdStruct.getAttributes());
    }
  }

  @Test
  public void canCreateOracleArray() throws Exception {
    try (DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(getOracleConnection(), null)) {
      Object[] attributes1 = {"ABC123", new BigDecimal(1)};
      Object[] attributes2 = {"456DEF", new BigDecimal(2)};
      Struct agentIdStruct1 = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes1);
      Struct agentIdStruct2 = dbConnectionAdapter.createStruct("metrics.agent_id_obj", attributes2);
      Struct[] elements = {agentIdStruct1, agentIdStruct2};
      Array agentIdArray = dbConnectionAdapter.createArrayOf("metrics.agent_id_tbl", elements);
      Assert.assertTrue("Not an Oracle ARRAY", oracle.sql.ARRAY.class.isAssignableFrom(agentIdArray.getClass()));
      // NOTE: Oracle uses uppercase SQL type names
      Assert.assertEquals("Invalid SqlTypeName", "METRICS.AGENT_ID_OBJ", agentIdArray.getBaseTypeName());
      Assert.assertThat("Invalid elements", elements,
          is(arrayContainingInAnyOrder(equalTo(agentIdStruct1), equalTo(agentIdStruct2))));
    }
  }

}
