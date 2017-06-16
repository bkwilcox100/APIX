var fs = require('fs');
var yam = require('js-yaml');
var xml2js = require('xml2js');

/*
Name: createTable
Description: Creates SQL table from OA Spec
Parameters:
- doc : JSON object used for table creation
- destPath : Destination for output SQL file
Preconditions:
- YML file has already been serialized
Postconditions:
- destPath exists
Return: None
Status: COMPLETE
*/
exports.createTable = function(doc, destPath) {
  var wholeCreateTable = "use middle_layer;\n\n";
  var primaryKey = null;

  // Check for required field
  var requiredList = [];
  for (key in doc["definitions"]) {
    for (keyItem in doc["definitions"][key]["required"]) {
      requiredList.push(doc["definitions"][key]["required"][keyItem]);
    }
  }

  // Create Table for each definition
  for (key in doc["definitions"]) {
    var tableString = "CREATE TABLE IF NOT EXISTS heb_" + toUnderscore(key) + " (\n";
    primaryKey = null;
    for (prop in doc["definitions"][key]["properties"]) {
      isArray = false;
      for (element in doc["definitions"][key]["properties"][prop]) {
        var propertyName = JSON.stringify(prop);
        var typeFormat = doc["definitions"][key]["properties"][prop]["format"];
        var flag = doc["definitions"][key]["properties"][prop]["type"];

        // Ensure the loop only adds to the document once per property
        if (element == "type") {
          // If the property is type string
          if (flag == "string") {
            // Check if ID
            if (propertyName.slice(-3, -1).toUpperCase() == "ID") {
              tableString += ("\t" + prop + " varchar(64)");
              // If first occurence of ID data, set as primary key
              if (primaryKey == null) {
                primaryKey = prop;
              }
            } else if (propertyName.slice(-5, -1).toUpperCase() == "DATE") {
              tableString += ("\t" + prop + " datetime default current_timestamp");
              if (propertyName.slice(1, 5).toUpperCase() == "LAST") {
                tableString += " on update current_timestamp";
              }
            } else {
              tableString += ("\t" + prop + " varchar(1024)");
            }
          }

          // If property is type integer
          else if (flag == "integer") {
            var hasMin = doc["definitions"][key]["properties"][prop].hasOwnProperty('minimum');
            var hasMax = doc["definitions"][key]["properties"][prop].hasOwnProperty('maximum');
            if (hasMax && hasMin) {
              var max = doc["definitions"][key]["properties"][prop]["maximum"];
              var min = doc["definitions"][key]["properties"][prop]["minimum"];
              if (max < min) {
                throw "Bad Index: Min larger than max";
              } else {
                tableString += ("\t" + prop + " varchar(" + max + ")");
              }
            } else if (hasMax) {
              var max = doc["definitions"][key]["properties"][prop]["maximum"];
              tableString += ("\t" + prop + " varchar(" + max + ")");
            } else if (hasMin) {
              var min = doc["definitions"][key]["properties"][prop]["minimum"];
              if (min < 32) {
                tableString += ("\t" + prop + " varchar(" + 32 + ")");
              } else {
                tableString += ("\t" + prop + " varchar(" + min + ")");
              }
            } else {
              tableString += ("\t" + prop + " varchar(64)");
            }
          } else if (flag == "number") {
            if (typeFormat == "float") {
              tableString += ("\t" + prop + " varchar(32)");
            } else {
              tableString += ("\t" + prop + " varchar(64)");
            }
          } else if (flag == "boolean") {
            tableString += ("\t" + prop + " varchar(1)");
          } else {
            //tableString += ("\t" + prop + " other\n");
          }

          var isRequired = false;

          for (x in requiredList) {
            if (requiredList[x] == prop) {
              tableString += " not null,";
              isRequired = true;
              break; // Redesign before final
            }
          }

          if (!isRequired && flag == "string") {
            tableString += ",\n";
          } else if (isRequired) {
            tableString += "\n";
          } else if (flag != "array") {
            tableString += ",\n";
          } else {
            // Do nothing
          }
        }

      }
    }
    tableString += ("\tprimary key (" + primaryKey + ")\n");
    tableString += ");\n\n";
    wholeCreateTable += tableString;
  }

  fs.writeFile(destPath, wholeCreateTable, function(error) {
    if (error) {
      throw error;
    }
    console.log("SQL Write Successful");
  });
}

