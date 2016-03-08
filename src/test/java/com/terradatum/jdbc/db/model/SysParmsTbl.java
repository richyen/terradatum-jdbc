package com.terradatum.jdbc.db.model;

import com.terradatum.jdbc.StructArrayList;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;

public class SysParmsTbl extends StructArrayList<SysParmsObj> {
  public static final String SQL_TYPE_NAME = "metrics.sys_parms_tbl";
    private final ArrayList<SysParmsObj> delegate = new ArrayList<>();

  /**
   * Required default constructor.
   */
  @SuppressWarnings("unused")
  public SysParmsTbl() {
  }

  @Override
  protected ArrayList<SysParmsObj> delegate() {
    return delegate;
  }

  @Override
  public String getSQLTypeName() {
    return SQL_TYPE_NAME;
  }

  @Override
  public SysParmsTbl setArray(Array array) throws SQLException, InvocationTargetException,
      NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
      super.setArray(array);
      return this;
  }

  @Override
  public SysParmsTbl setElements(SysParmsObj[] elements) throws SQLException {
      super.setElements(elements);
      return this;
  }
}