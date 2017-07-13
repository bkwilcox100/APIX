const util = require('./util.js');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(doc, destination){
  var options = {
    serviceName: util.getServiceName(doc),
    runtime: "java",
    environment: "flex",
    jdk: "openjdk8",
    isThreadSafe: true
  };

  fs.readFile('./docs/mustache/app_engine_template.yaml', 'utf8', function(err, data){
      if (err){
        throw (err);
      }
      fs.writeFile(node_path.join(destination, 'app.yaml'), mustache.render(data, options), function(err){
        if (err){
          throw (err);
        }
        console.log("App Engine Spec Created.");
        });
    });
}
