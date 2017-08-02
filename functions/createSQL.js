const fs = require('fs');
const _ = require('underscore');
const util = require('./util.js');

var debugMode = true;

exports.create = function(source, destination) {
  var definitions = getFinalDefinitionList(source);
  var output = "use middle_layer;\n\n";
  var parent = {
    child: "",
    reference: ""
  };
  for (def in definitions) {
    output = generateTable(output, source, definitions[def], parent);
  }

  var fileName = destination + util.getSQLTimeStamp() + "__" + util.getServiceName(source) + ".sql";
  fs.writeFile(fileName, output, function(error) {
    if (error) {
      throw error;
    }
    console.log("SQL Write Successful");
  });
}

function getFinalDefinitionList(doc) {
  var ignoreList = util.getIgnoreList();
  var finalList = [];
  for (def in doc['definitions']) {
    if (!(_.contains(ignoreList, def.toLowerCase()))) {
      finalList.push(def);
    }
  }

  return finalList;
}

function generateTable(str, doc, defName, parent) {
  var size = 1024;
  //  since this is always making new tables, drop them if they are already there
  str += "drop table if exists " + util.toUnderscore(defName) +";\n";

  // create the new table
  str += "CREATE TABLE IF NOT EXISTS heb_" + util.toUnderscore(defName) + " (\n";
  if (parent.child == defName) {
    var childID = 'id';
    for (propName in doc['definitions'][defName]['properties']) {
      if (propName.search(/id/i) != -1) {
        childID = propName;
      }
    }
    str += ("\t" + util.toUnderscore(childID) + " varchar(" + getPropSize(doc, defName, childID) + ") not null,\n");
    // TODO: add a check to see if the parent back link is in the spec.  If it is, skip it and just use this one.
    str += ("\t" + util.toUnderscore(util.getID(doc, parent.reference)) + " varchar(" + getPropSize(doc, parent.reference, util.getID(doc, parent.reference)) + ") not null,\n");

    for (propName in doc['definitions'][defName]['properties']) {
      if (doc['definitions'][defName]['properties'][propName].hasOwnProperty('type')) {
        if (doc['definitions'][defName]['properties'][propName]['type'] == 'array') {
          parent.child = doc['definitions'][defName]['properties'][propName]['items']['$ref'];
          parent.child = parent.child.slice(parent.child.lastIndexOf('/') + 1, parent.child.length);
          parent.reference = defName;
        }
      }

      if (propName.search(/id/i) == -1) {
        size = getPropSize(doc, defName, propName);
        // completely ignore any creation or last modified date that is defined in the input spec
        // because they are always required in the database despite if they are used in the spec.
        if (propName.toLowerCase().indexOf('creationdate') < 0 &&
          		propName.toLowerCase().indexOf('creation_date')  < 0 &&
          		propName.toLowerCase().indexOf('lastmodifieddate')  < 0 &&
          		propName.toLowerCase().indexOf('last_modified_date')  < 0) {
          if (util.isRequired(doc, defName, propName) || _.contains(doc['definitions'][defName]['required'], propName)) {
            str += ("\t" + util.toUnderscore(propName) + " varchar(" + size + ") not null,\n");
          } else {
            str += ("\t" + util.toUnderscore(propName) + " varchar(" + size + "),\n");
          }
        } else {
        	console.log("createSQL: creation_date or last_modified_date found in spec, but skipped to be added to flyway script explicitly");
        }
      }
    }
  } else {
    for (propName in doc['definitions'][defName]['properties']) {
      if (doc['definitions'][defName]['properties'][propName].hasOwnProperty('type')) {
        if (doc['definitions'][defName]['properties'][propName]['type'] == 'array') {
          if (doc['definitions'][defName]['properties'][propName]['type'].hasOwnProperty('items')){
            parent.child = doc['definitions'][defName]['properties'][propName]['items']['$ref'];
            parent.child = parent.child.slice(parent.child.lastIndexOf('/') + 1, parent.child.length);
            parent.reference = defName;
          }
        }
      }
      size = getPropSize(doc, defName, propName);
      // completely ignore any creation or last modified date that is defined in the input spec
      // because they are always required in the database despite if they are used in the spec.
      if (propName.toLowerCase().indexOf('creationdate') < 0 &&
      		propName.toLowerCase().indexOf('creation_date')  < 0 &&
      		propName.toLowerCase().indexOf('lastmodifieddate')  < 0 &&
      		propName.toLowerCase().indexOf('last_modified_date')  < 0) {
        if (util.isRequired(doc, defName, propName) || _.contains(doc['definitions'][defName]['required'], propName)) {
          str += ("\t" + util.toUnderscore(propName) + " varchar(" + size + ") not null,\n");
        } else {
          str += ("\t" + util.toUnderscore(propName) + " varchar(" + size + "),\n");
        }
      } else {
      	console.log("createSQL: creation_date or last_modified_date found in spec, but skipped to be added to flyway script explicitly");
      }
    }
  }

  //  These are required at all times for all Liquid Sky tables.
  str += ("\tcreation_date datetime default current_timestamp,\n");
  str += ("\tlast_modified_date datetime default current_timestamp on update current_timestamp,\n");

  str += ("\tprimary key (" + util.toUnderscore(util.getID(doc, defName)) + ")\n");

  str += ");\n\n";
  return str;
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
