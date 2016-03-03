package com.terradatum.jdbc.db;

import com.terradatum.jdbc.db.model.NumberTbl;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author rbellamy@terradatum.com
 * @date 2/8/16
 */
public class OracleNumberTest extends NumberTblTest {

  @Test
  public void canGetNumbersViaPreparedStatement()
      throws SQLException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    NumberTbl numberTbl = getNumbersViaStatement(getOracleConnection(), null);

    Assert.assertNotNull(numberTbl);
  }

  @Test
  public void canGetNumbersWithCorrectElementsViaPreparedStatement()
      throws SQLException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    NumberTbl numberTbl = getNumbersViaStatement(getOracleConnection(), null);

    Assert.assertThat(NUMBERS, is(numberTbl.toArray()));
  }
}
