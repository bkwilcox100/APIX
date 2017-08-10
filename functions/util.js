const _ = require('underscore');
const fs = require('fs');
// Utility Functions
exports.toUnderscore = function(string) {
  var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
    return "_" + y.toLowerCase()
  }).replace(/^_/, "");
  return newString;
}

exports.toUnderscoreUpper = function(string) {
  try {
    var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
      return "_" + y.toLowerCase()
    }).replace(/^_/, "");
    return newString.toUpperCase();
  } catch (e) {
    throw e;
  }
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
  var ignoreList = exports.getIgnoreList(doc);
  try {
    for (path in doc['paths']) {
      if (path.search(serviceName) != -1){
        path = path.slice(1, path.length);
        path = path.slice(serviceName.length, path.length);
        path = path.slice(1, path.length);
        path = path.slice(path.indexOf('/'), path.length);
        path = path.slice(1, path.length);
        if (path.indexOf('/') != -1) {
          path = path.slice(0, path.indexOf('/'));
        }
        if (!(_.contains(TLCSet, path))) {
          if (!(_.contains(ignoreList, path))){
            TLCSet.push(path);
          }
        }
      }
    }
    //console.log(TLCSet);
    return TLCSet;
  } catch (err) {
    throw (err);
  }
}

exports.isTLC = function(doc, def){
  var list = this.getTLC(doc);
  if (def){
    for (x in list){
      if (list[x].search(def) != -1){
        return true;
      } else if (list[x].search(def.toLowerCase()) != -1){
        return true;
      }
    }
    // if (_.contains(list, def)){
    //   return true;
    // } else if (_.contains(list, def.toLowerCase())){
    //   return true;
    // }
  }
  return false;
}

exports.getID = function(doc, def){
  var foundID = false;
  if (doc['definitions'].hasOwnProperty(def)){
    if (doc['definitions'][def].hasOwnProperty('properties')){
      for (prop in doc['definitions'][def]['properties']){
        if (prop.slice(-2, prop.length).toLowerCase() == "id"){
          return prop;
        }
      }
    }
  }
  return "id";
}

exports.isRequired = function(doc, def, prop){
  if (doc['definitions'][def].hasOwnProperty('required')){
    for (x in doc['definitions'][def]['required']){
      if (doc['definitions'][def]['required'][x] == prop){
        return true;
      }
    }
  }
  return false;
}

exports.getSQLTimeStamp = function(name){
  var date = new Date();
  var year = date.getFullYear();
  var month = date.getMonth();
  var day = date.getDate();
  if (date.getHours() < 10){
    if (date.getMinutes() < 10){
      var time = "0" + date.getHours() + "" + "0" + date.getMinutes();
    } else {
      var time = "0" + date.getHours() + "" + date.getMinutes();
    }
  } else {
    if (date.getMinutes() < 10){
      var time = date.getHours() + "" + "0" + date.getMinutes();
    } else {
      var time = date.getHours() + "" + date.getMinutes();
    }
  }

  var timestamp = ("V" + String(year).slice(-2) + String('0' + month).slice(-2) + day + "." + time);
  return timestamp;
}

exports.getServiceName = function(doc) {
  try {
    return doc['tags'][0]['description'];
  } catch (e) {
    console.error("No Service Name Found");
  }
  return "NoN";
}

exports.getServiceVersion = function(doc){
  try {
    return doc['info']['version'];
  } catch(e){
    console.error("No Service Version Found");
    return "NoN";
  }
}

exports.getPathName = function(doc, path) {
  var serviceName = exports.getServiceName(doc);
  path = path.slice(1, path.length);
  path = path.slice(serviceName.length, path.length);
  path = path.slice(1, path.length);
  path = path.slice(path.indexOf('/'), path.length);
  path = path.slice(1, path.length);
  if (path.indexOf('/') != -1) {
    path = path.slice(0, path.indexOf('/'));
  }
  return path;
}

exports.matchWithDefinition = function(doc, name){
  for (def in doc['definitions']){
    //console.log(def + " vs " + name);
    if (def.toLowerCase() == name.toLowerCase()){
      return def;
    } else if (def.toLowerCase() == name.slice(0, name.length - 1).toLowerCase()){
      return def;
    }
  }
  return false;
}

exports.getIgnoreList = function(list){
  return fs.readFileSync('./docs/ignore.txt', 'utf8').split('\n');
}

// For Use with Java Method Generation
exports.trimReadCollection = function(str) {
  str = str.slice(4, -10);
  return str;
}

exports.trimReadResource = function(str) {
  val = str;
  val = val.slice(4, -8);
  return val;
}

exports.trimCreateBatch = function(str) {
  if (str.search(/createbatch/i) != -1){
    str = str.slice(11, str.length);
    if (str.slice(-1) == 's'){
      str = str.slice(0, str.length)
    }
  }
  return str;
}

exports.trimUpdateResource = function(str){
  str = str.slice(6, -8)
  return str;
}

exports.trimDeleteResource = function(str){
  str = str.slice(6, -8)
  return str;
}

exports.trimDeleteBatch = function(str){
  str = str.slice(11, -9)
  return str;
}
