const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(doc, destination){
  fs.readFile('./docs/mustache/test_template.java', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }
    var options = {
      service_name: util.getServiceName(doc) + "Test"
    };
    var fileName = options.service_name + '.java';
    fs.writeFile(node_path.join(destination, fileName), mustache.render(data, options), function(err){
      if (err){
        throw (err);
      }
      console.log("Test File Created.");
    });
  });
}