/*
Name: serializeYML
Description: Converts YML to JSON object
Parameters:
- sourcePath : Source YML doc to be converted
- destPath : Document to store JSON data
Preconditions:
- YML document exists at specfied path
Postconditions:
- destPath exists
Return: JSON object
Status: COMPLETE
*/
exports.serializeYML = function(sourcePath, destPath) {
  // Constants
  var CURRENT_TIME = Date();
  // Attempt to Serialize YAML file
  try {
    // Load YAML from file and convert to java object
    var doc = yam.safeLoad(fs.readFileSync(sourcePath, 'utf8'));

    // Print object to screen for confirmation
    //console.log(doc);
    fs.writeFile(destPath, JSON.stringify(doc), function(error) {
      if (error) {
        throw error;
      }
      console.log("YML Read and Conversion Successful");

    });
  } catch (loadError) {
    throw loadError;
  }
  return doc;
}

/*
Name: serializeXML
Description: Converts XML to JSON object
Parameters:
- sourcePath : Source XML doc to be converted
Preconditions:
- XML document exists at specfied path
Postconditions:
- destPath exists
Return: JSON object
Status: COMPLETE
*/
exports.serializeXML = function(sourcePath, destPath) {
  fs.readFile(sourcePath, 'utf8', function(err, data) {
    if (err) {
      throw err;
    }
    xml2js.parseString(data, function(err2, result) {
      if (err2) {
        throw err2;
      }
      console.log("XML Conversion Successful");
      var stringResult = JSON.stringify(result);
      console.log(result);
      fs.writeFile(destPath, stringResult, function(err3) {
        if (err3) {
          throw err3;
        }
        console.log("XML Read Successful");
      });
      return result;
    });
  });
}

