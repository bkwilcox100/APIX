const util = require('./util.js');
const _ = require('underscore');
const node_path = require('path');
const fs = require('fs');

exports.create = function(doc) {
  var output = "";

  output = generateHeader(doc, output);
  
  console.log(output);
}

function getPaths(doc){
  var pathList = [];
  for (path in doc['paths']){
    pathList.push(path);
  }
  return pathList;
}

function getBaseURL(doc){
  var str = util.getServiceName(doc);
  str = '/' + str + '/v1';
  return str;
}

function generateHeader(doc, str){
  str += "@RestController\n";
  str += ("@RequestMapping(value=\"" + getBaseURL(doc) + "\")\n");
  return str;
}

function generateServlet(doc, str, pathName){

  return str;
}

function trimPathName(doc, str){
  var base = getBaseURL(doc);
  if (str.search(base) != -1){
    str = str.slice(base.length, str.length);
    str = str.slice(1);
    if (str.search('/') != -1){
      str = str.slice(0, str.indexOf('/'));
    }
  } else {
    str = str.slice(1);
  }
  return str;
}
