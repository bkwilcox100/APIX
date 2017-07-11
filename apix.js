#! usr/bin/env node
// TODO: Create output directory if it doesn't exist
'use strict';

// Modules
var fs = require('fs');             // For interacting with file system
var path = require('path');         // For interacting with file system
var _ = require('underscore');      // Utility Functions Library
var clear = require('clear');       // Clears screen before start of program
var chalk = require('chalk');       // For colorful UI
var mkdirp = require('mkdirp');     // For creating multiple directories
var program = require('commander');

// Commander Argument Settings
program
    .version('1.0.0')
    .option('-g --graphic', 'Run with Graphical Interface');

program
    .option('-s, --source <src>', 'Run with specified source (Defaults to current directory)')
    .option('-d, --dest <dest>', 'Run with destination (Requires source)');

program.parse(process.argv);

// User Defined Functions
var stdfunc = require("./functions/stdfunc.js");
var generateJava = require("./functions/createJava.js").create;
var util = require("./functions/util.js");
var files = require("./functions/files.js");

// Global Variables
var sourcePath = "";
var destPath = "";
var serviceName = "";
var doc = null;

if (program.graphic){
  // Clear Terminal Window
  clear();
  // Display Welcome Message
  console.log(chalk.green("Welcome to APIX!"));

  // Execute
  stdfunc.executeWithInquirer();

} else if (program.source && program.dest){
  stdfunc.execute(program.source, program.dest);
} else if (program.source){
  stdfunc.execute(program.source, __dirname);
} else {
  console.log("No Options Selected");
}
