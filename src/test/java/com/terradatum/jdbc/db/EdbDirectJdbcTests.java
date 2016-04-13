package com.terradatum.jdbc.db;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author rbellamy@terradatum.com
 * @date 4/12/16
 */
public class EdbDirectJdbcTests extends DirectJdbcTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(EdbDirectJdbcTests.class);
  private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

  @Before
  public void prepare() throws Exception {
    assert EDB_DATA_SOURCE != null : "Datasource cannot be null";
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(EDB_DATA_SOURCE), super.setupDb());
    LOGGER.debug(dbSetupTracker.toString());
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Test
  public void canGetParentByNameUsingDirectJdbc() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    Struct parentStruct = getParentStructByNameUsingDirectJdbc(getEdbConnection());
    Assert.assertNotNull(parentStruct);
    Assert.assertTrue("The attribute is not an Struct", Struct.class.isAssignableFrom(parentStruct.getAttributes()[2].getClass()));
    Assert.assertTrue("The attribute is not an Array", Array.class.isAssignableFrom(parentStruct.getAttributes()[6].getClass()));
  }

  @Test
  public void getChildCountByParentUsingDirectJdbcWithOverload() throws Exception {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountByParentUsingDirectJdbcWithOverload(getEdbConnection());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }

  @Test
  public void canInsertNullIntegerInPreparedStatement() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    insertNullIntegerInPreparedStatement(getEdbConnection());
  }

  @Test
  public void canCreateObjectTypeAndBodyWithStatement() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    createObjectTypeAndBodyWithStatement(getEdbConnection());
  }

}
