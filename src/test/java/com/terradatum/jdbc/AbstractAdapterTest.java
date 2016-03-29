package com.terradatum.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
@SuppressWarnings("Duplicates")
public class AbstractAdapterTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdapterTest.class);
  protected static final DataSource ORACLE_DATA_SOURCE = createOracleDataSource();
  protected static final DataSource EDB_DATA_SOURCE = createEdbDataSource();
  protected final String searchPath = "'jdbc_test'";

  private static DataSource createOracleDataSource() {
    try {
      // TODO: Why the hell do we need to set these here? They should be picked up by the IntelliJ Surefire support.
      // https://youtrack.jetbrains.com/issue/IDEA-101185
      // https://youtrack.jetbrains.com/issue/IDEA-123453
      //System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
      //System.setProperty("oracle.jdbc.Trace", Boolean.TRUE.toString());
      return BasicDataSourceFactory.createDataSource(getProperties("oracle"));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  private static DataSource createEdbDataSource() {
    try {
      BasicDataSource basicDataSource = BasicDataSourceFactory.createDataSource(getProperties("edb"));
      basicDataSource.setConnectionProperties("loglevel=2");
      return basicDataSource;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @NotNull
  private static Properties getProperties(String vendor) throws IOException {
    Properties p = new Properties();
    InputStream is = ClassLoader.getSystemResourceAsStream(vendor + ".properties");
    p.load(is);
    return p;
  }

  public OracleConnectionAdapter getOracleConnectionAdapter() throws SQLException {
    assert ORACLE_DATA_SOURCE != null;
    return (OracleConnectionAdapter)JdbcConnectionAdapterFactory.create(getOracleConnection());
  }

  public Connection getOracleConnection() throws SQLException {
    assert ORACLE_DATA_SOURCE != null;
    return ORACLE_DATA_SOURCE.getConnection();
  }

  public EdbConnectionAdapter getEdbConnectionAdapter(String searchPath) throws SQLException {
    assert EDB_DATA_SOURCE != null;
    return (EdbConnectionAdapter)JdbcConnectionAdapterFactory.create(getEdbConnection(), searchPath);
  }

  public Connection getEdbConnection() throws SQLException {
    assert EDB_DATA_SOURCE != null;
    return EDB_DATA_SOURCE.getConnection();
  }

}
