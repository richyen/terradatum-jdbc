package com.terradatum.jdbc.converters;

/**
 * @author rbellamy@terradatum.com
 * @date 2/1/16
 */
public class StringDoubleConverter extends AbstractConverter<String, Double> {
  @Override
  protected Double doForward(String s) {
    return Double.valueOf(s);
  }

  @Override
  protected String doBackward(Double aDouble) {
    return String.valueOf(aDouble);
  }
}
