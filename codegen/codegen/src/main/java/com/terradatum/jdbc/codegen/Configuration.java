package com.terradatum.jdbc.codegen;

import com.google.common.base.MoreObjects;

import java.io.File;
import java.util.List;

public class Configuration {
  private final String url;
  private final String username;
  private final String password;
  private final String packageName;
  private final File outputDirectory;
  private final File objectTemplateFile;
  private final File tableTemplateFile;
  private final List<String> schemas;
  private final String typeIncludes;
  private final String typeExcludes;

  public Configuration(String url,
                       String username,
                       String password,
                       String packageName,
                       File outputDirectory,
                       File objectTemplateFile,
                       File tableTemplateFile,
                       List<String> schemas,
                       String typeExcludes,
                       String typeIncludes) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.packageName = packageName;
    this.outputDirectory = outputDirectory;
    this.objectTemplateFile = objectTemplateFile;
    this.tableTemplateFile = tableTemplateFile;
    this.schemas = schemas;
    this.typeExcludes = typeExcludes;
    this.typeIncludes = typeIncludes;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getPackageName() {
    return packageName;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public File getObjectTemplateFile() {
    return objectTemplateFile;
  }

  public File getTableTemplateFile() {
    return tableTemplateFile;
  }

  public List<String> getSchemas() {
    return schemas;
  }

  public String getTypeIncludes() {
    return typeIncludes;
  }

  public String getTypeExcludes() {
    return typeExcludes;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("url", url).add("username", username).add("password", "*****")
                      .add("packageName", packageName).add("outputDirectory", outputDirectory).add("objectTemplateFile", objectTemplateFile)
                      .add("tableTemplateFile", tableTemplateFile).add("schemas", schemas).add("typeIncludes", typeIncludes)
                      .add("typeExcludes", typeExcludes).toString();
  }
}
