package com.terradatum.jdbc.codegen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;

/**
 * @author rbellamy@terradatum.com
 * @date 3/26/16
 */
public class TestGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestGenerator.class);
  /**
   * Used for testing - not really viable when runnning in various systems with different paths.
   * <p>
   * <b>USE THE MAVEN PLUGIN!!!!</b>
   *
   * @param args the program arguments
   */
  public static void main(String[] args) {
    try {
      Properties p = new Properties();
      InputStream is = ClassLoader.getSystemResourceAsStream("jdbc.properties");
      p.load(is);
      String packageName = "com.terradatum.jdbc.db.objects";

      // set the output directory based on input parameters, otherwise current directory
      File projectBaseDir;
      if (args.length > 0) {
        projectBaseDir = new File(args[0]);
      } else {
        projectBaseDir = new File(".");
      }

      // set the search path with the rest of the input parameters, otherwise nothing
      ArrayList<String> searchPath = null;
      if (args.length > 1) {
        String[] searchPathArray = new String[args.length - 1];
        arraycopy(args, 1, searchPathArray, 0, searchPathArray.length);
        searchPath = new ArrayList<>(asList(searchPathArray));
      }

      String outputDirectoryPath = projectBaseDir.toString();
      String templatesDirectoryPath = projectBaseDir + "/src/main/resources/templates";
      File objectTemplateFile = new File(templatesDirectoryPath, "obj.stg");
      File tableTemplateFile = new File(templatesDirectoryPath, "tbl.stg");
      File outputDirectory = new File(outputDirectoryPath);

      Generator.execute(new Configuration(
          p.getProperty("edb.connection-url"),
          p.getProperty("edb.username"),
          p.getProperty("edb.password"),
          packageName,
          outputDirectory,
          objectTemplateFile,
          tableTemplateFile,
          searchPath,
          null,
          "(?i:.*)"));
    } catch (ClassNotFoundException | SQLException | IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
