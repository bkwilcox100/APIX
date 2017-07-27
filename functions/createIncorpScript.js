const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');
const util = require('./util.js');

exports.create = function(doc, destination){
  fs.readFile('./functions/incorporateNewProject.sh', 'utf8', function(err, data){
    if (err) {
      throw (err);
    }
    fs.writeFile(node_path.join(destination, 'incorporateNewProject.sh'), data, function(err){
      if (err){
        throw (err);
      }
      console.log("Environment Created.");
    });
  });
}
