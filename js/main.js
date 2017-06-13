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
var stdfunc = require("./stdfunc.js");

// User Defined Variables
var sourceYML = './openapiexample.yml';

// Serialize YML document
var doc = stdfunc.serializeYML(sourceYML,'./currentJSON.json');

// Create Table based on YML document
stdfunc.createTable(doc, './sql_output.sql');

// TESTING PURPOSES: Create Local Server and listen on port 8080
http.createServer(function(req, res) {
  res.writeHead(200, {
    'Content-Type': 'text/html'
  });
  res.write("API Name: " + doc.info.title + "\n");
  if (doc.info.hasOwnProperty('description')){
    res.write("Description: " + doc.info.description + "\n");
  }
  if (doc.info.hasOwnProperty('contact')){
    if (doc.info.contact.hasOwnProperty('name')){
      res.write("Author: " + doc.info.contact.name + "\n");
    }
  }
  res.end("END RESPONSE");
}).listen(8080);
