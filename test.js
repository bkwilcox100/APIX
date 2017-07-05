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
var http = require('http');
var fs = require('fs');
var yam = require('js-yaml');
var _ = require('underscore');

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");

// User Defined Variables
var sourceYML = './docs/yml/openapiexample.yml';
var sourceXML = './docs/xml/data-store.xml';

// Serialize YML document
var doc = stdfunc.serializeYML('./docs/yml/openapi.yaml');
stdfunc.createJava(doc, '/Users/brandonwilcox/Desktop/')

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
