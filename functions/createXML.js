const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');

exports.create = function(source, destination) {
  // List of definitions to ignore
  var ignoreList = ['successmessage', 'message', 'errorresponse', 'batchresponse', 'auditlogentry', 'jsonmap'];

  var output = "";
  output = generateStatic();
  for (def in source['definitions']) {
    if (!(_.contains(ignoreList, def.toLowerCase()))) {
      output = generateCommentBlock(output, def);
      output = generateDataTypeRow(output, source, def);
      output = generateTableNameRow(output, source, def);
      if (source['definitions'][def].hasOwnProperty('properties')) {
        for (prop in source['definitions'][def]['properties']) {
          if (source['definitions'][def]['properties'][prop]['type'] != "array") {
            output = generateColumnNameRow(output, source, def, prop);
          }
        }
      }
      if (source['definitions'][def].hasOwnProperty('properties')) {
        for (prop in source['definitions'][def]['properties']) {
          if (source['definitions'][def]['properties'][prop]['type'] == "array") {
            output = generateReferenceTable(output, source, def, prop);
          }
        }
      }
      output = endTableDataType(output, source, def);
    }
  }
  output = endDataStore(output);
  fs.writeFile(node_path.join(destination, 'data-store.xml'), output, function(err) {
    if (err) {
      throw (err);
    }
    console.log('Data Store Written Successfully');
  });
}

function generateStatic() {
  var tempPath = node_path.join('.', 'docs', 'xml', 'xml_ls_template.xml');
  return fs.readFileSync(tempPath, 'utf8');
}

function generateCommentBlock(str, name) {
  str += "\n<!--\n==========================================================\n";
  str += "Definitions of resources for " + name + "\n";
  str += "==========================================================\n-->\n";
  return str;
}

function generateDataTypeRow(str, doc, name) {
  var idString = "";
  str += "<data-type "
  str += ("name=\"" + util.toCamelCase(name) + "\" ");

  if (doc['definitions'][name].hasOwnProperty('properties')) {
    idString = util.getID(doc, name);
  } else {
    idString = "id";
  }
  str += ("id-property=\"" + idString + "\" ");
  str += ("id-generator-prefix=\"\" ");
  str += ("use-id-generator=\"" + true + "\" ");
  str += ("pub-sub-enabled=\"" + false + "\" ");
  str += ("fcm-enabled=\"" + false + "\"");
  str += ">\n";
  return str;
}

function generateTableNameRow(str, doc, name) {
  str += ("\t<table name=\"heb_" + util.toUnderscore(name) + "\" ");
  str += "type=\"primary\" ";
  str += ("id-column=\"" + util.toUnderscore(util.getID(doc, name)) + "\">\n");
  return str;
}

function generateColumnNameRow(str, doc, defName, propName) {
  // Default values
  var maxLength = getMaxSize(doc, defName, propName);
  var minLength = getMinSize(doc, defName, propName);
  str += ("\t\t<column column-name=\"" + util.toUnderscore(propName) + "\" property=\"" + propName + "\">\n");
  str += ("\t\t\t<attribute name=\"requiredProperty\" value=\"" + util.isRequired(doc, defName, propName) + "\"/>\n");
  str += ("\t\t\t<attribute name=\"restrictedProperty\" value=\"false\"/>\n");
  str += ("\t\t\t<attribute name=\"maxLength\" value=\"" + maxLength + "\"/>\n");
  str += ("\t\t\t<attribute name=\"minLength\" value=\"" + minLength + "\"/>\n");
  str += "\t\t</column>\n";
  return str;
}

function endTable(str) {
  str += "\t</table>\n\n";
  return str;
}

function endTableDataType(str, doc, defName) {
  str += "\t</table>\n\n";
  if (util.isTLC(doc, defName)) {
    str += "\t<named-query name=\"all_" + util.toUnderscore(defName) + "\">\n";
    str += "\t\tselect " + util.toUnderscore(util.getID(doc, defName)) + " from heb_" + util.toUnderscore(defName) + ";\n";
    str += "\t</named-query>\n\n"
  }
  str += "</data-type>\n";
  return str;
}

function endDataStore(str) {
  str += "</data-store>";
  return str;
}
//TODO: Combine EndTable with generateReferenceTable
function generateReferenceTable(str, doc, def, prop) {
  var child = doc['definitions'][def]['properties'][prop]['items']['$ref'];
  child = child.slice(child.lastIndexOf('/') + 1, child.length);
  var primaryChildKey = util.getID(doc, child);

  var dataType =  doc['definitions'][child]['type'];
  str += ("\n\t<table name=\"heb_" + util.toUnderscore(prop) + "\" ");
  str += "type=\"reference\" ";
  str += ("id-column=\"" + util.toUnderscore(util.getID(doc, def)) + "\">\n");
  str += ("\t\t<column column-name=\"" + util.toUnderscore(primaryChildKey) + "\" list-item-type=\"" + dataType + "\" property=\"" + prop + "\" read-only=\"true\" cascade=\"true\" />\n");
  str += ("\t\t<column column-name=\"" + util.toUnderscore(util.getID(doc, def)) + "\" item-type=\"" + def + "\" property=\"parent\" />\n");
  str = endTable(str);
  return str;
}

function getMaxSize(doc, def, prop) {

  if (doc['definitions'][def]['properties'][prop].hasOwnProperty('maxLength')) {
    return doc['definitions'][def]['properties'][prop]['maxLength'];

  } else if (doc['definitions'][def]['properties'][prop].hasOwnProperty('type')) {
    var type = doc['definitions'][def]['properties'][prop]['type'];
    var size;

    switch (type) {
      case "string":
        size = 1024;
        break;

      case "integer":
        size = 64;
        break;

      case "number":
        size = 64;
        break;

      case "float":
        size = 64;
        break;

      case "boolean":
        size = 1;
        break;

      default:
        size = 64;

    }
    return size;
  } else {
    return 1024;
  }

}

function getMinSize(doc, def, prop) {

  if (doc['definitions'][def]['properties'][prop].hasOwnProperty('minLength')) {
    return doc['definitions'][def]['properties'][prop]['minLength'];

  } else {
    return 0;
  }

}