/*
Name: createXML
Description: Creates an XML document from OA Spec
Parameters:
- sourceDoc : Source YML doc to be converted to XML
Preconditions:
- YML document exists at specfied path
Postconditions:
- destPath exists
Return: None
Status: INCOMPLETE
*/
exports.createXML = function(sourceDoc, destPath) {
  var wholeDoc = "";
  fs.readFile('./docs/xml/xml_ls_template.xml', 'utf8', function(err, data) {
    if (err) {
      throw err;
    }
    wholeDoc = data;

    // Iterates through the definitions object
    for (def in sourceDoc["definitions"]) {
      // Per Definition Variables
      var hasID = false;
      var idString = "";
      var hasIdGenerator = true;
      var hasPubSub = false;
      var hasFCM = false;
      var arrayList = [];
      var arrayPropList = [];
      var hasArray = false;

      var commentStr = "<!--\n==========================================================\n"
      commentStr += "Definitions of resources for " + def + "\n"
      commentStr += "==========================================================\n-->\n";
      wholeDoc += commentStr;
      wholeDoc += "<data-type "
      wholeDoc += ("name=\"" + toCamelCase(def) + "\" ");
      // Iterates through properties to check for an ID
      for (id in sourceDoc["definitions"][def]["properties"]) {
        var isPrimarySet = false;
        if (id.slice(-2, id.length).toLowerCase() == "id") {
          hasID = true;
          idString = id;
          break;
        }
      }
      // If an ID exists, add a property
      if (hasID) {
        wholeDoc += ("id-property=\"" + idString + "\" ");
      }
      wholeDoc += ("id-generator-prefix=\"\" ");
      wholeDoc += ("use-id-generator=\"" + hasIdGenerator + "\" ");
      wholeDoc += ("pub-sub-enabled=\"" + hasPubSub + "\" ");
      wholeDoc += ("fcm-enabled=\"" + hasFCM + "\"");

      wholeDoc += ">\n";

      wholeDoc += ("\t<table name=\"heb_" + toUnderscore(def) + "\" ");

      // Set to primary if first, else set to reference
      if (!isPrimarySet) {
        wholeDoc += "type=\"primary\" ";
        isPrimarySet = true;
      } else {
        wholeDoc += "type=\"reference\" ";
      }
      wholeDoc += ("id-column=\"" + idString + "\">\n");

      // Iterates through properties
      for (prop in sourceDoc["definitions"][def]["properties"]) {
        var isRequired = false;
        var isRestricted = true;
        var maxLength = 64;
        var minLength = 0;

        // Check to see if definition is empty
        if (sourceDoc["definitions"][def].hasOwnProperty('properties')) {
          // Check required list for property
          for (rItem in sourceDoc["definitions"][def]["required"]) {
            if (prop == sourceDoc["definitions"][def]["required"][rItem]) {
              isRequired = true;
            }
          }
          // Add to array list if property is array
          if (sourceDoc["definitions"][def]["properties"][prop]["type"] == "array"){
            arrayList.push(def);
            arrayPropList.push(prop);
            hasArray = true;
          }
          // Check for Min and Max Lengths
          if (sourceDoc["definitions"][def]["properties"][prop].hasOwnProperty('maximum')) {
            maxLength = sourceDoc["definitions"][def]["properties"][prop]["maximum"];
          } else if (sourceDoc["definitions"][def]["properties"][prop].hasOwnProperty('minimum')) {
            minLength = sourceDoc["definitions"][def]["properties"][prop]["minimum"];
          } else {
            if (sourceDoc["definitions"][def]["properties"][prop].hasOwnProperty('type')) {
              switch (sourceDoc["definitions"][def]["properties"][prop]["type"]) {
                case "string":
                  maxLength = 1024;
                  break;

                case "integer":
                  maxLength = 64;
                  break;

                case "number":
                  maxLength = 64;
                  break;

                case "float":
                  maxLength = 64;
                  break;

                case "boolean":
                  maxLength = 1;
                  break;

                default:
                  maxLength = 64;

              }
            } else {
              maxLength = 1024;
            }
          }

          wholeDoc += ("\t\t<column column-name=\"" + prop.toLowerCase() + "\" property=\"" + prop + "\">\n");

          // Go back and add conditions for attributes
          wholeDoc += ("\t\t\t<attribute name=\"requiredProperty\" value=\"" + isRequired + "\"/>\n");
          wholeDoc += ("\t\t\t<attribute name=\"restrictedProperty\" value=\"false\"/>\n");
          wholeDoc += ("\t\t\t<attribute name=\"maxLength\" value=\"" + maxLength + "\"/>\n");
          wholeDoc += ("\t\t\t<attribute name=\"minLength\" value=\"" + minLength + "\"/>\n");
        }
        wholeDoc += "\t\t</column>\n";
      }
      wholeDoc += "\t</table>\n";
      console.log(arrayList);
      console.log(hasArray);

      wholeDoc += "</data-type>\n\n";
    }
    wholeDoc += "</data-store>";
    //console.log(wholeDoc);
    fs.writeFile(destPath, wholeDoc, function(err) {
      if (err) {
        throw err;
      }
      console.log("XML Write and Conversion Successful");
    });
  });

}

// Utility Functions

var toUnderscore = exports.toUnderscore = function(string) {
  var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
    return "_" + y.toLowerCase()
  }).replace(/^_/, "");
  return newString;
}

var toCamelCase = exports.toCamelCase = function capitalizeFirstLetter(string) {
  return string.charAt(0).toLowerCase() + string.slice(1);
}

var getArrTableName = exports.getArrTableName = function(str){
  var lastIndex = str.lastIndexOf('/');
  lastIndex++;
  var newStr = str.slice(lastIndex, str.length);
  return newStr;
}
