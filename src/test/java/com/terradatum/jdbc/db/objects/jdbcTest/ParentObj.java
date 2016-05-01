package com.terradatum.jdbc.db.objects.jdbcTest;

import com.terradatum.jdbc.converters.ConverterUtil;
import com.terradatum.jdbc.DbStruct;
import com.google.common.base.MoreObjects;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

/**
 * DO NOT MODIFY!!! This class was generated by the Terradatum JDBC Code Generator and will be overwritten if regenerated
 * @date 2016-04-30T16:58:56.518
 */
public class ParentObj implements DbStruct {
  public static final String SQL_TYPE_NAME = "jdbc_test.parent_obj";

  private BigDecimal parentId;
  private String parentName;
  private SubParentObj subParent;
  private Timestamp someDate;
  private Timestamp someTimestamp;
  private byte[] image;
  private ChildTbl children;

  /**
   * Required default constructor.
   */
  public ParentObj() {
  }

  /**
   * All-elements, type-safe constructor
   */
  public ParentObj(BigDecimal parentId, String parentName, SubParentObj subParent, Timestamp someDate, Timestamp someTimestamp, byte[] image, ChildTbl children) {
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
   * @param struct the {@link Struct} which is used as the model for this {@link DbStruct} object
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
    parentId = ConverterUtil.convert(attributes[0], BigDecimal.class);
    parentName = ConverterUtil.convert(attributes[1], String.class);
    subParent = new SubParentObj();
    subParent.setStruct((Struct) attributes[2]);
    someDate = ConverterUtil.convert(attributes[3], Timestamp.class);
    someTimestamp = ConverterUtil.convert(attributes[4], Timestamp.class);
    image = ConverterUtil.convert(attributes[5], byte[].class);
    children = new ChildTbl();
    children.setArray((Array) attributes[6]);
  }

  public BigDecimal getParentId() {
    return parentId;
  }

  public void setParentId(BigDecimal parentId) {
    this.parentId = parentId;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public SubParentObj getSubParent() {
    return subParent;
  }

  public void setSubParent(SubParentObj subParent) {
    this.subParent = subParent;
  }

  public Timestamp getSomeDate() {
    return someDate;
  }

  public void setSomeDate(Timestamp someDate) {
    this.someDate = someDate;
  }

  public Timestamp getSomeTimestamp() {
    return someTimestamp;
  }

  public void setSomeTimestamp(Timestamp someTimestamp) {
    this.someTimestamp = someTimestamp;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public ChildTbl getChildren() {
    return children;
  }

  public void setChildren(ChildTbl children) {
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