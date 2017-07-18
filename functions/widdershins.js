const converter = require('widdershins');
const serialize = require('./serialize.js');
const fs = require('fs');
var options = {
  codeSamples: true,
  templateCallback: function(templateName,stage,data){
    return data;
  },
  theme: 'darkula',
  search: true,
  sample: true,
  discovery: false,
  includes: []
};

var src = '../middle-layer/heb-liquidsky-service-adminrest/openapi.yaml';
var doc = serialize.YML(src);

fs.writeFile('/Users/brandonwilcox/Desktop/slate.md', converter.convert(doc, options), function(err){
  if (err){
    throw (err);
  }
  console.log("Success");
});
