#! usr/bin/env node

// Modules
var http = require('http');         // For testing on local server
var fs = require('fs');             // For interacting with file system
var path = require('path');         // For interacting with file system
var yam = require('js-yaml');       // For parsing the YML files
var xml2js = require('xml2js');     // For parsing XML files (Remove)
var _ = require('underscore');      // Utility Functions Library
var clear = require('clear');       // Clears screen before start of program
var chalk = require('chalk');       // For colorful UI
var mkdirp = require('mkdirp');     // For creating multiple directories

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");
var files = require("./functions/files.js");

// Global Variables
var sourcePath = "";
var destPath = "";
var isPathValid = false;
var serviceName = "";
var doc = null;

// Clear Terminal Window
clear();

// Display Welcome Message
console.log(chalk.green("Welcome to APIX!"));

// TODO: Redesign Path/Validation process

// Recieve Input and Output Paths and Validate
files.getPaths(function() {
  sourcePath = arguments['0']['sourcePath'];
  destPath = arguments['0']['destPath'];

  // Eliminate Whitespace
  sourcePath = sourcePath.replace(/ /g, '');
  destPath = destPath.replace(/ /g, '');

  // In add / to end of path if not already there
  if (destPath.substr(-1, 1) != "/") {
    destPath += '/';
  }

  // Validate Paths
  files.fileExists(sourcePath).then(function(msg) {
    if (path.extname(sourcePath) != '.yml' && path.extname(sourcePath) != '.yaml') {
      throw new Error("Source is not a YML file");
    }
    console.log(sourcePath + " is Valid");
    doc = stdfunc.serializeYML(sourcePath);
    serviceName = ('heb-liquidsky-' + stdfunc.getServiceName(doc));
  }).catch(function(msg) {
    console.error("Specified Source is Invalid: ", msg);
  });

  files.directoryExists(destPath).then(function(msg) {
    console.log(destPath + " is Valid");
    // Create Output Folder
    if (!(fs.existsSync(destPath + serviceName))) {
      fs.mkdirSync(destPath + serviceName);
    }
    destPath += (serviceName + "/");
    console.log('Created Output Folder | New Path: ' + destPath);

    console.log('\nGenerating File Hierarchy...\n');

    stdfunc.createTable(doc, destPath);
    fs.writeFileSync(destPath + 'oaspec.yml', fs.readFileSync(sourcePath));

    // BEGIN CREATING FILE STRUCTURE
    mkdirp(destPath + 'src/main/appengine/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/main/appengine/ created");

      // Generation Functions Below
    });

    mkdirp(destPath + 'src/main/java/com/heb/liquidsky/data/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/main/java/com/heb/liquidsky/data/ created");

      // Generation Functions Below
      stdfunc.createXML(doc, destPath + 'src/main/java/com/heb/liquidsky/data/');
    });

    mkdirp(destPath + 'src/main/java/com/heb/liquidsky/endpoints/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/main/java/com/heb/liquidsky/endpoints/ created");

      // Generation Functions Below
      stdfunc.createJava(doc, (destPath + 'src/main/java/com/heb/liquidsky/endpoints/'));
    });

    mkdirp(destPath + 'src/main/java/com/heb/liquidsky/spring/web/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/main/java/com/heb/liquidsky/spring/web/ created");

      // Generation Functions Below
    });

    mkdirp(destPath + 'src/main/resources/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/main/resources/ created");

      // Generation Functions Below
    });

    mkdirp(destPath + 'src/test/java/com/heb/liquidsky/data/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/test/java/com/heb/liquidsky/data/ created");

      // Generation Functions Below
    });

    mkdirp(destPath + 'src/test/resources/', function(err){
      if (err){
        throw err;
      }
      // Confirm directories were created
      console.log("/src/test/resources/ created");

      // Generation Functions Below
    });
    // END CREATE FILE STRUCTURE

  }).catch(function() {
    console.error("Specified Destination is Invalid");
  });
});
