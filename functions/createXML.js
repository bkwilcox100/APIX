const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');

exports.create = function(source, destination) {
  var output = "";
  output = generateStatic();

  for (def in source['definitions']) {
    output = generateCommentBlock(output, def);
    output = generateDataTypeRow(output, source, def);
    output = generateTableNameRow(output, source, def);
    if (source['definitions'][def].hasOwnProperty('properties')){
      for (prop in source['definitions'][def]['properties']){
          output = generateColumnNameRow(output, source, def, prop);
      }
    }
    output = endTableDataType(output);
  }
  output = endDataStore(output);
  fs.writeFile(node_path.join(destination, 'data-store.xml'), output, function(err){
    if (err){
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

function generateTableNameRow(str, doc, name){
  str += ("\t<table name=\"heb_" + util.toUnderscore(name) + "\" ");
  // Check if TLC, if so set to primary, else reference
  if (util.isTLC(doc, name)){
    str += "type=\"primary\" ";
  } else {
    str += "type=\"reference\" ";
  }
  str += ("id-column=\"" + util.toUnderscore(util.getID(doc, name)) + "\">\n");
  return str;
}

function generateColumnNameRow(str, doc, defName, propName){
  // Default values
  var maxLength = 64;
  var minLength = 0;
  str += ("\t\t<column column-name=\"" + util.toUnderscore(propName) + "\" property=\"" + propName + "\">\n");
  str += ("\t\t\t<attribute name=\"requiredProperty\" value=\"" + util.isRequired(doc, defName, propName) + "\"/>\n");
  str += ("\t\t\t<attribute name=\"restrictedProperty\" value=\"false\"/>\n");
  str += ("\t\t\t<attribute name=\"maxLength\" value=\"" + maxLength + "\"/>\n");
  str += ("\t\t\t<attribute name=\"minLength\" value=\"" + minLength + "\"/>\n");
  str += "\t\t</column>\n";
  return str;
}

function endTableDataType(str){
  str += "\t</table>\n\n";
  str += "</data-type>\n";
  return str;
}

function endDataStore(str){
  str += "</data-store>";
  return str;
}
