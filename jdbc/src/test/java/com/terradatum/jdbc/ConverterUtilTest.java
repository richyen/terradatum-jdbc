package com.terradatum.jdbc;

import com.terradatum.jdbc.converters.ConverterUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author rbellamy@terradatum.com
 * @date 2/4/16
 */
public class ConverterUtilTest {

  @Test
  public void canConvertFromStringToBigDecimal() {
    String bigDecimalString10 = "1.0";
    String bigDecimalStringPi = "3.14159265359";
    BigDecimal bigDecimal10 = ConverterUtil.convert(bigDecimalString10, BigDecimal.class);
    Assert.assertEquals("Incorrect String to BigDecimal conversion", BigDecimal.valueOf(1.0), bigDecimal10);
    BigDecimal bigDecimalPi = ConverterUtil.convert(bigDecimalStringPi, BigDecimal.class);
    Assert.assertEquals("Incorrect String to BigDecimal conversion", BigDecimal.valueOf(3.14159265359), bigDecimalPi);
  }

  @Test
  @Ignore("There is a problem in the code used to reverse the Converter. Even though the implementation supports such\n"
      + "reversal, for some reason the compiler chokes when using generic types.")
  public void canConvertFromBigDecimalToString() {
    BigDecimal bigDecimal10 = BigDecimal.valueOf(1.0);
    BigDecimal bigDecimalPi = BigDecimal.valueOf(3.14159265359);
    String bigDecimalString10 = ConverterUtil.convert(bigDecimal10, String.class);
    Assert.assertEquals("Incorrect BigDecimal to String conversion", "1.0", bigDecimalString10);
    String bigDecimalStringPi = ConverterUtil.convert(bigDecimalPi, String.class);
    Assert.assertEquals("Incorrect BigDecimal to String conversion", "3.14159265359", bigDecimalStringPi);
  }
}
