package com.terradatum.jdbc.converters;

import java.math.BigInteger;

/**
 * @author rbellamy@terradatum.com
 * @date 2/1/16
 */
public class StringBigIntegerConverter extends AbstractConverter<String, BigInteger> {
  @Override
  protected BigInteger doForward(String s) {
    return new BigInteger(s);
  }

  @Override
  protected String doBackward(BigInteger bigInteger) {
    return String.valueOf(bigInteger);
  }
}
