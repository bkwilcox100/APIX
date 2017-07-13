var fs = require('fs');
var path = require('path');
var inquirer = require('inquirer');

var getCurrentDirectory = exports.getCurrentDirectory = function() {
  return path.basename(process.cwd());
}

var directoryExists = exports.directoryExists = function(directoryPath) {
  return new Promise(function(resolve, reject) {
    //directoryPath = directoryPath.trim();
    fs.stat(directoryPath, function(err, data) {
      if (err) {
        // Reject on error
        reject(err);
      } else {
        // Return result on success
        resolve(data.isDirectory());
      }
    });
  });
}

var fileExists = exports.fileExists = function(filePath) {
  //filePath = filePath.trim();
  return new Promise(function(resolve, reject) {
    fs.access(filePath, function(err) {
      if (err) {
        return reject(err);
      }
      resolve("File exists.");
    });
  });
}

exports.getPaths = function(callback) {
  var askPaths = [{
      name: "sourcePath",
      type: "input",
      message: "Please enter the location of the OA Spec (.yml/.yaml): ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return "Please enter a path";
        }
      }
    },
    {
      name: "destPath",
      type: "input",
      message: "Please enter a location for output: ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return "Please enter a path";
        }
      }
    }
  ];
  inquirer.prompt(askPaths).then(callback);
}

exports.getInitialSpec = function(callback) {
  var options = [{
      name: "title",
      type: "input",
      message: "Enter a title: ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return false;
        }
      }
    },
    {
      name: "description",
      type: "input",
      message: "Enter a description: ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return false;
        }
      }
    },
    {
      name: "version",
      type: "input",
      message: "Enter a version number: ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return false;
        }
      },
      default: "1.0.0"
    },
    {
      name: "serviceName",
      type: "input",
      message: "Enter a service name: ",
      validate: function(val) {
        if (val.length) {
          return true;
        } else {
          return false;
        }
      }
    }
  ];

  inquirer.prompt(options).then(callback);
}
