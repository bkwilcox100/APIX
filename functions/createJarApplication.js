const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(doc, destination){
  var mustache_vars = {
    serviceName: util.getServiceName(doc),
    serviceVersion: util.getServiceVersion(doc)
  };

  fs.readFile('./docs/mustache/application_java_template.java', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }

    fs.writeFile(node_path.join(destination, 'Application.java'), mustache.render(data, mustache_vars), function(err){
      if (err){
        throw (err);
      }
      console.log("Application.java Created.");
    });
  });
}
