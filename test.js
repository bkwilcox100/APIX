/*
NOTES:
- The ID of every definitions MUST end in "ID" (case insensitive)
- primary key must be first property and end in ID
*/

// Modules
const http = require('http');
const fs = require('fs');
const yam = require('js-yaml');
const _ = require('underscore');
const files = require('./functions/files.js');
const serialize = require('./functions/serialize.js');
const util = require('./functions/util.js');
const pug = require('pug');

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");
var generateJava = require("./functions/createJavaInterface.js").create;
var generateXML = require('./functions/createXML.js').create;
var generateSQL = require('./functions/createSQL.js').create;
var generateServlet = require('./functions/createJavaServlet.js').create;
var generateAE = require('./functions/createAppEngineSpec.js').create;
var generateHTML = require('./functions/createHTML.js').create;

// User Defined Variables
var src = './middle-layer/heb-liquidsky-service-adminrest/openapi.yaml';
var dest = '/Users/brandonwilcox/Desktop';

var object = serialize.YML(src);
generateHTML(object);


// http.createServer(function(req, res) {
//   res.writeHead(200, {
//     'Content-Type': 'text/html'
//     'Content-Type': 'text/html'
//   });
//   res.write(pug.renderFile('./docs/pug/html_template.pug'));
//   res.write('<div style="margin-top:40vh;text-align:center">');
//   res.write('  <form method=get action=/ style="font:3em monospace">');
//   res.write('    <span style="color:#F08">$</span> npm repo');
//   res.write('    <input name=pkgName placeholder=pkgname size=10 style="font:1em monospace;color:#777;border:0">');
//   res.write('  </form>');
//   res.write('</div>');
//   res.end();
// }).listen(8080);
