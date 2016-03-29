package com.terradatum.jdbc.db.objects.jdbcTest;

import com.terradatum.jdbc.converters.ConverterUtil;
import com.terradatum.jdbc.DbStruct;
import com.google.common.base.MoreObjects;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;
import java.util.Objects;

/**
 * DO NOT MODIFY!!! This class was generated by the Terradatum JDBC Code Generator and will be overwritten if regenerated
 * @date 2016-03-28T19:45:12.424
 */
public class ParentObj implements DbStruct {
  public static final String SQL_TYPE_NAME = "jdbc_test.parent_obj";

  private java.math.BigDecimal parentId;
  private String parentName;
  private com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj subParent;
  private java.sql.Timestamp someDate;
  private java.sql.Timestamp someTimestamp;
  private byte[] image;
  private com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl children;

  /**
   * Required default constructor.
   */
  public ParentObj() {
  }

  /**
   * All-elements, type-safe constructor
   */
  public ParentObj(java.math.BigDecimal parentId, String parentName, com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj subParent, java.sql.Timestamp someDate, java.sql.Timestamp someTimestamp, byte[] image, com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl children) {
    this.parentId = parentId;
    this.parentName = parentName;
    this.subParent = subParent;
    this.someDate = someDate;
    this.someTimestamp = someTimestamp;
    this.image = image;
    this.children = children;

  }

  /**
   * Set the {@link Struct}, which is then parsed and hydrated into the properties of the {@link DbStruct}.
   *
   * There are numerous casts and other shenanigans occurring here, so this method can throw numerous exceptions.
   * This is a better solution than a method that hides all exceptions and prevents the call site from responding
   * accordingly.
   * @param struct the {@link java.sql.Struct} which is used as the model for this {@link DbStruct} object
   * @throws SQLException
   * @throws NoSuchMethodException
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unused")
  public void setStruct(Struct struct) throws SQLException, NoSuchMethodException, NoSuchFieldException,
            IllegalAccessException, InvocationTargetException {
    if (struct != null) {
      setAttributes(struct.getAttributes());
    }
  }

  @Override
  public String getSQLTypeName() throws SQLException {
    return SQL_TYPE_NAME;
  }

  @Override
  public Object[] getAttributes() throws SQLException {
    return new Object[]{parentId, parentName, subParent, someDate, someTimestamp, image, children};
  }

  @Override
  public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException { 
    return getAttributes();
  }

  @Override
  public void setAttributes(Object[] attributes) throws SQLException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
    parentId = ConverterUtil.convert(attributes[0], java.math.BigDecimal.class);
    parentName = ConverterUtil.convert(attributes[1], String.class);
    subParent = new com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj();
    subParent.setStruct((Struct) attributes[2]);
    someDate = ConverterUtil.convert(attributes[3], java.sql.Timestamp.class);
    someTimestamp = ConverterUtil.convert(attributes[4], java.sql.Timestamp.class);
    image = ConverterUtil.convert(attributes[5], byte[].class);
    children = new com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl();
    children.setArray(attributes[6]);
  }

  public java.math.BigDecimal getParentId() {
    return parentId;
  }

  public void setParentId(java.math.BigDecimal parentId) {
    this.parentId = parentId;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj getSubParent() {
    return subParent;
  }

  public void setSubParent(com.terradatum.jdbc.db.objects.jdbcTest.SubParentObj subParent) {
    this.subParent = subParent;
  }

  public java.sql.Timestamp getSomeDate() {
    return someDate;
  }

  public void setSomeDate(java.sql.Timestamp someDate) {
    this.someDate = someDate;
  }

  public java.sql.Timestamp getSomeTimestamp() {
    return someTimestamp;
  }

  public void setSomeTimestamp(java.sql.Timestamp someTimestamp) {
    this.someTimestamp = someTimestamp;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl getChildren() {
    return children;
  }

  public void setChildren(com.terradatum.jdbc.db.objects.jdbcTest.ChildTbl children) {
    this.children = children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ParentObj that = (ParentObj) o;
    return
      Objects.equals(getParentId(), that.getParentId()) &&
      Objects.equals(getParentName(), that.getParentName()) &&
      Objects.equals(getSubParent(), that.getSubParent()) &&
      Objects.equals(getSomeDate(), that.getSomeDate()) &&
      Objects.equals(getSomeTimestamp(), that.getSomeTimestamp()) &&
      Objects.equals(getImage(), that.getImage()) &&
      Objects.equals(getChildren(), that.getChildren());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getParentId(),
        getParentName(),
        getSubParent(),
        getSomeDate(),
        getSomeTimestamp(),
        getImage(),
        getChildren());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("parentId", getParentId())
        .add("parentName", getParentName())
        .add("subParent", getSubParent())
        .add("someDate", getSomeDate())
        .add("someTimestamp", getSomeTimestamp())
        .add("image", getImage())
        .add("children", getChildren()).toString();
  }
}