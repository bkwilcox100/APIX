const util = require('./util.js');
const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(titleV, descriptionV, versionV, serviceNameV){
  var mustache_vars = {
    title: titleV,
    description: descriptionV,
    version: versionV,
    serviceName: serviceNameV
  };

  fs.readFile('./docs/mustache/init_template.yaml', 'utf8', function(err, data){
    if (err){
      throw (err);
    }
    fs.writeFile(node_path.join(__dirname, 'openapi.yaml'), mustache.render(data, mustache_vars), function(err){
      if (err){
        throw (err);
      }
      console.log('Open API Spec Generation Successful');
      });
    });

}
