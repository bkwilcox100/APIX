const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');

// Create Functions

/*
Name: create
Description: Creates Java Class from OA Spec
Parameters:
- source : Source JS object file to be converted
- destination : Destination folder for .java file(s)
Preconditions:
- JS object exists
Postconditions:
- destination is valid
Return: None
Status: COMPLETE
*/
exports.create = function(source, destination) {
  // Get Top Level Collection from Source Doc
  var groups = util.getTLC(source);
  var interfaceNames = util.getTLC(source);

  // Initialize output string
  var output = "";
  var groupDefinitions = [];

  if (groups.length == 0) {
    console.error("No Interfaces Detected: Check Paths.");
  } else {
    // Turn path names into proper interface names if possible
    interfaceNames = replaceInterfaceNames(source, interfaceNames);
    // Create one file for each interface
    for (group in groups) {
      output = "";
      // Add Static Content
      output = generateStaticTop(output, interfaceNames[group]);

      // Compile list of definitions for each interface
      groupDefinitions = getPathDefinitions(source, groups[group]);
      //console.log(groupDefinitions);
      // Creates each data item
      for (def in groupDefinitions) {
        output = addDataItem(output, source, groupDefinitions[def]);
      }

      // Static CRUD operations
      output = generateCreateOP(output, source, groups[group], groupDefinitions);
      output = generateReadOP(output, source, groups[group], groupDefinitions);
      output = generateUpdateOP(output, source, groups[group], groupDefinitions);
      output = generateDeleteOP(output, source, groups[group], groupDefinitions);

      // Add closing bracket
      output = closeBracket(output);
      var fileName = interfaceNames[group] + "Interface.java";
      fs.writeFile(node_path.join(destination, fileName), output, function(err) {
        if (err) {
          console.error("Could not write Java file");
          throw err;
        }
      });
      console.log(fileName + " Created.");
    }
  }
}

// Helper Functions

// Generates Static Content at beginning of file
function generateStaticTop(output_str, interface_name) {
  output_str += "// Add any necessary packages.\n\n\n";
  output_str += ("public class " + interface_name + "Interface {\n\n");
  output_str += "\tprivate static final String CONTEXT_FILTER = \"default\";\n";
  output_str += "\tprivate static final String DEFAULT_PARENT_PROPERTY = \"parent\";\n\n";
  return output_str;
}

// Generates Static CRUD declarations
function generateStaticCRUD(output_str) {
  output_str += "\tprivate static final String OPERATION_CREATE = \"create\";\n";
  output_str += "\tprivate static final String OPERATION_UPDATE = \"update\";\n";
  output_str += "\tprivate static final String OPERATION_DELETE = \"delete\";\n";
  output_str += "\tprivate static final String OPERATION_READ = \"read\";\n";
  return output_str;
}

// Preserves the CamelCase/UpperCase of each interface name
function replaceInterfaceNames(doc, groupList) {
  for (def in doc['definitions']) {
    for (item in groupList) {
      if ((def.toLowerCase()).search(groupList[item].toLowerCase()) != -1 ) {
        groupList[item] = def;
      } else {
        if (((def + 's').toLowerCase()).search(groupList[item].toLowerCase()) != -1) {
          groupList[item] = def;
        }
      }
    }
  }
  return groupList;
}

// Gets the necessary definitions from each path
function getPathDefinitions(doc, group) {
  var definitions = [];
  var item = "";
  for (path in doc['paths']) {
    if (path.indexOf(group) != -1) {
      if (doc['paths'][path].hasOwnProperty('get')) {
        if (doc['paths'][path]['get']['responses']['200']['schema'].hasOwnProperty('type')) {
          if (doc['paths'][path]['get']['responses']['200']['schema'].hasOwnProperty('items')) {
            if (doc['paths'][path]['get']['responses']['200']['schema']['items'].hasOwnProperty('$ref')) {
              item = util.getArrTableName(doc['paths'][path]['get']['responses']['200']['schema']['items']['$ref']);
              if (!(_.contains(definitions, item))) {
                definitions.push(item);
              }
            }
          }
        } else {
          if (doc['paths'][path]['get']['responses']['200']['schema'].hasOwnProperty('$ref')) {
            item = util.getArrTableName(doc['paths'][path]['get']['responses']['200']['schema']['$ref']);
            if (!(_.contains(definitions, item))) {
              definitions.push(item);
            }
          }
        }
      }

      if (doc['paths'][path].hasOwnProperty('post')) {
        if (doc['paths'][path]['post']['responses']['200']['schema'].hasOwnProperty('type')) {
          if (doc['paths'][path]['post']['responses']['200']['schema'].hasOwnProperty('items')) {
            if (doc['paths'][path]['post']['responses']['200']['schema']['items'].hasOwnProperty('$ref')) {
              item = util.getArrTableName(doc['paths'][path]['post']['responses']['200']['schema']['items']['$ref']);
              if (!(_.contains(definitions, item))) {
                definitions.push(item);
              }
            }
          }
        } else {
          if (doc['paths'][path]['post']['responses']['200']['schema'].hasOwnProperty('$ref')) {
            item = util.getArrTableName(doc['paths'][path]['post']['responses']['200']['schema']['$ref']);
            if (!(_.contains(definitions, item))) {
              definitions.push(item);
            }
          }
        }
      }

    }
  }
  return definitions;
}

