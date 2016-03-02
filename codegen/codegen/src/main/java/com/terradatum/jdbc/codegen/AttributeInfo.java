package com.terradatum.jdbc.codegen;

import com.google.common.base.MoreObjects;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class AttributeInfo {
  private boolean isStruct = false;
  private boolean isArray = false;
  private boolean isString = false;
  private boolean isDifferentSchema = false;
  private String typeName;
  private String name;

  public boolean isStruct() {
    return isStruct;
  }

  public void setStruct(boolean struct) {
    this.isStruct = struct;
  }

  public boolean isArray() {
    return isArray;
  }

  public void setArray(boolean array) {
    this.isArray = array;
  }

  public boolean isString() {
    return isString;
  }

  public void setString(boolean string) {
    this.isString = string;
  }

  public boolean isDifferentSchema() {
    return isDifferentSchema;
  }

  public void setDifferentSchema(boolean isDifferentSchema) {
    this.isDifferentSchema = isDifferentSchema;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSchemaName() {
    return Names.toLowerCamelCase(Names.getSchemaAndName(typeName).getKey());
  }

  public String getClassName() {
    return Names.toUpperCamelCase(Names.getSchemaAndName(typeName).getValue());
  }

  public String getPropertyName() {
    return Names.toLowerCamelCase(name);
  }

  public String getAccessorName() {
    return Names.toUpperCamelCase(name);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("isStruct", isStruct).add("isArray", isArray).add("isString", isString)
        .add("isDifferentSchema", isDifferentSchema).add("typeName", typeName).add("name", name).add("struct", isStruct())
        .add("array", isArray()).add("string", isString()).add("differentSchema", isDifferentSchema())
        .add("schemaName", getSchemaName()).add("className", getClassName()).add("propertyName", getPropertyName())
        .add("accessorName", getAccessorName()).toString();
  }
}
