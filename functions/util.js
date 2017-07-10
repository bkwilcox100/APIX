const _ = require('underscore');
// Utility Functions
exports.toUnderscore = function(string) {
  var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
    return "_" + y.toLowerCase()
  }).replace(/^_/, "");
  return newString;
}

exports.toUnderscoreUpper = function(string) {
  var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
    return "_" + y.toLowerCase()
  }).replace(/^_/, "");
  return newString.toUpperCase();
}

exports.toCamelCase = function capitalizeFirstLetter(string) {
  return string.charAt(0).toLowerCase() + string.slice(1);
}

exports.getArrTableName = function(str) {
  var lastIndex = str.lastIndexOf('/');
  lastIndex++;
  var newStr = str.slice(lastIndex, str.length);
  return newStr;
}

exports.getTLC = function(doc) {
  var serviceName = exports.getServiceName(doc);
  var TLCSet = [];
  try {
    for (path in doc['paths']) {
      path = path.slice(1, path.length);
      path = path.slice(serviceName.length, path.length);
      path = path.slice(1, path.length);
      path = path.slice(path.indexOf('/'), path.length);
      path = path.slice(1, path.length);
      if (path.indexOf('/') != -1) {
        path = path.slice(0, path.indexOf('/'));
      }
      if (!(_.contains(TLCSet, path))) {
        TLCSet.push(path);
      }
    }
    //console.log(TLCSet);
    return TLCSet;
  } catch (err) {
    throw "SPEC-ERROR: No Paths found";
  }
}

exports.isTLC = function(doc, def){
  var list = this.getTLC(doc);
  if (_.contains(list, def)){
    return true;
  }
  return false;
}

exports.getID = function(doc, def){
  var foundID = false;
  for (prop in doc['definitions'][def]['properties']){
    if (prop.slice(-2, prop.length).toLowerCase() == "id"){
      return prop;
    }
  }
  return "NO_ID";
}

/*
Name: getServiceName
Description: Gets service name from OA Spec
Parameters:
- doc : JSON object of OA Spec
Preconditions:
- doc exists
Postconditions:
- None
Return: String
Status: COMPLETE
*/
exports.getServiceName = function(doc) {
  try {
    return doc['tags'][0]['description'];
  } catch (e) {
    console.error("No Service Name Found");
  }
  return "NoN";
}
