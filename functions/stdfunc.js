const fs = require('fs');
const _ = require('underscore');
const path = require('path');
const mkdirp = require('mkdirp');
const util = require('./util.js');
const files = require('./files.js');
const generateInterface = require('./createJavaInterface.js').create;
const generateServlet = require('./createJavaServlet').create;
const generateXML = require('./createXML.js').create;
const generateSQL = require('./createSQL.js').create;
const generateAE = require('./createAppEngineSpec.js').create;
const generateJAR = require('./createJarApplication.js').create;
const generatePOM = require('./createPOM.js').create;
const generateAppProp = require('./createAppProp.js').create;
const generateLogProp = require('./createLogProp.js').create;
const generateTestCase = require('./createTestCase.js').create;
const generateReadMe = require('./createReadMe.js').create;
const generateHTML = require('./createHTML.js').create;
const serialize = require('./serialize.js');

exports.execute = function(source, dest) {
  var serviceName;
  var doc;

  // Eliminate Whitespace
  source = source.replace(/ /g, '');
  dest = dest.replace(/ /g, '');

  // In add / to end of path if not already there
  if (dest.substr(-1, 1) != "/") {
    dest += '/';
  }

  files.fileExists(source).then(function(msg) {
    if (path.extname(source) != '.yml' && path.extname(source) != '.yaml') {
      throw new Error("Source is not a YML file");
    }
    console.log(source + " is Valid");
    doc = serialize.YML(source);
    serviceName = ('heb-liquidsky-service-' + util.getServiceName(doc));

    files.directoryExists(dest).then(function(msg) {
      console.log(dest + " is Valid");
      // Create Output Folder
      if (!(fs.existsSync(dest + serviceName))) {
        fs.mkdirSync(dest + serviceName);
      }
      dest += (serviceName + "/");
      console.log('Created Output Folder | New Path: ' + dest);

      console.log('\nGenerating File Hierarchy...\n');

      generateSQL(doc, dest);
      generatePOM(doc, dest);
      generateReadMe(doc, dest);
      generateHTML(doc, dest);
      fs.writeFileSync(dest + 'openapi.yaml', fs.readFileSync(source));

      // BEGIN CREATING FILE STRUCTURE
      mkdirp(dest + 'src/main/appengine/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/appengine/ created");

        // Generation Functions Below
        generateAE(doc, dest + 'src/main/appengine/');
      });

      mkdirp(dest + 'src/main/java/com/heb/liquidsky/data/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/data/ created");

        // Generation Functions Below
        generateXML(doc, dest + 'src/main/java/com/heb/liquidsky/data/');
      });

      mkdirp(dest + 'src/main/java/com/heb/liquidsky/endpoints/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/endpoints/ created");

        // Generation Functions Below
        generateInterface(doc, (dest + 'src/main/java/com/heb/liquidsky/endpoints/'));
      });

      mkdirp(dest + 'src/main/java/com/heb/liquidsky/spring/web/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/spring/web/ created");

        // Generation Functions Below
        generateServlet(doc, (dest + 'src/main/java/com/heb/liquidsky/spring/web/'));
        generateJAR(doc, (dest + 'src/main/java/com/heb/liquidsky/spring/'));
      });

      mkdirp(dest + 'src/main/resources/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/resources/ created");

        // Generation Functions Below

        generateAppProp(dest + 'src/main/resources/');
      });

      mkdirp(dest + 'src/test/java/com/heb/liquidsky/data/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/test/java/com/heb/liquidsky/data/ created");

        // Generation Functions Below
        generateTestCase(doc, dest + 'src/test/java/com/heb/liquidsky/data/');
      });

      mkdirp(dest + 'src/test/resources/', function(err) {
        if (err) {
          throw err;
        }
        // Confirm directories were created
        console.log("/src/test/resources/ created");

        // Generation Functions Below
        generateLogProp(dest + 'src/test/resources/');
      });

      // END CREATE FILE STRUCTURE

    }).catch(function(e) {
      console.error(e);
    });
  }).catch(function(msg) {
    console.error(msg);
  });
}

exports.executeWithInquirer = function(){
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
      doc = serialize.YML(sourcePath);
      serviceName = ('heb-liquidsky-service-' + util.getServiceName(doc));
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

      generateSQL(doc, destPath);
      generatePOM(doc, destPath);
      generateHTML(doc, destPath);
      fs.writeFileSync(destPath + 'openapi.yaml', fs.readFileSync(sourcePath));

      // BEGIN CREATING FILE STRUCTURE
      mkdirp(destPath + 'src/main/appengine/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/appengine/ created");

        // Generation Functions Below
        generateAE(doc, destPath + 'src/main/appengine/');
      });

      mkdirp(destPath + 'src/main/java/com/heb/liquidsky/data/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/data/ created");

        // Generation Functions Below
        generateXML(doc, destPath + 'src/main/java/com/heb/liquidsky/data/');
      });

      mkdirp(destPath + 'src/main/java/com/heb/liquidsky/endpoints/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/endpoints/ created");

        // Generation Functions Below
        generateInterface(doc, (destPath + 'src/main/java/com/heb/liquidsky/endpoints/'));
      });

      mkdirp(destPath + 'src/main/java/com/heb/liquidsky/spring/web/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/java/com/heb/liquidsky/spring/web/ created");

        // Generation Functions Below
        generateServlet(doc, (destPath + 'src/main/java/com/heb/liquidsky/spring/web/'));
        generateJAR(doc, (destPath + 'src/main/java/com/heb/liquidsky/spring/'));
      });

      mkdirp(destPath + 'src/main/resources/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/main/resources/ created");

        // Generation Functions Below
        generateAppProp(destPath + 'src/main/resources/');
      });

      mkdirp(destPath + 'src/test/java/com/heb/liquidsky/data/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/test/java/com/heb/liquidsky/data/ created");

        // Generation Functions Below
        generateTestCase(doc, destPath + 'src/test/java/com/heb/liquidsky/data/');
      });

      mkdirp(destPath + 'src/test/resources/', function(err){
        if (err){
          throw err;
        }
        // Confirm directories were created
        console.log("/src/test/resources/ created");

        // Generation Functions Below
        generateLogProp(destPath + 'src/test/resources/');
      });

      // END CREATE FILE STRUCTURE

    }).catch(function(e) {
      console.error("Specified Destination is Invalid: ", e);
    });
  });
}
