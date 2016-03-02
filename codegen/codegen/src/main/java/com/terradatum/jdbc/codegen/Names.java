package com.terradatum.jdbc.codegen;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import javafx.util.Pair;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class Names {

  public static String toLowerCamelCase(String value) {
    if (!Strings.isNullOrEmpty(value)) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value);
    }
    return null;
  }

  public static String toUpperCamelCase(String value) {
    if (!Strings.isNullOrEmpty(value)) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value);
    }
    return null;
  }

  public static Pair<String, String> getSchemaAndName(String typeName) {
    String name = typeName;
    String schema = null;
    if (!Strings.isNullOrEmpty(typeName)) {
      int dotIndex = typeName.indexOf('.');
      if (dotIndex != -1) {
        schema = typeName.substring(0, dotIndex);
        name = typeName.substring(dotIndex + 1);
      }
    }

    return new Pair<>(schema, name);
  }
}
