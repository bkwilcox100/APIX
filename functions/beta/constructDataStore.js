const fs = require('fs');
const mustache = require('mustache');

exports.create = function(doc){
var options = {
  data_store: exports.constructDataType(doc)
};
var base = fs.readFileSync('./docs/beta/ds_base_template.xml', 'utf8');
var output = mustache.render(base, options)

console.log(output);
}

exports.constructDataType = function(doc){
  var output = "";

  var data_type_base = fs.readFileSync('./docs/beta/ds_datatype_template.xml', 'utf8');
  for (def in doc['definitions']){
    var options = {
      definition_name: def,
      tables: " "
    };
    output += mustache.render(data_type_base, options);
  }
  return output;
}

exports.constructTables = function(doc, def){
  
}
