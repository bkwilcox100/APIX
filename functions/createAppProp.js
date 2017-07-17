const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(destination){
  fs.readFile('./docs/mustache/app_prop_template.properties', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }

    fs.writeFile(node_path.join(destination, 'application.properties'), data, function(err){
      if (err){
        throw (err);
      }
      console.log("application.properties Created.");
    });
  });
}
