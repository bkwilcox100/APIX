/*
Name: APIX or API Expander
Author: Brandon Wilcox
Date Created: 6/8/17
Date Modified: 6/13/17
*/

/*
NOTES:
- The ID of every definitions MUST end in "ID" (case insensitive)
- Any date objects MUST end in "date" or "Date"
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

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");
var generateJava = require("./functions/createJava.js").create;
var generateXML = require('./functions/createXML.js').create;
var generateSQL = require('./functions/createSQL.js').create;

// User Defined Variables
var src = '/Users/brandonwilcox/Downloads/openapi-example-1.yaml';
var dest = '/Users/brandonwilcox/Desktop';

// Serialize YML document
var doc = serialize.YML(src);

generateSQL(doc, dest);


// TESTING PURPOSES: Create Local Server and listen on port 8080
// http.createServer(function(req, res) {
//   res.writeHead(200, {
//     'Content-Type': 'text/html'
//   });
//   res.write('<div style="margin-top:40vh;text-align:center">')
//     res.write('  <form method=get action=/ style="font:3em monospace">')
//     res.write('    <span style="color:#F08">$</span> npm repo')
//     res.write('    <input name=pkgName placeholder=pkgname size=10 style="font:1em monospace;color:#777;border:0">')
//     res.write('</form>')
//     res.write('</div>')
//     res.end()
// }).listen(8080);
