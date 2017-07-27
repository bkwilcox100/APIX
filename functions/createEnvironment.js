const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(doc, destination){
  fs.readFile('./docs/mustache/environment_template.properties', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }
    var options = {
      service_name: util.getServiceName(doc)
    };
    var fileName = "environment-addToDefault.properties";
    fs.writeFile(node_path.join(destination, fileName), mustache.render(data, options), function(err){
      if (err){
        throw (err);
      }
      console.log("Environment Created.");
    });
  });
}
