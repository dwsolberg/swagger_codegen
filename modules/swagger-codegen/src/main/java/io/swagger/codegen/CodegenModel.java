package io.swagger.codegen;

import io.swagger.models.ExternalDocs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CodegenModel 
{
    public String parent;
    public String name, classname, description, classVarName, modelJson;
    public String unescapedDescription;
    public String defaultValue;
    public List<CodegenProperty> vars = new ArrayList<CodegenProperty>();
    public Set<String> imports = new HashSet<String>();
    public Boolean hasVars, emptyVars, hasMoreModels, hasEnums;
    public ExternalDocs externalDocs;
    public Map<String, Object> vendorExtensions;

    // DWS Additions

    // Builds a convenience initializer with required variables
    public Boolean isInitRequired; // x-init-required

    // Whether to automatically build the corresponding core-data model
    // Requires that each model object conforms to UuidFindable
    public Boolean isBuildCoreData; // x-build-core-data

    // Protocols
    public Boolean isProtocolUUIDType; // x-protocol-uuid-type
    public Boolean isProtocolSortOrderType; // x-protocol-sort-order-type
    public Boolean isProtocolNameType; // x-protocol-name-type
    public Boolean isProtocolSoftDeletableType; // x-protocol-soft-deleteable-type

}
