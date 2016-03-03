package com.terradatum.jdbc;

import com.edb.jdbc4.Jdbc4Connection;
import oracle.jdbc.driver.OracleConnection;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author rbellamy@terradatum.com
 * @date 1/26/16
 */
public class AbstractAdapterTest {
  protected final String searchPath = "'metrics','terradatum'";
  private OracleConnection oracleConnection;
  private Jdbc4Connection edbConnection;

  @Before
  public void setUp() throws ClassNotFoundException, SQLException, IOException {
    Properties p = new Properties();
    InputStream is = ClassLoader.getSystemResourceAsStream("jdbc.properties");
    p.load(is);
    Class.forName(p.getProperty("oracle.class-name"));
    Class.forName(p.getProperty("edb.class-name"));
    setOracleConnection((OracleConnection) DriverManager.getConnection(p.getProperty("oracle.connection-url"),
        p.getProperty("oracle.username"), p.getProperty("oracle.password")));
    setEdbConnection((Jdbc4Connection) DriverManager.getConnection(p.getProperty("edb.connection-url"),
        p.getProperty("edb.username"), p.getProperty("edb.password")));
  }

  public OracleConnection getOracleConnection() {
    return oracleConnection;
  }

  public void setOracleConnection(OracleConnection oracleConnection) {
    this.oracleConnection = oracleConnection;
  }

  public Jdbc4Connection getEdbConnection() {
    return edbConnection;
  }

  public void setEdbConnection(Jdbc4Connection edbConnection) {
    this.edbConnection = edbConnection;
  }
}
