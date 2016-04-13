package com.terradatum.jdbc.db;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 3/26/16
 */
public class OracleFunctionCallWithObjectParameterTests extends FunctionCallWithObjectParameterTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(OracleFunctionCallWithObjectParameterTests.class);
  private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

  @Before
  public void prepare() throws Exception {
    assert ORACLE_DATA_SOURCE != null : "Datasource cannot be null";
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ORACLE_DATA_SOURCE), super.setupDb());
    LOGGER.debug(dbSetupTracker.toString());
    dbSetupTracker.launchIfNecessary(dbSetup);
  }

  @Test
  public void canGetChildCountByParentWithDefaultAttributes() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountByParentWithDefaultAttributes(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }

  @Test
  public void canGetChildCountByParentWithNullAttributes() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountByParentWithNullAttributes(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }

  @Test
  public void canGetChildCountByParentWithOverloadedFunction() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountByParentWithOverload(getOracleConnectionAdapter());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }

  @Test
  public void canGetChildCountByParentUsingDirectJdbcCallableStatement() throws SQLException {
    dbSetupTracker.skipNextLaunch();
    int childCountByParent = getChildCountByParentUsingDirectJdbcCallableStatement(getOracleConnection());
    Assert.assertEquals("Caesar has the wrong number of children", 3, childCountByParent);
  }
}
