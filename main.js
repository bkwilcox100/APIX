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

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");

// User Defined Variables
var sourceYML = './docs/yml/openapiexample.yml';
var sourceXML = './docs/xml/data-store.xml';

// Serialize YML document
var doc = stdfunc.serializeYML('/Users/brandonwilcox/Downloads/openapi.yaml');

// Create Table based on YML document
//stdfunc.createTable(doc, './docs/sql/sql_output.sql');

//stdfunc.createXML(doc, "./docs/xml/G_XML.xml");

//stdfunc.serializeXML('./docs/xml/G_XML.xml', './docs/json/G_JSON.json');

//stdfunc.createJava(doc);

stdfunc.createJava(doc, '/Users/brandonwilcox/Downloads/');

//console.log(stdfunc.getServiceName(doc));

//console.log(stdfunc.getTLC(doc));

// TESTING PURPOSES: Create Local Server and listen on port 8080
// http.createServer(function(req, res) {
//   res.writeHead(200, {
//     'Content-Type': 'text/html'
//   });
//   res.write("API Name: " + doc.info.title + "\n");
//   if (doc.info.hasOwnProperty('description')) {
//     res.write("Description: " + doc.info.description + "\n");
//   }
//   if (doc.info.hasOwnProperty('contact')) {
//     if (doc.info.contact.hasOwnProperty('name')) {
//       res.write("Author: " + doc.info.contact.name + "\n");
//     }
//   }
//   res.write("YML Parse: COMPLETE\n");
//   res.write("SQL Table Create: COMPLETE\n");
//   res.write("XML Parse: COMPLETE\n")
//   res.end("END RESPONSE");
// }).listen(8080);