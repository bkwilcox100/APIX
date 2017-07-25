const util = require('./util.js');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(doc, destination) {
  var options = {
    serviceName: util.getServiceName(doc)
  };

  fs.readFile('./docs/mustache/readme_template.md', 'utf8', function(err, data) {
    if (err) {
      throw (err);
    }
    fs.writeFile(node_path.join(destination, 'README.md'), mustache.render(data, options), function(err) {
      if (err) {
        throw (err);
      }
      console.log("Readme File Created.");
    });
  });
}
