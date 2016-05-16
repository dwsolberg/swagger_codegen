package io.swagger.codegen.languages;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import io.swagger.codegen.*;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreDataCodegen extends DefaultCodegen implements CodegenConfig {
  private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{[a-zA-Z_]+\\}");
  protected String projectName = "SwaggerClient";
  protected boolean unwrapRequired = false;
  protected String[] responseAs = new String[0];
  protected String sourceFolder = "CoreDataBase";

  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  public String getName() {
    return "coredata";
  }

  public String getHelp() {
    return "Generates a CoreData client library.";
  }

  public CoreDataCodegen() {
    super();
    outputFolder = "CoreData";
    templateDir = "coredata";

    languageSpecificPrimitives = new HashSet<String>(
      Arrays.asList(
        "Int",
        "Float",
        "Double",
        "Bool",
        "Void",
        "String",
        "Character")
    );

    defaultIncludes = new HashSet<String>(
      Arrays.asList(
        "NSDate",
        "Array",
        "Dictionary",
        "Set",
        "Any",
        "Empty",
        "AnyObject")
    );

    reservedWords = new HashSet<String>(
      Arrays.asList(
        "class", "break", "as", "associativity", "deinit", "case", "dynamicType", "convenience", "enum", "continue",
        "false", "dynamic", "extension", "default", "is", "didSet", "func", "do", "nil", "final", "import", "else",
        "self", "get", "init", "fallthrough", "Self", "infix", "internal", "for", "super", "inout", "let", "if",
        "true", "lazy", "operator", "in", "COLUMN", "left", "private", "return", "FILE", "mutating", "protocol",
        "switch", "FUNCTION", "none", "public", "where", "LINE", "nonmutating", "static", "while", "optional",
        "struct", "override", "subscript", "postfix", "typealias", "precedence", "var", "prefix", "Protocol",
        "required", "right", "set", "Type", "unowned", "weak", "guard", "defer")
    );
  
    typeMapping = new HashMap<String, String>();
    typeMapping.put("array", "Transformable");
    typeMapping.put("List", "Transformable");
    typeMapping.put("map", "Transformable");
    typeMapping.put("date", "Date");
    typeMapping.put("Date", "Date");
    typeMapping.put("DateTime", "Date");
    typeMapping.put("boolean", "Boolean");
    typeMapping.put("string", "String");
    typeMapping.put("char", "String");
    typeMapping.put("short", "Integer 32");
    typeMapping.put("int", "Integer 32");
    typeMapping.put("long", "Integer 32");
    typeMapping.put("integer", "Integer 32");
    typeMapping.put("Integer", "Integer 32");
    typeMapping.put("float", "Double");
    typeMapping.put("number", "Double");
    typeMapping.put("double", "Double");
    typeMapping.put("object", "String");
    typeMapping.put("file", "Binary");
    
    importMapping = new HashMap<String, String>();
  }
  
  @Override
  public void processOpts() {
    super.processOpts();

    // Setup project name
    if (additionalProperties.containsKey("projectName")) {
      projectName = (String) additionalProperties.get("projectName");
    } else {
      additionalProperties.put("projectName", projectName);
    }
    

    // Setup unwrapRequired option, which makes all the properties with "required" non-optional
    if (additionalProperties.containsKey("unwrapRequired")) {
      unwrapRequired = Boolean.parseBoolean(String.valueOf(additionalProperties.get("unwrapRequired")));
    }
    additionalProperties.put("unwrapRequired", unwrapRequired);

    // Setup unwrapRequired option, which makes all the properties with "required" non-optional
    if (additionalProperties.containsKey("responseAs")) {
      Object responseAsObject = additionalProperties.get("responseAs");
      if (responseAsObject instanceof String) {
        responseAs = ((String)responseAsObject).split(",");
      } else {
        responseAs = (String[]) responseAsObject;
      }
    }
    additionalProperties.put("responseAs", responseAs);

    supportingFiles.add(new SupportingFile("CoreDataBuilders.mustache", sourceFolder, "CoreDataBuilders.swift"));
    supportingFiles.add(new SupportingFile("CoreDataCommonalities.mustache", sourceFolder, "CoreDataCommonalities.swift"));
    supportingFiles.add(new SupportingFile("xcdatamodel.mustache", sourceFolder, "CoreData.xcdatamodeld/CoreData.xcdatamodel/contents"));
    supportingFiles.add(new SupportingFile("CDStack.mustache", sourceFolder, "CDStack.swift"));
  }

  @Override
  public String escapeReservedWord(String name) {
    return "Swagger" + name;  // add an underscore to the name
  }

  @Override
  public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      return "Transformable";
    } else if (p instanceof MapProperty) {
      return "Transformable";
    }
    return super.getTypeDeclaration(p);
  }

  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if (languageSpecificPrimitives.contains(type))
        return toModelName(type);
    } else
      type = swaggerType;
    return toModelName(type);
  }

  @Override
  public String toDefaultValue(Property p) {
    // nil
    return null;
  }

  @Override
  public String toInstantiationType(Property p) {
    if (p instanceof MapProperty) {
      MapProperty ap = (MapProperty) p;
      String inner = getSwaggerType(ap.getAdditionalProperties());
      return "[String:" + inner + "]";
    } else if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      String inner = getSwaggerType(ap.getItems());
      return "[" + inner + "]";
    }
    return null;
  }

  @Override
  public CodegenProperty fromProperty(String name, Property p) {
    CodegenProperty codegenProperty = super.fromProperty(name, p);
    if (codegenProperty.isEnum) {
      List<Map<String, String>> swiftEnums = new ArrayList<Map<String, String>>();
      List<String> values = (List<String>) codegenProperty.allowableValues.get("values");
      for (String value : values) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("enum", StringUtils.capitalize(value));
        map.put("raw", value);
        swiftEnums.add(map);
      }
      codegenProperty.allowableValues.put("values", swiftEnums);
      codegenProperty.datatypeWithEnum =
              StringUtils.left(codegenProperty.datatypeWithEnum, codegenProperty.datatypeWithEnum.length() - "Enum".length());
      if (reservedWords.contains(codegenProperty.datatypeWithEnum)) {
        codegenProperty.datatypeWithEnum = escapeReservedWord(codegenProperty.datatypeWithEnum);
      }
    }
    return codegenProperty;
  }

  @Override
  public String toApiName(String name) {
    if(name.length() == 0)
      return "DefaultAPI";
    return initialCaps(name) + "API";
  }

  @Override
  public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Model> definitions) {
    path = normalizePath(path);
    List<Parameter> parameters = operation.getParameters();
    parameters = Lists.newArrayList(Iterators.filter(parameters.iterator(), new Predicate<Parameter>() {
      @Override
      public boolean apply(@Nullable Parameter parameter) {
        return !(parameter instanceof HeaderParameter);
      }
    }));
    operation.setParameters(parameters);
    return super.fromOperation(path, httpMethod, operation, definitions);
  }

  private static String normalizePath(String path) {
    StringBuilder builder = new StringBuilder();

    int cursor = 0;
    Matcher matcher = PATH_PARAM_PATTERN.matcher(path);
    boolean found = matcher.find();
    while (found) {
      String stringBeforeMatch = path.substring(cursor, matcher.start());
      builder.append(stringBeforeMatch);

      String group = matcher.group().substring(1, matcher.group().length() - 1);
      group = camelize(group, true);
      builder
              .append("{")
              .append(group)
              .append("}");

      cursor = matcher.end();
      found = matcher.find();
    }

    String stringAfterMatch = path.substring(cursor);
    builder.append(stringAfterMatch);

    return builder.toString();
  }
}