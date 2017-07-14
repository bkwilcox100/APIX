const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(title_var, description_var, version_var, service_name_var){
  var mustache_vars = {
    title: title_var,
    description: description_var,
    version: version_var,
    serviceName: service_name_var
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
