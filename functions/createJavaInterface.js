const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

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
  var output;
  var groupDefinitions = [];

  if (groups.length == 0) {
    console.error("No Interfaces Detected: Check Paths.");
  } else {
    // Turn path names into proper interface names if possible
    interfaceNames = replaceInterfaceNames(source, interfaceNames);
    // Create one file for each interface
    for (group in groups) {
      output = fs.readFileSync('./docs/mustache/interface_imports_template.java', 'utf8');
      var options = {Author: "Insert Author"};
      output = mustache.render(output, options);
      // Add Static Content
      output = generateStaticTop(output, interfaceNames[group]);

      // Compile list of definitions for each interface
      groupDefinitions = getPathDefinitions(source, groups[group]);

      // Creates each data item
      for (def in groupDefinitions) {
        output = addDataItem(output, source, groupDefinitions[def]);
      }

      // Static CRUD operations
      output = generateMethods(source, output, groups[group]);

      // Add closing bracket
      output = closeBracket(output);

      var fileName = interfaceNames[group] + "Interface.java";
      fs.writeFile(node_path.join(destination, fileName), output, function(err) {
        if (err) {
          console.error("Could not write Interface file");
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
  output_str += ("\n\npublic class " + interface_name + "Interface {\n\n");
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
    output_str = generateNamedQuery(output_str, doc, definition);
  }
  output_str += "\n";
  return output_str;
}

function generateMethods(doc, output, pathName){
  try {
    var opID;
    var defName;
    var idString;
    output += "\n\t// Read Operations\n\n";
    for (path in doc['paths']){
      if (path.search(pathName) != -1){
        if (doc['paths'][path].hasOwnProperty('get')){
          opID = doc['paths'][path]['get']['operationId'];
          if (opID.search(/collection/i) != -1){
            output += ("\tpublic List<Map<String, Object>> " + opID + "() throws ServiceException {\n");
            output += ("\t\treturn ResourceUtils.readCollectionFromQuery(" + util.toUnderscoreUpper(util.getServiceName(doc)) + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimReadCollection(opID)) + ", CONTEXT_FILTER);\n\t}\n\n");
          } else if (opID.search(/resource/i) != -1) {
            defName = util.matchWithDefinition(doc, util.trimReadResource(opID));
            idString = util.getID(doc, defName);
            output += ("\tpublic Map<String, Object> " + opID + "(String " + idString + ") throws ServiceException {\n");
            output += ("\t\treturn ResourceUtils.readResource(" + idString + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimReadResource(opID)) + ", CONTEXT_FILTER);\n\t}\n\n");
          } else {
            output += ("\tpublic List<Map<String, Object>> " + opID + "() throws ServiceException {\n");
            output += ("\t\t // This method does not match the expected format and only a stub was created for it.\n\t}\n\n");
          }
        }
      }
    }

    output += "\n\t// Create Operations\n\n";
    for (path in doc['paths']){
      if (path.search(pathName) != -1) {
        if (doc['paths'][path].hasOwnProperty('post')){
          opID = doc['paths'][path]['post']['operationId'];
          defName = util.matchWithDefinition(doc, util.trimCreateBatch(opID));
          if (opID.search(/createBatch/i) != -1){
            if (util.isTLC(doc, defName)){
              output += ("\tpublic Map<String, Object> " + opID + "(JsonElement requestBody) throws ServiceException {\n");
              output += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimCreateBatch(opID)) + ", requestBody);\n");
              output += ("\t\treturn response;\n\t}\n\n");
            } else {
              idString = util.getID(doc, defName);
              output += ("\tpublic Map<String, Object> " + opID + "(JsonElement requestBody, String " + idString + ") throws ServiceException {\n");
              output += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimCreateBatch(opID)) + ", " + idString + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimCreateBatch(opID)) + ", requestBody, null);\n");
              output += ("\t\treturn response;\n\t}\n\n");
            }
          } else {
            output += ("\tpublic Map<String, Object> " + opID + "() throws ServiceException {\n");
            output += ("\t\t// This method does not match the expected format and only a stub was created for it.\n\t}\n\n");
          }
        }
      }
    }

    output += "\n\t// Update Operations\n\n";
    for (path in doc['paths']){
      if (path.search(pathName) != -1) {
        if (doc['paths'].hasOwnProperty(path)){
          if (doc['paths'][path].hasOwnProperty('put')){
            opID = doc['paths'][path]['put']['operationId'];
            defName = util.matchWithDefinition(doc, util.trimUpdateResource(opID));
            idString = util.getID(doc, defName);
            if (opID.search(/update/i) != -1){
              if (util.isTLC(doc, defName)){
                idString = util.getID(doc, defName);
                output += ("\tpublic Map<String, Object> " + opID + "(JsonElement requestBody, String " + idString + ") throws ServiceException {\n");
                output += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + idString + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimUpdateResource(opID)) + ", requestBody, CONTEXT_FILTER);\n");
                output += ("\t\treturn response;\n\t}\n\n");
              } else {
                idString = util.getID(doc, defName);
                output += ("\tpublic Map<String, Object> " + opID + "(JsonElement requestBody, String " + idString + ") throws ServiceException {\n");
                output += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + idString + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(util.trimUpdateResource(opID)) + ", " + idString + ", DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);\n");
                output += ("\t\treturn response;\n\t}\n\n");
              }
            }
          }
        }
      }
    }

    output += "\n\t// Delete Operations\n\n";
    for (path in doc['paths']){
      if (path.search(pathName) != -1) {
        if (doc['paths'].hasOwnProperty(path)){
          if (doc['paths'][path].hasOwnProperty('delete')){
            opID = doc['paths'][path]['delete']['operationId'];
            if (opID.search(/deleteBatch/i) != -1){
              output += ("\tpublic Map<String, Object> " + opID + "(JsonElement requestBody) throws ServiceException {\n");
              output += ("\t\tMap<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_" + util.toUnderscoreUpper(defName) + ");\n");
              output += ("\t\treturn response;\n\t}\n\n");
            } else if (opID.search(/delete/i) != -1) {
              defName = util.matchWithDefinition(doc, util.trimDeleteResource(opID));
              idString = util.getID(doc, defName);
              output += ("\tpublic Map<String, Object> " + opID + "(String " + idString + ") throws ServiceException {\n");
              output += ("\t\tMap<String, Object> response = ResourceUtils.deleteResource(" + idString + ", DATA_ITEM_NAME_" + util.toUnderscoreUpper(defName) + ");\n");
              output += "\t\treturn response;\n\t}\n\n";
            } else {
              output += ("\tpublic List<Map<String, Object>> " + opID + "() throws ServiceException {\n");
              output += ("\t\t // This method does not match the expected format and only a stub was created for it.\n\t}\n\n");
            }
          }
        }
      }
    }
    return output;
  } catch (e) {
    //console.error('ERROR: Operation ID Missing');
    console.error(e);
  }
}

function generateNamedQuery(output_str, doc, def){
  output_str += "\tprivate static final String " + util.toUnderscoreUpper(def) + "_QUERY = \"getAll" + def +  "\";\n";
  return output_str;
}

// Adds closing bracket to file
function closeBracket(output_str) {
  output_str += "\n}";
  return output_str;
}
