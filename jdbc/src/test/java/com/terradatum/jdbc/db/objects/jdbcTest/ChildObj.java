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
 * @date 2016-03-28T19:45:12.116
 */
public class ChildObj implements DbStruct {
  public static final String SQL_TYPE_NAME = "jdbc_test.child_obj";

  private java.math.BigDecimal childId;
  private String childName;

  /**
   * Required default constructor.
   */
  public ChildObj() {
  }

  /**
   * All-elements, type-safe constructor
   */
  public ChildObj(java.math.BigDecimal childId, String childName) {
    this.childId = childId;
    this.childName = childName;

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
    return new Object[]{childId, childName};
  }

  @Override
  public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException { 
    return getAttributes();
  }

  @Override
  public void setAttributes(Object[] attributes) throws SQLException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
    childId = ConverterUtil.convert(attributes[0], java.math.BigDecimal.class);
    childName = ConverterUtil.convert(attributes[1], String.class);
  }

  public java.math.BigDecimal getChildId() {
    return childId;
  }

  public void setChildId(java.math.BigDecimal childId) {
    this.childId = childId;
  }

  public String getChildName() {
    return childName;
  }

  public void setChildName(String childName) {
    this.childName = childName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ChildObj that = (ChildObj) o;
    return
      Objects.equals(getChildId(), that.getChildId()) &&
      Objects.equals(getChildName(), that.getChildName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getChildId(),
        getChildName());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("childId", getChildId())
        .add("childName", getChildName()).toString();
  }
}