package com.terradatum.jdbc;

import com.google.common.collect.ForwardingList;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author rbellamy@terradatum.com
 * @date 2/8/16
 */
public abstract class JdbcArrayList<E> extends ForwardingList<E> implements DbArray<E> {
  private final TypeToken<E> typeToken = new TypeToken<E>(getClass()) {
  };

  protected JdbcArrayList() {}

  @Override
  @SuppressWarnings("unchecked")
  public JdbcArrayList<E> setArray(Array array) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException {
    if (array != null) {
      setElements((E[]) array.getArray());
    }
    return this;
  }

  @Override
  public JdbcArrayList<E> setArray(Object array) throws SQLException, NoSuchMethodException, NoSuchFieldException,
      IllegalAccessException, InvocationTargetException {
    return setArray((Array) array);
  }

  @Override
  public JdbcArrayList<E> setElements(E[] elements) throws SQLException {
    if (elements != null) {
      Collections.addAll(this, elements);
    }
    return this;
  }

  protected abstract ArrayList<E> delegate();

  @Override
  public TypeToken<E> getTypeToken() {
    return typeToken;
  }

  /**
   * This method is required because of reflection gymnastics in {@link OracleCallableStatementAdapter} and
   * {@link EdbCallableStatementAdapter}. Basically I couldn't find a way to cast the {@param element} to the proper type from
   * outside this method, so the best I could do was pass it in as an object and let this method do the unchecked cast...
   *
   * @param element an element of the {@link Array}
   * @return true if success, false if not
   */
  @Override
  public boolean addObject(Object element) {
    // noinspection unchecked
    return super.add((E) element);
  }

