package com.terradatum.jdbc;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author rbellamy@terradatum.com
 * @date 3/15/16
 */
public class SqlError {
  private int errorCode;
  private String state;

  public SqlError(int errorCode, String state) {
    this.errorCode = errorCode;
    this.state = state;
  }

  /**
   * Gets the SQL state code from the supplied {@link SQLException exception}.
   * <p>
   * Some JDBC drivers nest the actual exception from a batched update, so we might need to dig down into the nested exception.
   *
   * @param sqlException
   *          the exception from which the {@link SQLException#getSQLState() SQL state} is to be extracted
   * @return the SQL state code
   */
  public static String getSqlState(SQLException sqlException) {
    String sqlState = null;
    if (sqlException != null) {
      sqlState = sqlException.getSQLState();
      if (Strings.isNullOrEmpty(sqlState)) {
        return getSqlState(sqlException.getNextException());
      }
    }
    return sqlState;
  }

  /**
   * Gets the SQL state code from the supplied {@link SQLException exception}.
   * <p>
   * Some JDBC drivers nest the actual exception from a batched update, so we might need to dig down into the nested exception.
   *
   * @param sqlException
   *          the exception from which the {@link SQLException#getErrorCode() SQL error code} is to be extracted
   * @return the SQL error code
   */
  public static int getSqlErrorCode(SQLException sqlException) {
    int sqlErrorCode = 0;
    if (sqlException != null) {
      sqlErrorCode = sqlException.getErrorCode();
      if (sqlErrorCode == 0) {
        return getSqlErrorCode(sqlException.getNextException());
      }
    }
    return sqlErrorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getState() {
    return state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SqlError that = (SqlError) o;
    return
        Objects.equals(getErrorCode(), that.getErrorCode()) &&
            Objects.equals(getState(), that.getState());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getErrorCode(),
        getState());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("errorCode", getErrorCode())
                      .add("state", getState()).toString();
  }
}
