package com.terradatum.jdbc.codegen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Terradatum DB Codegen Plugin.
 *
 * @author rbellamy@terradatum.com
 * @date 2015-02-07
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES, requiresDependencyResolution = TEST)
public class DbCodegenPlugin extends AbstractMojo {

  /**
   * The Maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  /**
   * The JDBC Url used to create a database connection to PPAS
   * <p>
   * <b>MUST BE AN EDB URL!!!</b>
   */
  @Parameter(required = true)
  private String url;

  /**
   * The username used to connect to the database - must have access to {@code pg_catalog}
   */
  @Parameter(required = true)
  private String username;

  /**
   * The password used to connect to the database
   */
  @Parameter(required = true)
  private String password;

  /**
   * Location of the output directory
   */
  @Parameter(alias = "output-directory", property = "com.terradatum.db.codegen.output", defaultValue = "${project.build.directory}/generated-sources/db-codegen")
  private File outputDirectory;

  /**
   * The package name to apply to the generated classes.
   */
  @Parameter(alias = "package-name", required = true)
  private String packageName;

  /**
   * Folder containing the template files
   */
  @Parameter(alias = "template-directory", defaultValue = "src/main/resources/templates")
  private File templateDirectory;

  /**
   * The template file name used to generate {@link java.sql.Struct} model classes from database OBJECT types.
   * <p>
   * Looks in the {@code {$templateDirectory}} and if it can't find it there, pulls the default template from the
   * {@link Generator} resource.
   */
  @Parameter(alias = "object-template-file-name", defaultValue = "obj.stg")
  private String objectTemplateFileName;

  /**
   * The template file name used to generate {@link java.sql.Array} model classes from database TABLE types.
   * <p>
   * Looks in the {@code {$templateDirectory}} and if it can't find it there, pulls the default template from the
   * {@link Generator} resource.
   */
  @Parameter(alias = "table-template-file-name", defaultValue = "tbl.stg")
  private String tableTemplateFileName;

  /**
   * The list of schemas from which to collect types
   */
  @Parameter(required = true)
  private List<String> schemas;

  /**
   * All elements that are included (A Java regular expression. Use the pipe to separate several expressions). Watch out for
   * case-sensitivity.
   * <p>
   * You can create case-insensitive reqular expressions using the syntax: (?i:expr).
   * <p>
   * Whitespace is ignored.
   */
  @Parameter(alias = "type-includes", defaultValue = ".*")
  private String typeIncludes;

  /**
   * All the elements that excluded (A Java regular expression. Use the pipe to separate several expressions.
   * <p>
   * Excludes match before includes.
   */
  @Parameter(alias = "type-excludes")
  private String typeExcludes;

  /**
   * Whether to skip the execution of this plugin.
   */
  @Parameter
  private boolean skip;

  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Skipping Terradatum DB code generation");
      return;
    }

    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

    try {
      Thread.currentThread().setContextClassLoader(getClassLoader());
      Configuration configuration = new Configuration(url, username, password, packageName, outputDirectory,
          new File(templateDirectory.getPath() + File.separatorChar + objectTemplateFileName),
          new File(templateDirectory.getPath() + File.separatorChar + tableTemplateFileName), schemas, typeExcludes,
          typeIncludes);

      Generator.execute(configuration);
    } catch (Exception ex) {
      throw new MojoExecutionException("Error running Terradatum DB code generation tool", ex);
    } finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }

    project.addCompileSourceRoot(outputDirectory.getPath());
  }

  @SuppressWarnings("unchecked")
  private ClassLoader getClassLoader() throws MojoExecutionException {
    try {
      List<String> classpathElements = project.getRuntimeClasspathElements();
      URL urls[] = new URL[classpathElements.size()];

      for (int i = 0; i < urls.length; i++) {
        urls[i] = new File(classpathElements.get(i)).toURI().toURL();
      }

      return new URLClassLoader(urls, getClass().getClassLoader());
    } catch (Exception e) {
      throw new MojoExecutionException("Couldn't create a classloader.", e);
    }
  }
}
