const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(doc, destination){
  fs.readFile('./docs/mustache/test_template.java', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }
    var fileName = util.getServiceName(doc) + 'Test.java';
    fs.writeFile(node_path.join(destination, fileName), data, function(err){
      if (err){
        throw (err);
      }
      console.log("Test File Created.");
    });
  });
}
