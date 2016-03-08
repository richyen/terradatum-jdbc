package com.terradatum.jdbc.db;

import com.terradatum.jdbc.db.model.SysParmsTbl;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 3/7/16
 */
public class EdbSysParmsTest extends SysParmsTest {
  @Test
  public void canGetSysParmsViaCallableStatement() throws NoSuchMethodException, InstantiationException, SQLException,
      IllegalAccessException, InvocationTargetException, NoSuchFieldException {
    SysParmsTbl sysParmsTbl = getSysParmsByDate(getEdbConnection(), searchPath);
    Assert.assertNotNull(sysParmsTbl);
  }
}
