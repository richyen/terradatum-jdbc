package com.terradatum.jdbc;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.SQLException;

/**
 * @author rbellamy@terradatum.com
 * @date 3/27/16
 */
public interface DbArray<E> extends Array, DbData {

  Object[] toArray();

  DbArray<E> setArray(Array array) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException;

  DbArray<E> setArray(Object array) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException;

  DbArray<E> setElements(E[] elements) throws SQLException;

  TypeToken<E> getTypeToken();

  boolean addObject(Object element);

  char getEdbArrayDelimiter();
}
