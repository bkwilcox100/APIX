const util = require('./util.js');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(doc, destination){
  var options = {
    serviceName: util.getServiceName(doc),
    serviceVersion: util.getServiceVersion(doc),
    serviceTitle: doc['info']['title'] || util.getServiceName(doc)
  };

  fs.readFile('./docs/mustache/pom_template.xml', 'utf8', function(err, data){
    if (err){
      throw (err);
    }
    fs.writeFile(node_path.join(destination, 'pom.xml'), mustache.render(data, options), function(err){
      if (err){
        throw (err);
      }
      console.log("POM.xml Created.");
    });
  });
}
