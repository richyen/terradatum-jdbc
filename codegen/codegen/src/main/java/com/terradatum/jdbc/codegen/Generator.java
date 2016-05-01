package com.terradatum.jdbc.codegen;

//import com.google.common.base.Strings;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rbellamy@terradatum.com
 * @date 2/6/16
 */
public class Generator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
  private static final String TEMPLATES_DIRECTORY_PATH = "./src/main/resources/templates";

  @Option(name = "-u", aliases = "--url", usage = "the EDB database url to connect to", metaVar = "<url>", required = true)
  private String url = null;

  @Option(name = "-n", aliases = "--username", usage = "the EDB username", metaVar = "<username>", required = true)
  private String username = null;

  @Option(name = "-p", aliases = "--password", usage = "the EDB password", metaVar = "<password>", required = true)
  private String password = null;

  @Option(name = "-pk", aliases = "--packageName", usage = "the package name", metaVar = "<package name>")
  private String packageName = "com.terradatum.jdbc.db.objects";

  @Option(name = "-o", aliases = "--outputDirectory", usage = "the output directory", metaVar = "<output directory>")
  private File outputDirectory = new File(".");

  @Option(name = "-ot", aliases = "--objectTemplateFile", usage = "the object template file", metaVar = "<object template file>")
  private File objectTemplateFile = new File(TEMPLATES_DIRECTORY_PATH, "obj.stg");

  @Option(name = "-tt", aliases = "--tableTemplateFile", usage = "the table template file", metaVar = "<table template file>")
  private File tableTemplateFile = new File(TEMPLATES_DIRECTORY_PATH, "tbl.stg");

  @Option(name = "-s", aliases = "--schemas", usage = "the schemas", metaVar = "[schema1 [schema2 [schemaN] ...]]", required = true)
  private List<String> schemas = null;

  @Option(name = "-e", aliases = "--excludes", usage = "the types to exclude", metaVar = "<excludes regex>")
  private String typeExcludes = null;

  @Option(name = "-i", aliases = "--includes", usage = "the includes", metaVar = "<includes regex>")
  private String typeIncludes = "(?i:.*)";

  @Option(name = "-h", aliases = "--help", usage = "print usage")
  private boolean printUsage = false;

  private static void createDirIfNotExists(File directory) {
    if (!directory.isDirectory()) {
      if (!directory.exists() && !directory.mkdirs()) {
        throw new IllegalArgumentException("Cannot make output directory:" + directory);
      }
    }
  }

  private static void generate(TypeInfo typeInfo,
                               String packageName,
                               LocalDateTime now,
                               File schemaOutputDirectory,
                               File templateFile) throws IOException {
    File outFile = new File(schemaOutputDirectory.getPath() + File.separatorChar + typeInfo.getClassName() + ".java");
    LOGGER.debug("Generating '{}'", outFile.toPath());
    try (FileWriter out = new FileWriter(outFile)) {
      STGroup template = new STGroupFile(templateFile.getPath());
      LOGGER.debug("Template file '{]'", templateFile.getPath());
      ST mainTemplate = template.getInstanceOf("main");

      LOGGER.debug("... to package {} on {} using:\n{}", packageName, now, typeInfo);
      mainTemplate.add("typeInfo", typeInfo);
      mainTemplate.add("package", packageName);
      mainTemplate.add("created", now);
      mainTemplate.write(new AutoIndentWriter(out));
      LOGGER.debug("Generated '{}'", outFile.toPath());
    }
  }

  public static void execute(Configuration configuration) throws ClassNotFoundException, SQLException, IOException {
    File outputDirectory = new File(configuration.getOutputDirectory().getPath() + File.separatorChar
        + configuration.getPackageName().replace('.', File.separatorChar));

    createDirIfNotExists(outputDirectory);

    LOGGER.info("Executing codegen with configuration:\n{}", configuration);

    TypeCollector typeCollector = new TypeCollector(configuration.getUrl(), configuration.getUsername(),
        configuration.getPassword());
    // get the Struct (OBJECT) and Array (TABLE) UDTs
    List<TypeInfo> metadata =
        typeCollector.getMetadata(
            configuration.getSchemas(),
            configuration.getTypeExcludes(),
            configuration.getTypeIncludes());

    LOGGER.info("Found #{} total types after excludes and includes.", metadata.size());

    File objectTemplateFile = configuration.getObjectTemplateFile();
    File tableTemplateFile = configuration.getTableTemplateFile();
    if (!objectTemplateFile.exists()) {
      LOGGER.info("Object template file '{}' does not exist, using embedded resource file instead", objectTemplateFile.getPath());
      objectTemplateFile = new File(Generator.class.getResource("/templates/obj.stg").getFile());
    }

    if (!tableTemplateFile.exists()) {
      LOGGER.info("Table template file '{}' does not exist, using embedded resource file instead", tableTemplateFile.getPath());
      tableTemplateFile = new File(Generator.class.getResource("/templates/tbl.stg").getFile());
    }

    // generate the files for each type
    for (TypeInfo typeInfo : metadata) {
      LOGGER.info("Generating file for '{}'", typeInfo.getTypeName());
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

  public static void main(String[] args) {
    new Generator().doMain(args);
  }

  /**
   * @param args the program arguments
   */
  public void doMain(String[] args) {
    try {
      CmdLineParser parser = new CmdLineParser(this);
      try {
        parser.setUsageWidth(110);
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        System.err.println(e.getMessage());
        System.err.println("java Generator [options]...");
        parser.printUsage(System.err);
        System.err.println();
        return;
      }

      if (printUsage) {
        parser.printUsage(System.out);
        System.out.println();
        return;
      }

      execute(new Configuration(
          url,
          username,
          password,
          packageName,
          outputDirectory,
          objectTemplateFile,
          tableTemplateFile,
          schemas,
          typeExcludes,
          typeIncludes));
    } catch (ClassNotFoundException | SQLException | IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
