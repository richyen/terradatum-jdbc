package com.terradatum.jdbc.db.model;

import com.terradatum.jdbc.StructArrayList;

import java.util.ArrayList;

/**
 * @author rbellamy@terradatum.com
 * @date 1/30/16
 */
public class MlsAreaTypeTbl extends StructArrayList<MlsAreaTypeObj> {
  private final ArrayList<MlsAreaTypeObj> delegate = new ArrayList<>();
  public static final String SQL_TYPE_NAME = "terradatum.mls_area_type_tbl";

  /**
   * Required default constructor.
   */
  @SuppressWarnings("unused")
  public MlsAreaTypeTbl() {
  }

  @Override
  protected ArrayList<MlsAreaTypeObj> delegate() {
    return delegate;
  }

  @Override
  public String getSQLTypeName() {
    return SQL_TYPE_NAME;
  }
}
