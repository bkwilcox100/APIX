const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(destination){
  fs.readFile('./docs/mustache/logging_template.properties', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }

    fs.writeFile(node_path.join(destination, 'logging.properties'), data, function(err){
      if (err){
        throw (err);
      }
      console.log("logging.properties Created.");
    });
  });
}
