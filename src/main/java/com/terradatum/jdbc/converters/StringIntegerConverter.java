package com.terradatum.jdbc.converters;

/**
 * @author rbellamy@terradatum.com
 * @date 2/1/16
 */
public class StringIntegerConverter extends AbstractConverter<String, Integer> {
  @Override
  protected Integer doForward(String s) {
    return Integer.valueOf(s);
  }

  @Override
  protected String doBackward(Integer integer) {
    return String.valueOf(integer);
  }
}
