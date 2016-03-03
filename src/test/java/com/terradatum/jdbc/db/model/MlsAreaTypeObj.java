package com.terradatum.jdbc.db.model;

import com.terradatum.jdbc.converters.ConverterUtil;
import com.terradatum.jdbc.DbStruct;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author rbellamy@terradatum.com
 * @date 1/25/16
 */
public class MlsAreaTypeObj implements DbStruct {
  public static final String SQL_TYPE_NAME = "terradatum.mls_area_type_obj";
  private BigDecimal sid;
  private Timestamp createdDate;
  private Timestamp modifiedDate;
  private String modifiedBy;
  private String description;

  /**
   * Required default constructor.
   */
  @SuppressWarnings("unused")
  public MlsAreaTypeObj() {
  }

  public MlsAreaTypeObj(BigDecimal sid, Timestamp createdDate, Timestamp modifiedDate, String modifiedBy, String description) {
    this.sid = sid;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.modifiedBy = modifiedBy;
    this.description = description;
  }

  @Override
  public void setStruct(Struct struct)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    setAttributes(struct.getAttributes());
  }

  @Override
  public String getSQLTypeName() throws SQLException {
    return SQL_TYPE_NAME;
  }

  @Override
  public Object[] getAttributes() throws SQLException {
    return new Object[] { sid, createdDate, modifiedDate, modifiedBy, description };
  }

  @Override
  public void setAttributes(Object[] attributes)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {

    sid = ConverterUtil.convert(attributes[0], BigDecimal.class);
    createdDate = ConverterUtil.convert(attributes[1], Timestamp.class);
    modifiedDate = ConverterUtil.convert(attributes[1], Timestamp.class);
    modifiedBy = ConverterUtil.convert(attributes[3], String.class);
    description = ConverterUtil.convert(attributes[4], String.class);
  }

  @Override
  public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
    return getAttributes();
  }

  public BigDecimal getSid() {
    return sid;
  }

  public void setSid(BigDecimal sid) {
    this.sid = sid;
  }

  public Timestamp getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public Timestamp getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "MlsAreaTypeObj{" + "sid=" + sid + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", modifiedBy='"
        + modifiedBy + '\'' + ", description='" + description + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MlsAreaTypeObj that = (MlsAreaTypeObj) o;

    if (getSid() != null ? !getSid().equals(that.getSid()) : that.getSid() != null) {
      return false;
    }
    if (getCreatedDate() != null ? !getCreatedDate().equals(that.getCreatedDate()) : that.getCreatedDate() != null) {
      return false;
    }
    if (getModifiedDate() != null ? !getModifiedDate().equals(that.getModifiedDate()) : that.getModifiedDate() != null) {
      return false;
    }
    if (getModifiedBy() != null ? !getModifiedBy().equals(that.getModifiedBy()) : that.getModifiedBy() != null) {
      return false;
    }
    return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;

  }

  @Override
  public int hashCode() {
    int result = getSid() != null ? getSid().hashCode() : 0;
    result = 31 * result + (getCreatedDate() != null ? getCreatedDate().hashCode() : 0);
    result = 31 * result + (getModifiedDate() != null ? getModifiedDate().hashCode() : 0);
    result = 31 * result + (getModifiedBy() != null ? getModifiedBy().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }
}
