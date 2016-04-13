package com.terradatum.jdbc.db;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.terradatum.jdbc.db.objects.jdbcTest.ChildObj;
import com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl;
import com.terradatum.jdbc.db.objects.jdbcTest.ParentObj;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author rbellamy@terradatum.com
 * @date 3/27/16
 */
public class OracleFunctionCallTests extends FunctionCallTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(OracleFunctionCallTests.class);
  private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

  @Before
  public void prepare() throws Exception {
    assert ORACLE_DATA_SOURCE != null : "Datasource cannot be null";
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ORACLE_DATA_SOURCE), super.setupDb());
    LOGGER.debug(dbSetupTracker.toString());
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Test
  public void canGetChildCountAsBigDecimalByParentId() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    BigDecimal childCountByParent = getChildCountAsBigDecimalByParentId(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", new BigDecimal(3), childCountByParent);
  }

  @Test
  public void canGetChildCountAsIntegerByParentId() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountAsIntegerByParentId(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }

  @Test
  public void canGetChildCountAsBigDecimalWhenReturnIsIntegerByParentId() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    BigDecimal childCountByParent = getChildCountAsBigDecimalWhenReturnIsIntegerByParentId(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", new BigDecimal(3), childCountByParent);
  }

  @Test
  public void canGetChildByName() throws SQLException, NoSuchMethodException, NoSuchFieldException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    dbSetupTracker.skipNextLaunch();
    Object[] attributes = {new BigDecimal(16), "Piglet"};
    ChildObj childObj = getChildByName(getOracleConnectionAdapter());
    Assert.assertNotNull("Piglet should not be null", childObj);
    Assert.assertArrayEquals("Attribute array is invalid", attributes, childObj.getAttributes());
    Assert.assertTrue("Not a Java Sql Struct", Struct.class.isAssignableFrom(childObj.getClass()));
    Assert.assertEquals("Invalid SQLTypeName", "jdbc_test.child_obj", childObj.getSQLTypeName());
    Assert.assertEquals("The child_obj should be named 'Piglet'", "Piglet", childObj.getChildName());
  }

  @Test
  public void canGetChildrenByParentId() throws SQLException, NoSuchMethodException, NoSuchFieldException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    dbSetupTracker.skipNextLaunch();
    ChildTbl childTbl = getChildrenByParentId(getOracleConnectionAdapter());
    Assert.assertNotNull(childTbl);
  }

  @Test
  public void canGetParentByName() throws SQLException, NoSuchMethodException, NoSuchFieldException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    dbSetupTracker.skipNextLaunch();
    ParentObj parentObj = getParentByName(getOracleConnectionAdapter());
    Assert.assertNotNull(parentObj);
  }
}