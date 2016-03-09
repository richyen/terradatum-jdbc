package com.terradatum.jdbc.db;

import com.terradatum.jdbc.*;
import com.terradatum.jdbc.db.model.MlsAreaTypeObj;
import com.terradatum.jdbc.db.model.MlsAreaTypeTbl;
import oracle.jdbc.OracleConnection;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;

import static com.terradatum.jdbc.JdbcConnectionAdapterFactory.create;
import static com.terradatum.jdbc.db.model.MlsAreaTypeTbl.SQL_TYPE_NAME;
import static java.sql.Types.ARRAY;

/**
 * @author rbellamy@terradatum.com
 * @date 2/2/16
 */
public class MlsTest extends AbstractAdapterTest {
  public static final MlsAreaTypeObj MLS_AREA_TYPE_OBJ_1 = new MlsAreaTypeObj(new BigDecimal(1), null, null, null, "Counties");
  public static final MlsAreaTypeObj MLS_AREA_TYPE_OBJ_2 = new MlsAreaTypeObj(new BigDecimal(2), null, null, null, "Cities");
  public static final MlsAreaTypeObj MLS_AREA_TYPE_OBJ_3 = new MlsAreaTypeObj(new BigDecimal(3), null, null, null, "MLS Areas");
  public static final MlsAreaTypeObj MLS_AREA_TYPE_OBJ_4 = new MlsAreaTypeObj(new BigDecimal(4), null, null, "METRICS",
      "ZIP Codes");
  public static final MlsAreaTypeObj MLS_AREA_TYPE_OBJ_5 = new MlsAreaTypeObj(new BigDecimal(8),
      Timestamp.valueOf("2008-09-08 16:09:48.0"), Timestamp.valueOf("2008-09-08 16:09:48.0"), "MSCHAEFER", "Map Codes");
  public static final Object[] MLS_AREA_TYPE_OBJS = new Object[] { MLS_AREA_TYPE_OBJ_1, MLS_AREA_TYPE_OBJ_2, MLS_AREA_TYPE_OBJ_3,
      MLS_AREA_TYPE_OBJ_4, MLS_AREA_TYPE_OBJ_5 };

  protected MlsAreaTypeTbl getMlsAreaTypesViaCallableStatement(Connection connection, String searchPath) throws SQLException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
    int mlsSid = 1;
    String commandText = "{? = call metrics.mls_pkg.get_mls_area_types(?)}";
    DbConnectionAdapter dbConnectionAdapter = create(connection, searchPath);
    DbCallableStatementAdapter dbCallableStatementAdapter = dbConnectionAdapter.prepareCallAdapter(commandText);
    dbCallableStatementAdapter.registerOutParameter(1, ARRAY, SQL_TYPE_NAME);
    dbCallableStatementAdapter.setNumeric(2, mlsSid);
    dbCallableStatementAdapter.execute();
    MlsAreaTypeTbl mlsAreaTypeTbl = dbCallableStatementAdapter.getArray(1, MlsAreaTypeTbl.class);
    return mlsAreaTypeTbl;
  }

  protected MlsAreaTypeTbl getMlsAreaTypesViaPreparedStatement(Connection connection, String searchPath)
      throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
    int mlsSid = 1;
    String commandText = "select metrics.mls_pkg.get_mls_area_types(?)";
    // seriously?????
    if (OracleConnection.class.isAssignableFrom(connection.getClass())) {
      commandText = commandText + " from dual";
    }
    DbConnectionAdapter dbConnectionAdapter = JdbcConnectionAdapterFactory.create(connection, searchPath);
    DbPreparedStatementAdapter dbPreparedStatementAdapter = dbConnectionAdapter.prepareStatementAdapter(commandText);
    dbPreparedStatementAdapter.setNumeric(1, mlsSid);
    ResultSet rs = dbPreparedStatementAdapter.executeQuery();
    MlsAreaTypeTbl mlsAreaTypeTbl = null;
    if (rs.next()) {
      Array mlsAreaTypeTblObject = (Array) rs.getObject(1);
      mlsAreaTypeTbl = new MlsAreaTypeTbl();
      mlsAreaTypeTbl.setArray(mlsAreaTypeTblObject);
    }
    return mlsAreaTypeTbl;
  }

}
