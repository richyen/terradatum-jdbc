package com.terradatum.jdbc.converters;

import com.google.common.base.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author rbellamy@terradatum.com
 * @date 2/1/16
 */
public class StringTimestampConverter extends AbstractConverter<String, Timestamp> {

  private DateTimeFormatter formatter;

  @Override
  protected Timestamp doForward(String s) {
    if (this.formatter != null) {
      return Timestamp.valueOf(LocalDateTime.parse(s, formatter));
    }

    return Timestamp.valueOf(s);
  }

  @Override
  protected String doBackward(Timestamp timestamp) {
    if (this.formatter != null) {
      return formatter.format(timestamp.toLocalDateTime());
    }

    return String.valueOf(timestamp);
  }

  public Converter<String, Timestamp> withFormatter(DateTimeFormatter formatter) {
    this.formatter = formatter;
    return this;
  }
}
