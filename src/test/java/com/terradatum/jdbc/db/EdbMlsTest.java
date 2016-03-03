package com.terradatum.jdbc.db;

import com.terradatum.jdbc.db.model.MlsAreaTypeTbl;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public class EdbMlsTest extends MlsTest {

  @Test
  public void canGetMlsAreaTypesViaCallableStatement() throws SQLException, InvocationTargetException, NoSuchMethodException,
      NoSuchFieldException, InstantiationException, IllegalAccessException {
    MlsAreaTypeTbl mlsAreaTypeTbl = getMlsAreaTypesViaCallableStatement(getEdbConnection(), searchPath);
    Assert.assertNotNull(mlsAreaTypeTbl);
  }

  @Test
  public void canGetMlsAreaTypesWithCorrectElementsViaCallableStatement() throws SQLException, InvocationTargetException,
      NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {

    MlsAreaTypeTbl mlsAreaTypeTbl = getMlsAreaTypesViaCallableStatement(getEdbConnection(), searchPath);

    Assert.assertThat(MLS_AREA_TYPE_OBJS, is(mlsAreaTypeTbl.toArray()));
  }

  @Test
  // @Ignore("Oracle and EDB prepared statement syntax with TABLE return types is very different")
  public void canGetMlsAreaTypesViaPreparedStatement() throws SQLException, InvocationTargetException, NoSuchMethodException,
      NoSuchFieldException, InstantiationException, IllegalAccessException {
    MlsAreaTypeTbl mlsAreaTypeTbl = getMlsAreaTypesViaPreparedStatement(getEdbConnection(), searchPath);
    Assert.assertNotNull(mlsAreaTypeTbl);
  }

  @Test
  // @Ignore("Oracle and EDB prepared statement syntax with TABLE return types is very different")
  public void canGetMlsAreaTypesWithCorrectElementsViaPreparedStatement()
      throws SQLException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    MlsAreaTypeTbl mlsAreaTypeTbl = getMlsAreaTypesViaPreparedStatement(getEdbConnection(), searchPath);

    Assert.assertThat(MLS_AREA_TYPE_OBJS, is(mlsAreaTypeTbl.toArray()));
  }

}
