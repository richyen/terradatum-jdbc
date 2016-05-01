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
 * @date 2016-04-30T16:58:56.384
 */
public class FactRow implements DbStruct {
  public static final String SQL_TYPE_NAME = "jdbc_test.fact_row";

  private Timestamp factTime;
  private String dimensionValue;
  private BigDecimal measureValue;

  /**
   * Required default constructor.
   */
  public FactRow() {
  }

  /**
   * All-elements, type-safe constructor
   */
  public FactRow(Timestamp factTime, String dimensionValue, BigDecimal measureValue) {
    this.factTime = factTime;
    this.dimensionValue = dimensionValue;
    this.measureValue = measureValue;

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
    return new Object[]{factTime, dimensionValue, measureValue};
  }

  @Override
  public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException { 
    return getAttributes();
  }

  @Override
  public void setAttributes(Object[] attributes) throws SQLException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
    factTime = ConverterUtil.convert(attributes[0], Timestamp.class);
    dimensionValue = ConverterUtil.convert(attributes[1], String.class);
    measureValue = ConverterUtil.convert(attributes[2], BigDecimal.class);
  }

  public Timestamp getFactTime() {
    return factTime;
  }

  public void setFactTime(Timestamp factTime) {
    this.factTime = factTime;
  }

  public String getDimensionValue() {
    return dimensionValue;
  }

  public void setDimensionValue(String dimensionValue) {
    this.dimensionValue = dimensionValue;
  }

  public BigDecimal getMeasureValue() {
    return measureValue;
  }

  public void setMeasureValue(BigDecimal measureValue) {
    this.measureValue = measureValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FactRow that = (FactRow) o;
    return
      Objects.equals(getFactTime(), that.getFactTime()) &&
      Objects.equals(getDimensionValue(), that.getDimensionValue()) &&
      Objects.equals(getMeasureValue(), that.getMeasureValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getFactTime(),
        getDimensionValue(),
        getMeasureValue());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("factTime", getFactTime())
        .add("dimensionValue", getDimensionValue())
        .add("measureValue", getMeasureValue()).toString();
  }
}