/*
Name: APIX or API Expander
Author: Brandon Wilcox
Date Created: 6/8/17
Date Modified: 6/10/17
*/

/*
NOTES:
The ID of every definitions MUST end in "Id" or "ID"
Any date objects must end in "date" or "Date"
*/

// Modules
var http = require('http');
var fs = require('fs');
var yam = require('js-yaml');

// User Defined Functions
var stdfunc = require("./stdfunc.js");

// Constants
var CURRENT_TIME = Date();
// Attempt to Serialize YAML file
try {
  // Load YAML from file and convert to java object
  var doc = yam.safeLoad(fs.readFileSync('./openapiexample.yml', 'utf8'));

  // Print object to screen for confirmation
  console.log(doc);
  fs.writeFile('./currentJSON.txt', doc, function(error) {
    if (error) {
      return console.error();
    }
    console.log("Object to File Write Successful");
  });

  // Create or overwrite test file
  var output = doc.info.title + "\n\nTimestamp: " + CURRENT_TIME;

  // Test Object
  console.log("\nTest Output:\n");

  for (key in doc["definitions"]){
    console.log(key + ':');
    console.log("type: " + doc["definitions"][key]["type"]);
    for (prop in doc["definitions"][key]["properties"]){
      console.log("\t" + prop + ":");
      for (element in doc["definitions"][key]["properties"][prop]){
        //console.log("SCOTT   -> " + JSON.stringify(doc["definitions"][key]["properties"]));
        console.log("\t\t" + element + ":");
        console.log("\t\t" + doc["definitions"][key]["properties"][prop][element] + "\n");

      }
    }
    console.log('\n');
  }


  var wholeCreateTable = "";
  var primaryKey = "primaryKey not set";
  var isString = true;
  // Check for required field
  var requiredList = [];
  for (key in doc["definitions"]){
    for(keyItem in doc["definitions"][key]["required"]){
      console.log(doc["definitions"][key]["required"][keyItem]);
      requiredList.push(doc["definitions"][key]["required"][keyItem]);
      console.log(requiredList);
    }
  }

  // Create Table for each definition
  for (key in doc["definitions"]){
    var tableString = "CREATE TABLE IF NOT EXISTS heb_" + key + " (\n";

    for (prop in doc["definitions"][key]["properties"]){
      isArray = false;
      for (element in doc["definitions"][key]["properties"][prop]){
        var propertyName = JSON.stringify(prop);
        var flag = doc["definitions"][key]["properties"][prop][element];
        if (flag == "string"){
          // Check if ID
          if (propertyName.slice(-3, -1) == "Id" || propertyName.slice(-3, -1) == "ID"){
            tableString += ("\t" + prop + " varchar(64)");
          } else if (propertyName.slice(-5, -1) == "date" || propertyName.slice(-5, -1) == "Date"){
            tableString += ("\t" + prop + " datetime default current_timestamp");
          } else {
            tableString += ("\t" + prop + " varchar(1024)");
          }
        } else {
          //tableString += ("\t" + prop + " other");
          isString = false;
        }
        var isRequired = false;
        for (x in requiredList){
          if (requiredList[x] == prop){
            tableString += " not null,";
            isRequired = true;
            console.log("Required encountered");
            break; // Redesign before final
          }
        }
        if (!isRequired && isString){
          tableString += ",\n";
        } else if (isString){
          tableString += "\n";
        } else {
          isString = true;
        }

      }
    }
    tableString += ("\tprimary key (" + primaryKey + ")\n");
    tableString += ");\n\n";
    wholeCreateTable += tableString;
  }

  fs.writeFile('./ymltest.sql',wholeCreateTable, function(error) {
    if (error) {
      return console.error();
    }
    console.log("YML Info Write Successful");
  });


  // TESTING PURPOSES: Create Local Server and listen on port 8080
  http.createServer(function(req, res) {
    res.writeHead(200, {
      'Content-Type': 'text/html'
    });
    res.write("API Name: " + doc.info.title + "\n");
    res.write("Description: " + doc.info.description + "\n");
    res.end("END RESPONSE")
  }).listen(8080);
} catch (error) {
  console.log(error);
}