  /**
   * Retrieves the contents of the SQL <code>ARRAY</code> value designated
   * by this
   * <code>Array</code> object in the form of an array in the Java
   * programming language. This version of the method <code>getArray</code>
   * uses the type map associated with the connection for customizations of
   * the type mappings.
   * <p>
   * <strong>Note:</strong> When <code>getArray</code> is used to materialize
   * a base type that maps to a primitive data type, then it is
   * implementation-defined whether the array returned is an array of
   * that primitive data type or an array of <code>Object</code>.
   *
   * @return an array in the Java programming language that contains
   * the ordered elements of the SQL <code>ARRAY</code> value
   * designated by this <code>Array</code> object
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public Object getArray() throws SQLException {
    return delegate().toArray();
  }

  /**
   * Retrieves the contents of the SQL <code>ARRAY</code> value designated by this
   * <code>Array</code> object.
   * This method uses
   * the specified <code>map</code> for type map customizations
   * unless the base type of the array does not match a user-defined
   * type in <code>map</code>, in which case it
   * uses the standard mapping. This version of the method
   * <code>getArray</code> uses either the given type map or the standard mapping;
   * it never uses the type map associated with the connection.
   * <p>
   * <strong>Note:</strong> When <code>getArray</code> is used to materialize
   * a base type that maps to a primitive data type, then it is
   * implementation-defined whether the array returned is an array of
   * that primitive data type or an array of <code>Object</code>.
   *
   * @param map a <code>java.util.Map</code> object that contains mappings
   *            of SQL type names to classes in the Java programming language
   * @return an array in the Java programming language that contains the ordered
   * elements of the SQL array designated by this object
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public Object getArray(Map<String, Class<?>> map) throws SQLException {
    return getArray();
  }

  /**
   * Retrieves a slice of the SQL <code>ARRAY</code>
   * value designated by this <code>Array</code> object, beginning with the
   * specified <code>index</code> and containing up to <code>count</code>
   * successive elements of the SQL array.  This method uses the type map
   * associated with the connection for customizations of the type mappings.
   * <p>
   * <strong>Note:</strong> When <code>getArray</code> is used to materialize
   * a base type that maps to a primitive data type, then it is
   * implementation-defined whether the array returned is an array of
   * that primitive data type or an array of <code>Object</code>.
   *
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @return an array containing up to <code>count</code> consecutive elements
   * of the SQL array, beginning with element <code>index</code>
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public Object getArray(long index, int count) throws SQLException {
    int fromIndex = Math.toIntExact(index);
    return delegate().subList(fromIndex, fromIndex + count);
  }

  /**
   * Retreives a slice of the SQL <code>ARRAY</code> value
   * designated by this <code>Array</code> object, beginning with the specified
   * <code>index</code> and containing up to <code>count</code>
   * successive elements of the SQL array.
   * <p>
   * This method uses
   * the specified <code>map</code> for type map customizations
   * unless the base type of the array does not match a user-defined
   * type in <code>map</code>, in which case it
   * uses the standard mapping. This version of the method
   * <code>getArray</code> uses either the given type map or the standard mapping;
   * it never uses the type map associated with the connection.
   * <p>
   * <strong>Note:</strong> When <code>getArray</code> is used to materialize
   * a base type that maps to a primitive data type, then it is
   * implementation-defined whether the array returned is an array of
   * that primitive data type or an array of <code>Object</code>.
   *
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to
   *              retrieve
   * @param map   a <code>java.util.Map</code> object
   *              that contains SQL type names and the classes in
   *              the Java programming language to which they are mapped
   * @return an array containing up to <code>count</code>
   * consecutive elements of the SQL <code>ARRAY</code> value designated by this
   * <code>Array</code> object, beginning with element
   * <code>index</code>
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
    return getArray(index, count);
  }

  /**
   * Retrieves a result set that contains the elements of the SQL
   * <code>ARRAY</code> value
   * designated by this <code>Array</code> object.  If appropriate,
   * the elements of the array are mapped using the connection's type
   * map; otherwise, the standard mapping is used.
   * <p>
   * The result set contains one row for each array element, with
   * two columns in each row.  The second column stores the element
   * value; the first column stores the index into the array for
   * that element (with the first array element being at index 1).
   * The rows are in ascending order corresponding to
   * the order of the indices.
   *
   * @return a {@link ResultSet} object containing one row for each
   * of the elements in the array designated by this <code>Array</code>
   * object, with the rows in ascending order based on the indices.
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public ResultSet getResultSet() throws SQLException {
    throw new UnsupportedOperationException();
  }

  /**
   * Retrieves a result set that contains the elements of the SQL
   * <code>ARRAY</code> value designated by this <code>Array</code> object.
   * This method uses
   * the specified <code>map</code> for type map customizations
   * unless the base type of the array does not match a user-defined
   * type in <code>map</code>, in which case it
   * uses the standard mapping. This version of the method
   * <code>getResultSet</code> uses either the given type map or the standard mapping;
   * it never uses the type map associated with the connection.
   * <p>
   * The result set contains one row for each array element, with
   * two columns in each row.  The second column stores the element
   * value; the first column stores the index into the array for
   * that element (with the first array element being at index 1).
   * The rows are in ascending order corresponding to
   * the order of the indices.
   *
   * @param map contains the mapping of SQL user-defined types to
   *            classes in the Java programming language
   * @return a <code>ResultSet</code> object containing one row for each
   * of the elements in the array designated by this <code>Array</code>
   * object, with the rows in ascending order based on the indices.
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException();
  }

  /**
   * Retrieves a result set holding the elements of the subarray that
   * starts at index <code>index</code> and contains up to
   * <code>count</code> successive elements.  This method uses
   * the connection's type map to map the elements of the array if
   * the map contains an entry for the base type. Otherwise, the
   * standard mapping is used.
   * <p>
   * The result set has one row for each element of the SQL array
   * designated by this object, with the first row containing the
   * element at index <code>index</code>.  The result set has
   * up to <code>count</code> rows in ascending order based on the
   * indices.  Each row has two columns:  The second column stores
   * the element value; the first column stores the index into the
   * array for that element.
   *
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @return a <code>ResultSet</code> object containing up to
   * <code>count</code> consecutive elements of the SQL array
   * designated by this <code>Array</code> object, starting at
   * index <code>index</code>.
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public ResultSet getResultSet(long index, int count) throws SQLException {
    throw new UnsupportedOperationException();
  }

  /**
   * Retrieves a result set holding the elements of the subarray that
   * starts at index <code>index</code> and contains up to
   * <code>count</code> successive elements.
   * This method uses
   * the specified <code>map</code> for type map customizations
   * unless the base type of the array does not match a user-defined
   * type in <code>map</code>, in which case it
   * uses the standard mapping. This version of the method
   * <code>getResultSet</code> uses either the given type map or the standard mapping;
   * it never uses the type map associated with the connection.
   * <p>
   * The result set has one row for each element of the SQL array
   * designated by this object, with the first row containing the
   * element at index <code>index</code>.  The result set has
   * up to <code>count</code> rows in ascending order based on the
   * indices.  Each row has two columns:  The second column stores
   * the element value; the first column stores the index into the
   * array for that element.
   *
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @param map   the <code>Map</code> object that contains the mapping
   *              of SQL type names to classes in the Java(tm) programming language
   * @return a <code>ResultSet</code> object containing up to
   * <code>count</code> consecutive elements of the SQL array
   * designated by this <code>Array</code> object, starting at
   * index <code>index</code>.
   * @throws SQLException                    if an error occurs while attempting to
   *                                         access the array
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.2
   */
  @Override
  public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException();
  }

  /**
   * This method frees the <code>Array</code> object and releases the resources that
   * it holds. The object is invalid once the <code>free</code>
   * method is called.
   * <p>
   * After <code>free</code> has been called, any attempt to invoke a
   * method other than <code>free</code> will result in a <code>SQLException</code>
   * being thrown.  If <code>free</code> is called multiple times, the subsequent
   * calls to <code>free</code> are treated as a no-op.
   * <p>
   *
   * @throws SQLException                    if an error occurs releasing
   *                                         the Array's resources
   * @throws UnsupportedOperationException if the JDBC driver does not support
   *                                         this method
   * @since 1.6
   */
  @Override
  public void free() throws SQLException {
    throw new UnsupportedOperationException();
  }
}
