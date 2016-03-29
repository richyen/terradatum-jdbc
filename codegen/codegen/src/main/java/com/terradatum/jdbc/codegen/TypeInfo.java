package com.terradatum.jdbc.codegen;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class TypeInfo {
  private String typeName;
  private String elementTypeName;
  private boolean isElementComposite;
  private char edbArrayDelimiter;
  private List<AttributeInfo> attributeInfos = new ArrayList<>();

  public String getSchemaName() {
    return Names.toLowerCamelCase(Names.getSchemaAndName(typeName).getKey());
  }

  public String getClassName() {
    return Names.toUpperCamelCase(Names.getSchemaAndName(typeName).getValue());
  }

  public String getElementClassName() {
    return Names.toUpperCamelCase(Names.getSchemaAndName(elementTypeName).getValue());
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getElementTypeName() {
    return elementTypeName;
  }

  public void setElementTypeName(String elementTypeName) {
    this.elementTypeName = elementTypeName;
  }

  public List<AttributeInfo> getAttributeInfos() {
    return attributeInfos;
  }

  public void setAttributeInfos(List<AttributeInfo> attributeInfos) {
    this.attributeInfos = attributeInfos;
  }

  public boolean isElementComposite() {
    return isElementComposite;
  }

  public void setElementComposite(boolean elementComposite) {
    this.isElementComposite = elementComposite;
  }

  public char getEdbArrayDelimiter() {
    return edbArrayDelimiter;
  }

  public void setEdbArrayDelimiter(char delimiter) {
    this.edbArrayDelimiter = delimiter;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("typeName", typeName)
                      .add("elementTypeName", elementTypeName)
                      .add("attributeInfos", attributeInfos)
                      .add("schemaName", getSchemaName())
                      .add("className", getClassName())
                      .add("elementName", getElementClassName())
                      .add("elementComposite", isElementComposite()).toString();
  }
}
