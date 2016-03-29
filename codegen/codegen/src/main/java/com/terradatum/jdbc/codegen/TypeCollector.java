package com.terradatum.jdbc.codegen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.COMMENTS;
import static java.util.regex.Pattern.compile;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class TypeCollector {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final List<String> strings = Lists.newArrayList("varchar", "bpchar", "cstring", "name", "text", "regclass");
  private Connection connection = null;

  /**
   * The class that is responsible for collecting the various type definitions from PPAS.
   *
   * @param url      the JDBC url
   * @param username the JDBC username
   * @param password the JDBC password
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public TypeCollector(String url, String username, String password) throws ClassNotFoundException, SQLException {
    Class.forName("com.edb.Driver");
    connection = DriverManager.getConnection(url, username, password);
  }

  private static boolean isElementComposite(String element) {
    final String databaseTypes = "(?imx:oid|tid|xid|cid|bool|byte|int2|int4|int8|real|float4|float8|text|name|varchar|char|bpchar|cstring|character|regclass|bytea|date|time|timetz|timestamp|timestamptz|numeric|uuid|money|xml|json|clob)";
    final Pattern pattern = Pattern.compile(databaseTypes);
    return !pattern.matcher(element).matches();
  }

  public List<TypeInfo> getMetadata(List<String> schemas, String typeExcludes, String typeIncludes) throws SQLException {
    final List<TypeInfo> typeInfos = new ArrayList<>();
    Pattern excludesPattern = null;
    Pattern includesPattern = null;

    if (!Strings.isNullOrEmpty(typeExcludes)) {
      excludesPattern = compile(typeExcludes, COMMENTS);
    }

    if (!Strings.isNullOrEmpty(typeIncludes)) {
      includesPattern = compile(typeIncludes, COMMENTS);
    }

    String getTypesCommandString = "SELECT t.typrelid, format_type(t.oid, NULL) AS alias,\n" +
        "pg_get_userbyid(t.typowner) AS typeowner, e.typname AS element\n, e.typdelim" +
        "  FROM pg_type t\n" +
        "  JOIN pg_namespace n ON t.typnamespace = n.oid\n" +
        "  LEFT OUTER JOIN pg_type e ON e.oid=t.typelem\n" +
        "  LEFT OUTER JOIN pg_class ct ON ct.oid=t.typrelid AND ct.relkind <> 'c'\n" +
        "  LEFT OUTER JOIN pg_description des ON (des.objoid=t.oid AND des.classoid='pg_type'::REGCLASS)\n" +
        " WHERE t.typtype != 'd' AND t.typname NOT LIKE E'\\\\_%' AND n.nspname = ANY(?)\n" +
        "   AND ct.oid IS NULL\n" +
        " ORDER BY n.nspname, t.typname;";

    String getAttributesCommandString = "SELECT attname,\n" +
        "       nsp.nspname,\n" +
        "       t.typname,\n" +
        "       t.typinput\n" +
        "FROM pg_attribute att\n" + "JOIN pg_type t ON t.oid=atttypid\n" +
        "JOIN pg_namespace nsp ON t.typnamespace=nsp.oid\n" +
        "LEFT OUTER JOIN pg_type b ON t.typelem=b.oid\n" +
        "LEFT OUTER JOIN pg_collation c ON att.attcollation=c.oid\n" +
        "LEFT OUTER JOIN pg_namespace nspc ON c.collnamespace=nspc.oid\n" +
        "WHERE att.attrelid=?\n" +
        "ORDER BY attnum";

    //https://terradatum.atlassian.net/browse/PPAS-3651?focusedCommentId=67770&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-67770
    logger.debug("Resetting the search_path to ensure properly qualified names");
    try (Statement statement = connection.createStatement()) {
      statement.execute("SET search_path TO ''");
    }

    logger.debug("Unprepared type query:\n{}", getTypesCommandString);

    try (PreparedStatement getTypesPreparedStatement = connection.prepareStatement(getTypesCommandString)) {

      getTypesPreparedStatement.setArray(1, connection.createArrayOf("text", schemas.toArray()));

      logger.debug("Executing query to return types:\n{}", getTypesPreparedStatement.toString());

      ResultSet typeResultSet = getTypesPreparedStatement.executeQuery();
      typeLoop:
      while (typeResultSet.next()) {
        TypeInfo typeInfo = new TypeInfo();
        String alias = typeResultSet.getString("alias");
        String element = typeResultSet.getString("element");
        int typrelid = typeResultSet.getInt("typrelid");
        if (typrelid == 0) {
          String typdelim = typeResultSet.getString("typdelim");
          typeInfo.setEdbArrayDelimiter(typdelim.charAt(0));
        }

        if ((excludesPattern != null && excludesPattern.matcher(alias).matches())
            || (includesPattern != null && !includesPattern.matcher(alias).matches())) {
          logger.info("Excluding '{}'", alias);
          continue typeLoop;
        }

        if (!Strings.isNullOrEmpty(element) && (excludesPattern != null && excludesPattern.matcher(alias).matches())
            || (includesPattern != null && !includesPattern.matcher(alias).matches())) {

          logger.info("Excluding TABLE type '{}' because its OBJECT element '{}' is excluded", alias, element);
          continue typeLoop;
        }

        logger.info("Including '{}'", alias);
        typeInfo.setTypeName(alias);

        // typerelid == 0 means it's a TABLE object, and thus has no attributes
        if (typrelid != 0 && includesPattern != null && includesPattern.matcher(alias).matches()) {
          logger.debug("Unprepared attribute query:\n{}", getAttributesCommandString);

          try (PreparedStatement getAttributesPreparedStatement = connection.prepareStatement(getAttributesCommandString)) {

            getAttributesPreparedStatement.setInt(1, typrelid);

            logger.debug("Executing query to return attributes of '{}':\n{}", typeInfo.getTypeName(),
                getAttributesPreparedStatement.toString());

            ResultSet attributesResultSet = getAttributesPreparedStatement.executeQuery();

            attributeLoop:
            while (attributesResultSet.next()) {
              AttributeInfo attributeInfo = new AttributeInfo();
              String attname = attributesResultSet.getString("attname");
              String schemaName = attributesResultSet.getString("nspname");
              String typname = attributesResultSet.getString("typname");
              String typinput = attributesResultSet.getString("typinput");

              if (typinput.equals("record_in")) { // typinput = 'record_in' is Struct
                attributeInfo.setStruct(true);
              } else if (typinput.equals("nestedtable_in")) { // typinput = 'nestedtable_in' is Array
                attributeInfo.setArray(true);
              } else if (strings.contains(typname)) { // typname determines if it's a string
                attributeInfo.setString(true);
              }

              if (attributeInfo.isStruct() || attributeInfo.isArray()) {
                attributeInfo.setTypeName(schemaName + "." + typname);
                attributeInfo.setDifferentSchema(!typeInfo.getSchemaName().equals(schemaName));

                if ((excludesPattern != null && excludesPattern.matcher(attributeInfo.getTypeName()).matches())
                    || (includesPattern != null && !includesPattern.matcher(attributeInfo.getTypeName()).matches())) {
                  logger.warn("Marking '{}' attribute '{}' as type 'unknown' as it was explicitly excluded, "
                      + "or not explicitly included in the model.", typeInfo.getTypeName(), attributeInfo.getTypeName());
                  attributeInfo.setTypeName("unknown");
                  attributeInfo.setArray(false);
                  attributeInfo.setStruct(false);
                  attributeInfo.setString(false);
                  attributeInfo.setDifferentSchema(false);
                }
              } else {
                attributeInfo.setTypeName(typname);
              }
              attributeInfo.setName(attname);
              typeInfo.getAttributeInfos().add(attributeInfo);
            }
            if (typrelid != 0 && typeInfo.getAttributeInfos().size() == 0) {
              logger.warn("Excluding '{}' - the OBJECT has no attributes", typeInfo.getTypeName());
              continue typeLoop;
            }
          }

        } else {
          typeInfo.setElementTypeName(element);
          typeInfo.setElementComposite(isElementComposite(element));
        }

        typeInfos.add(typeInfo);
      }
    }

    return typeInfos;
  }
}
