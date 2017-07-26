const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');
exports.create = function(doc, destination) {
var pathString = "";
var defString = "";
pathString = generatePaths(doc);
defString = generateDefinitions(doc);

var options = {
  service_name: util.getServiceName(doc),
  description: doc['info']['description'] || "No description",
  author: doc['host'] || "No Host specified",
  paths: pathString,
  definitions: defString
};
fs.readFile('./docs/mustache/html_template.html', 'utf8', function(err, html_temp){
  if (err){
    throw (err);
  }
  fs.writeFile(node_path.join(destination, 'docs.html'), mustache.render(html_temp, options), function(err){
    if (err){
      throw (err);
    }
    console.log("HTML Write Success");
  });
  fs.readFile('./html/resources/heb_logo.png', function(err, img){
    if (err){
      throw (err);
    }
    fs.writeFile(node_path.join(destination, 'heb_logo.png'), img, function(err){
      if (err){
        throw (err);
      }
    });
  });
});
}

function generatePaths(doc) {
  var wholeTable = "<table>\n <tr><th>Name</th><th>GET</th><th>POST</th><th>PUT</th><th>DELETE</th></tr><tr>\n";
  for (path in doc['paths']){
    wholeTable += ("<tr>\n")
    wholeTable += ("<td>" + path + "</td>\n");
    wholeTable += ("<td>" + doc["paths"][path].hasOwnProperty('get') + "</td>\n");
    wholeTable += ("<td>" + doc["paths"][path].hasOwnProperty('post') + "</td>\n");
    wholeTable += ("<td>" + doc["paths"][path].hasOwnProperty('put') + "</td>\n");
    wholeTable += ("<td>" + doc["paths"][path].hasOwnProperty('delete') + "</td>\n");
    wholeTable += "</tr>\n";
  }
  wholeTable += "</tr></table>\n";
  return wholeTable;
}

function generateDefinitions(doc){
  var wholeTable = "<table>\n <tr><th>Name</th><th>Properties</th><th>Required</th></tr>\n";
  wholeTable += ("<tr>\n");
  for(def in doc["definitions"]){
    wholeTable += ("<td>" + def + "</td>\n");
    wholeTable += "<td>";
    if (doc["definitions"][def].hasOwnProperty("properties")){
      for (prop in doc["definitions"][def]["properties"]){
        wholeTable += (prop + ", ");
      }
    } else {
      wholeTable += "none, ";
    }
    wholeTable = wholeTable.slice(0, wholeTable.length - 2);
    wholeTable += "</td>\n";
    wholeTable += "<td>";
    if (doc["definitions"][def].hasOwnProperty("required")){
      for (prop in doc["definitions"][def]["required"]){
        wholeTable += (doc["definitions"][def]["required"][prop] + ", ");
      }
    } else {
      wholeTable += "none, ";
    }
    wholeTable = wholeTable.slice(0, wholeTable.length - 2);
    wholeTable += "</td>\n";
    wholeTable += "</tr>\n";
  }
  wholeTable += "</tr></table>";
  return wholeTable;
}
