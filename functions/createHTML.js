const node_path = require('path');
const fs = require('fs');
const mustache = require('mustache');

exports.create = function(destination, options) {

  fs.readFile('./docs/mustache/html_template.html', 'utf8', function(err, data) {
    if (err) {
      throw (err);
    }
    fs.writeFile(node_path.join(destination, 'doc.html'), mustache.render(data, options), function(err) {
      if (err) {
        throw (err);
      }
      console.log('HTML Generation Successful');
    });
  });
}
