package com.terradatum.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author rbellamy@terradatum.com
 * @date 1/31/16
 */
public interface DbStruct extends Struct, DbData {

  void setStruct(Struct struct)
      throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException;

  void setAttributes(Object[] attributes)
      throws SQLException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
