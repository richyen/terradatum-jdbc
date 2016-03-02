package com.terradatum.jdbc.codegen;

//import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class Generator {

  private static final Logger logger = LoggerFactory.getLogger(Generator.class);

  /**
   * Used for testing - not really viable when runnning in various systems with different paths.
   * <p>
   * <b>USE THE MAVEN PLUGIN!!!!</b>
   *
   * @param args
   *          not used
   */
  public static void main(String[] args) {
    try {
      Properties p = new Properties();
      p.load(new FileReader(new File("jdbc.properties")));
      String packageName = "com.terradatum.common.db.model";

      // set the output directory based on input parameters, otherwise current directory
      String projectBaseDir;
      if (args.length > 0) {
        projectBaseDir = args[0];
      } else {
        projectBaseDir = ".";
      }

      // set the search path with the rest of the input parameters, otherwise nothing
      ArrayList<String> searchPath = null;
      if (args.length > 1) {
        String[] searchPathArray = new String[args.length - 2];
        System.arraycopy(args, 1, searchPathArray, 0, searchPathArray.length);
        searchPath = new ArrayList<>(Arrays.asList(searchPathArray));
      }

      String outputDirectoryPath = projectBaseDir + "/target/generated-test-sources/db-codegen";
      String templatesDirectoryPath = projectBaseDir + "/src/main/resources/templates";
      File objectTemplateFile = new File(templatesDirectoryPath + "/obj.stg");
      File tableTemplateFile = new File(templatesDirectoryPath + "/tbl.stg");
      File outputDirectory = new File(outputDirectoryPath);
      Generator generator = new Generator();
      generator.execute(new Configuration(p.getProperty("edb.connection-url"), p.getProperty("edb.username"), p.getProperty("edb.password"),
          packageName, outputDirectory, objectTemplateFile, tableTemplateFile, searchPath, null,
          "(?i:\n" + "metrics\\.agent_contact_info_obj|\n" + "metrics\\.agent_contact_info_tbl|\n"
              + "terradatum\\.mls_agent_id_obj|\n" + "terradatum\\.mls_agent_id_tbl|\n" + "terradatum\\.mls_area_type_obj|\n"
              + "terradatum\\.mls_area_type_tbl|\n" + "terradatum\\.number_tbl|\n" + "terradatum\\.string_tbl|\n" + ")"));

    } catch (ClassNotFoundException | SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  private static void createDirIfNotExists(File directory) {
    if (!directory.isDirectory()) {
      if (!directory.exists() && !directory.mkdirs()) {
        throw new IllegalArgumentException("Cannot make output directory:" + directory);
      }
    }
  }

  private static void generate(TypeInfo typeInfo, String packageName, LocalDateTime now, File schemaOutputDirectory,
      File templateFile) throws IOException {
    File outFile = new File(schemaOutputDirectory.getPath() + File.separatorChar + typeInfo.getClassName() + ".java");
    logger.debug("Generating '{}'", outFile.toPath());
    try (FileWriter out = new FileWriter(outFile)) {
      STGroup template = new STGroupFile(templateFile.getPath());
      logger.debug("Template file '{]'", templateFile.getPath());
      ST mainTemplate = template.getInstanceOf("main");

      logger.debug("... to package {} on {} using:\n{}", packageName, now, typeInfo);
      mainTemplate.add("typeInfo", typeInfo);
      mainTemplate.add("package", packageName);
      mainTemplate.add("created", now);
      mainTemplate.write(new AutoIndentWriter(out));
      logger.debug("Generated '{}'", outFile.toPath());
    }
  }

  public static void execute(Configuration configuration) throws ClassNotFoundException, SQLException, IOException {
    File outputDirectory = new File(configuration.getOutputDirectory().getPath() + File.separatorChar
        + configuration.getPackageName().replace('.', File.separatorChar));

    createDirIfNotExists(outputDirectory);

    logger.info("Executing codegen with configuration:\n{}", configuration);

    TypeCollector typeCollector = new TypeCollector(configuration.getUrl(), configuration.getUsername(),
        configuration.getPassword());
    // get the Struct (OBJECT) and Array (TABLE) UDTs
    List<TypeInfo> metadata = typeCollector.getMetadata(configuration.getSchemas(), configuration.getTypeExcludes(),
        configuration.getTypeIncludes());

    logger.info("Found #{} total types after excludes and includes.", metadata.size());

    File objectTemplateFile = configuration.getObjectTemplateFile();
    File tableTemplateFile = configuration.getTableTemplateFile();
    if (!objectTemplateFile.exists()) {
      logger.info("Object template file '{}' does not exist, using embedded resource file instead", objectTemplateFile.getPath());
      objectTemplateFile = new File(Generator.class.getResource("/templates/obj.stg").getFile());
    }

    if (!tableTemplateFile.exists()) {
      logger.info("Table template file '{}' does not exist, using embedded resource file instead", tableTemplateFile.getPath());
      tableTemplateFile = new File(Generator.class.getResource("/templates/tbl.stg").getFile());
    }

    // generate the files for each type
    for (TypeInfo typeInfo : metadata) {
      logger.info("Generating file for '{}'", typeInfo.getTypeName());
      File schemaOutputDirectory = new File(outputDirectory.getPath() + File.separatorChar + typeInfo.getSchemaName());

      createDirIfNotExists(schemaOutputDirectory);

      // if (Strings.isNullOrEmpty(typeInfo.getElementTypeName())) {
      if (typeInfo.getElementTypeName() == null || typeInfo.getElementTypeName().equals("")) {
        // OBJECT Struct types don't have elements
        generate(typeInfo, configuration.getPackageName(), LocalDateTime.now(), schemaOutputDirectory, objectTemplateFile);
      } else {
        generate(typeInfo, configuration.getPackageName(), LocalDateTime.now(), schemaOutputDirectory, tableTemplateFile);
      }
    }
  }
}
