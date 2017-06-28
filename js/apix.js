#! usr/bin/env node

// Modules
var http = require('http');
var fs = require('fs');
var path = require('path');
var yam = require('js-yaml');
var xml2js = require('xml2js');
var CLI = require('CLUI');
var _ = require('underscore');
var clear = require('clear');
var chalk = require('chalk');
var Spinner = CLI.Spinner;

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");
var files = require("./functions/files.js");

// Global Variables
var sourcePath = "";
var destPath = "";
var isPathValid = false;

// Clear Terminal Window
clear();

// Display Welcome Message
console.log(chalk.yellow("Welcome to APIX!"));

// Recieve Input and Output Paths and Validate
files.getPaths(function() {
  sourcePath = arguments['0']['sourcePath'];
  destPath = arguments['0']['destPath'];

  // Eliminate Whitespace
  sourcePath = sourcePath.replace(/ /g, '');
  destPath = destPath.replace(/ /g, '');

  // In add / to end of path if not already there
  // if (sourcePath.substr(-1, 1) != "/"){
  //   sourcePath += '/';
  // }
  if (destPath.substr(-1, 1) != "/") {
    destPath += '/';
  }

  // Validate Paths
  files.fileExists(sourcePath).then(function(msg) {
    if (path.extname(sourcePath) != '.yml') {
      throw new Error("Source is not a YML file");
    }
    console.log(sourcePath + " is Valid");
  }).catch(function(msg) {
    console.error("Specified Source is Invalid: ", msg);
  });

  files.directoryExists(destPath).then(function(msg) {
    console.log(destPath + " is Valid");
    // Create Output Folder
    if (!(fs.existsSync(destPath + "output"))) {
      fs.mkdirSync(destPath + "output");
      destPath += "output/";
      console.log('Created Output Folder | New Path: ' + destPath);
    }

    console.log('\nGenerating...\n');
    
    // Read YML file
    var doc = stdfunc.serializeYML(sourcePath);

    stdfunc.createTable(doc, destPath);
    stdfunc.createJava(doc, destPath);
    stdfunc.createXML(doc, destPath);

  }).catch(function() {
    console.error("Specified Destination is Invalid");
  });
});
