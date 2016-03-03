package com.terradatum.jdbc.converters;

import java.math.BigDecimal;

/**
 * @author rbellamy@terradatum.com
 * @date 2/1/16
 */
public class StringBigDecimalConverter extends AbstractConverter<String, BigDecimal> {

  @Override
  protected BigDecimal doForward(String s) {
    return new BigDecimal(s);
  }

  @Override
  protected String doBackward(BigDecimal bigDecimal) {
    return String.valueOf(bigDecimal);
  }
}
