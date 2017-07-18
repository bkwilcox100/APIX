const fs = require('fs');
const _ = require('underscore');
const util = require('./util.js');

exports.create = function(source, destination) {
  var definitions = getFinalDefinitionList(source);
  var output = "use middle_layer;\n\n";
  for (def in definitions) {
    output = generateTable(output, source, definitions[def]);
  }

  var fileName = destination + util.getSQLTimeStamp() + "_" + util.getServiceName(source) + ".sql";
  fs.writeFile(fileName, output, function(error) {
    if (error) {
      throw error;
    }
    console.log("SQL Write Successful");
  });
}

function getFinalDefinitionList(doc) {
  var ignoreList = ['successmessage', 'message', 'errorresponse', 'batchresponse', 'jsonmap'];
  var finalList = [];
  for (def in doc['definitions']) {
    if (!(_.contains(ignoreList, def.toLowerCase()))) {
      finalList.push(def);
    }
  }

  return finalList;
}

function generateTable(str, doc, defName) {
  var size = 1024;

  str += "CREATE TABLE IF NOT EXISTS heb_" + util.toUnderscore(defName) + " (\n";
  for (propName in doc['definitions'][defName]['properties']) {
    size = getPropSize(doc, defName, propName);
    if (util.isRequired(doc, defName, propName) || _.contains(doc['definitions'][defName]['required'], propName)) {
      if(propName.toLowerCase().search('date') != -1){
        str += ("\t" + propName + " datetime default current_timestamp not null,\n");
      } else {
        str += ("\t" + propName + " varchar(" + size + ") not null,\n");
      }
    } else {
      if(propName.toLowerCase().search('date') != -1){
        str += ("\t" + propName + " datetime default current_timestamp,\n");
      } else {
        str += ("\t" + propName + " varchar(" + size + "),\n");
      }
    }
  }
  str += ("\tprimary key (" + util.getID(doc, defName) + ")\n");

  str += ");\n\n";
  return str
}

function getPropSize(doc, def, prop) {

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
