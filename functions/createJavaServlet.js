const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
exports.create = function(doc, destination) {
  var output = "";

  // Get Top Level Collection from Source Doc
  var groups = util.getTLC(doc);
  var interfaceNames = util.getTLC(doc);
  var groupDefinitions = [];

  if (groups.length == 0) {
    console.error("No Interfaces Detected: Check Paths.");
  } else {
    interfaceNames = replaceInterfaceNames(doc, interfaceNames);

    for (group in groups) {
      output = fs.readFileSync('./docs/mustache/servlet_imports_template.java', 'utf8');
      var options = {interfaceName: (interfaceNames[group] + "Interface"), Author: "Insert Author"};
      output = mustache.render(output, options);
      output = generateHeader(doc, output, interfaceNames[group]);
      output = generateMethods(doc, output, groups[group]);
      output += ('}');
      var fileName = interfaceNames[group] + "Servlet.java";
      fs.writeFile(node_path.join(destination, fileName), output, function(err) {
        if (err) {
          console.error("Could not write Servlet file");
          throw err;
        }
      });
      console.log(fileName + " Created.");
    }
  }
}

function getPaths(doc) {
  var pathList = [];
  for (path in doc['paths']) {
    pathList.push(path);
  }
  return pathList;
}

function getBaseURL(doc) {
  var str = util.getServiceName(doc);
  str = '/' + str + '/v1';
  return str;
}

function generateHeader(doc, str, name) {
  str += "\n@RestController\n";
  str += ("@RequestMapping(value=\"" + getBaseURL(doc) + "\")\n");
  str += ("public class " + name + "Servlet {\n\n");
  str += ("private static final " + name + "Interface INTERFACE_OBJECT = new " + name + "Interface();\n\n");
  return str;
}

function generateMethods(doc, str, groupName) {
  var opID;
  // POST/Create Methods
  str += ("/*\n");
  str += ("* =============================================================================\n");
  str += ("* Create Operations\n");
  str += ("* =============================================================================\n");
  str += ("*/\n\n");
  for (path in doc['paths']) {
    if (path.indexOf(groupName) != -1) {
      if (doc['paths'][path].hasOwnProperty('post')) {
        opID = doc['paths'][path]['post']['operationId'];
        idString = util.getID(doc, util.getPathName(doc, path));
        str += ("\t@PostMapping(value=\"" + trimPathName(doc, path) + "\")\n");
        var splitPath = path.split('/');
        if (splitPath[3] == splitPath[splitPath.length - 1]) {
          str += ("\tpublic Map<String, Object> " + opID + "(@RequestBody String body) throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "(EndpointUtils.getRequestBodyAsJsonElement(body));\n");
          str += ("\t}\n\n");
        } else {
          str += ("\tpublic Map<String, Object> " + opID + "(@PathVariable String " + idString + ", @RequestBody String body) throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "(EndpointUtils.getRequestBodyAsJsonElement(body), " + idString + ");\n");
          str += ("\t}\n\n");
        }
      }
    }
  }

  str += '\n';

  // GET/Read Methods
  str += ("/*\n");
  str += ("* =============================================================================\n");
  str += ("* Read Operations\n");
  str += ("* =============================================================================\n");
  str += ("*/\n\n");
  for (path in doc['paths']) {
    if (path.indexOf(groupName) != -1) {
      if (doc['paths'][path].hasOwnProperty('get')) {
        opID = doc['paths'][path]['get']['operationId'];
        idString = util.getID(doc, util.getPathName(doc, path));
        str += ("\t@GetMapping(value=\"" + trimPathName(doc, path) + "\")\n");
        if (path.slice(-1) != '}') {
          str += ("\tpublic List<Map<String, Object>> " + opID + "() throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "();\n");
          str += ("\t}\n\n");
        } else {
          str += ("\tpublic Map<String, Object> " + opID + "(@PathVariable String " + idString + ") throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "(" + idString + ");\n");
          str += ("\t}\n\n");
        }
      }
    }
  }

  str += '\n';

  // PUT/Update Methods
  str += ("/*\n");
  str += ("* =============================================================================\n");
  str += ("* Update Operations\n");
  str += ("* =============================================================================\n");
  str += ("*/\n\n");
  for (path in doc['paths']) {
    if (path.indexOf(groupName) != -1) {
      if (doc['paths'][path].hasOwnProperty('put')) {
        opID = doc['paths'][path]['put']['operationId'];
        idString = util.getID(doc, util.getPathName(doc, path));
        str += ("\t@PutMapping(value=\"" + trimPathName(doc, path) + "\")\n");
        str += ("\tpublic Map<String, Object> " + opID + "(@PathVariable String " + idString + ", @RequestBody String body) throws ServiceException {\n");
        str += ("\t\treturn INTERFACE_OBJECT." + opID + "(EndpointUtils.getRequestBodyAsJsonElement(body), " + idString + ");\n");
        str += ("\t}\n\n");
      }
    }
  }

  str += '\n';

  // DELETE/Delete Methods
  str += ("/*\n");
  str += ("* =============================================================================\n");
  str += ("* Delete Operations\n");
  str += ("* =============================================================================\n");
  str += ("*/\n\n");
  for (path in doc['paths']) {
    if (path.indexOf(groupName) != -1) {
      if (doc['paths'][path].hasOwnProperty('delete')) {
        opID = doc['paths'][path]['delete']['operationId'];
        idString = util.getID(doc, util.getPathName(doc, path));
        str += ("\t@DeleteMapping(value=\"" + trimPathName(doc, path) + "\")\n");
        if (path.slice(-1) != '}') {
          str += ("\tpublic Map<String, Object> " + opID + "(@RequestBody String body) throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "(EndpointUtils.getRequestBodyAsJsonElement(body));\n");
          str += ("\t}\n\n");
        } else {
          str += ("\tpublic Map<String, Object> " + opID + "(@PathVariable String " + idString + ") throws ServiceException {\n");
          str += ("\t\treturn INTERFACE_OBJECT." + opID + "(" + idString + ");\n");
          str += ("\t}\n\n");
        }
      }
    }
  }

  return str;
}

function trimPathName(doc, str) {
  var base = getBaseURL(doc);
  if (str.search(base) != -1) {
    str = str.slice(base.length, str.length);
    // str = str.slice(1);
    // if (str.search('/') != -1){
    //   str = str.slice(0, str.indexOf('/'));
    // }
  } else {
    str = str.slice(1);
  }
  return str;
}

// Preserves the CamelCase/UpperCase of each interface name
function replaceInterfaceNames(doc, groupList) {
  for (def in doc['definitions']) {
    for (item in groupList) {
      if ((def.toLowerCase()).search(groupList[item].toLowerCase()) != -1) {
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