// Adds a data item declaration
function addDataItem(output_str, doc, definition) {
  output_str += ("\tprivate static final String DATA_ITEM_NAME_" + util.toUnderscoreUpper(definition) + " = \"" + util.toCamelCase(definition) + "\";\n");
  for (prop in doc['definitions'][definition]['properties']) {
    output_str += ("\tprivate static final String APP_PROPERTIES_" + util.toUnderscoreUpper(prop) + " = \"" + util.toCamelCase(prop) + "\";\n");
  }
  if (util.isTLC(doc, definition)) {
    output_str += ("\tprivate static final String APP_PROPERTIES_COLLECTION_QUERY = \"all_" + util.toUnderscore(definition) + "\";\n");
  }
  output_str += "\n";
  return output_str;
}

// Generates Create operations
function generateCreateOP(output_str, doc, group, definitions) {
  output_str += "\n\t// Create Operations\n\n";
  for (def in definitions) {
    if (util.isTLC(doc, definitions[def])) {
      output_str += ("\tpublic Map<String, Object> createBatch" + definitions[def] + "(JsonElement requestBody) throws ServiceException {\n");
      output_str += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + util.toUnderscoreUpper(group) + ", null, null, requestBody, APP_PROPERTIES_" + util.toUnderscoreUpper("InsertID") + ");\n");
      output_str += "\t\treturn response;\n\t}\n\n";
    } else {
      output_str += ("\tpublic Map<String, Object> createBatch" + definitions[def] + "(JsonElement requestBody, String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
      output_str += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + util.toUnderscoreUpper(group) + ", " + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(group) + ", requestBody, null);\n");
      output_str += "\t\treturn response;\n\t}\n\n";
    }
  }
  return output_str;
}

// Generates Read operations
function generateReadOP(output_str, doc, group, definitions) {
  output_str += "\n\t// Read Operations\n\n";
  for (def in definitions) {
    if (util.isTLC(doc, definitions[def])) {
      wholeDoc += ("\tpublic List<Map<String, Object>> read" + definitions[def] + "Collection() throws ServiceException {\n");
      wholeDoc += ("\t\treturn ResourceUtils.readCollectionFromQuery(APP_PROPERTIES_COLLECTION_QUERY, DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", CONTEXT_FILTER);\n\t}\n\n");
      wholeDoc += ("\tpublic Map<String, Object> read" + definitions[def] + "Resource(String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
      wholeDoc += ("\t\treturn ResourceUtils.readResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", CONTEXT_FILTER);\n\t}\n\n");
    } else {
      output_str += ("\tpublic List<Map<String, Object>> read" + definitions[def] + "Collection() throws ServiceException {\n");
      output_str += ("\t\treturn ResourceUtils.readSubCollection(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", CONTEXT_FILTER);\n\t}\n\n");
      output_str += ("\tpublic Map<String, Object> read" + definitions[def] + "Resource(String " + util.getID(doc, definitions[def]) + ", String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
      output_str += ("\t\treturn ResourceUtils.readResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", CONTEXT_FILTER);\n\t}\n\n");
    }
  }
  return output_str;
}

// Generates Update operations
function generateUpdateOP(output_str, doc, group, definitions) {
  output_str += "\n\t// Update Operations\n\n";
  for (def in definitions) {
    if (util.isTLC(doc, definitions[def])) {
      output_str += ("\tpublic Map<String, Object> update" + definitions[def] + "Resource(JsonElement requestBody, String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
      output_str += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", requestBody, CONTEXT_FILTER);\n");
      output_str += ("\t\treturn response;\n\t}\n\n");
    } else {
      output_str += ("\tpublic Map<String, Object> update" + definitions[def] + "Resource(JsonElement requestBody, String " + util.getID(doc, definitions[def]) + ", String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
      output_str += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", " + util.getID(doc, definitions[def]) + ", DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);\n");
      output_str += ("\t\treturn response;\n\t}\n\n");
    }
  }
  return output_str;
}

// Generates Delete operations
function generateDeleteOP(output_str, doc, group, definitions) {
  output_str += "\n\t// Delete Operations\n\n";
  if (util.isTLC(doc, definitions[def])) {
    output_str += ("\tpublic Map<String, Object> deleteBatch" + definitions[def] + "Resource(JsonElement requestBody) throws ServiceException {\n");
    output_str += ("\t\tMap<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ");\n");
    output_str += ("\t\treturn response;\n\t}\n\n");

    output_str += ("\tpublic Map<String, Object> delete" + definitions[def] + "Resource(String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
    output_str += ("\t\tMap<String, Object> response = ResourceUtils.deleteResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ");\n");
    output_str += "return response;\n\t}\n\n";
  } else {
    output_str += ("\tpublic Map<String, Object> deleteBatch" + definitions[def] + "Resource(JsonElement requestBody) throws ServiceException {\n");
    output_str += ("\t\tMap<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ");\n");
    output_str += ("\t\treturn response;\n\t}\n\n");

    output_str += ("\tpublic Map<String, Object> delete" + definitions[def] + "Resource(String " + util.getID(doc, definitions[def]) + ", String " + util.getID(doc, definitions[def]) + ") throws ServiceException {\n");
    output_str += ("\t\tMap<String, Object> response = ResourceUtils.deleteResource(" + util.getID(doc, definitions[def]) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(definitions[def]) + ", " + util.getID(doc, definitions[def]) + ", DEFAULT_PARENT_PROPERTY);\n");
    output_str += "\t\treturn response;\n\t}\n\n";
  }
  return output_str;
}

// Adds closing bracket to file
function closeBracket(output_str) {
  output_str += "\n}";
  return output_str;
}
