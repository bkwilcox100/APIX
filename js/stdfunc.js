var fs = require('fs');
var yam = require('js-yaml');

/*
Name: createTable
Description: Creates SQL table and outputs to file
Parameters:
- doc : JSON object used for table creation
- destPath : Destination for output SQL file
Preconditions:
- YML file has already been serialized
Return: None
*/

exports.createTable = function(doc, destPath){
  var wholeCreateTable = "use middle_layer;\n\n";
  var primaryKey = null;

  // Check for required field
  var requiredList = [];
  for (key in doc["definitions"]){
    for(keyItem in doc["definitions"][key]["required"]){
      requiredList.push(doc["definitions"][key]["required"][keyItem]);
    }
  }

  // Create Table for each definition
  for (key in doc["definitions"]){
    var tableString = "CREATE TABLE IF NOT EXISTS heb_" + key + " (\n";
    primaryKey = null;
    for (prop in doc["definitions"][key]["properties"]){
      isArray = false;
      for (element in doc["definitions"][key]["properties"][prop]){
        var propertyName = JSON.stringify(prop);
        var typeFormat = doc["definitions"][key]["properties"][prop]["format"];
        var flag = doc["definitions"][key]["properties"][prop]["type"];

        // Ensure the loop only adds to the document once per property
        if (element == "type"){
          // If the property is type string
          if (flag == "string"){
            // Check if ID
            if (propertyName.slice(-3, -1).toUpperCase() == "ID" ){
              tableString += ("\t" + prop + " varchar(64)");
              // If first occurence of ID data, set as primary key
              if (primaryKey == null){
                primaryKey = prop;
              }
            }

            else if (propertyName.slice(-5, -1).toUpperCase() == "DATE"){
              tableString += ("\t" + prop + " datetime default current_timestamp");
              if (propertyName.slice(1, 5).toUpperCase() == "LAST"){
                tableString += " on update current_timestamp";
              }
            }

            else {
              tableString += ("\t" + prop + " varchar(1024)");
            }
          }

          // If property is type integer
          else if (flag == "integer") {
            console.log("INTEGER DETECTED: " + typeFormat);
            var hasMin = doc["definitions"][key]["properties"][prop].hasOwnProperty('minimum');
            var hasMax = doc["definitions"][key]["properties"][prop].hasOwnProperty('maximum');
            if (hasMax && hasMin){
              var max = doc["definitions"][key]["properties"][prop]["maximum"];
              var min = doc["definitions"][key]["properties"][prop]["minimum"];
              if (max < min){
                throw "Bad Index: Min larger than max";
              }
              else {
                tableString += ("\t" + prop + " varchar(" + max + ")");
              }
            }

            else if (hasMax) {
              var max = doc["definitions"][key]["properties"][prop]["maximum"];
              tableString += ("\t" + prop + " varchar(" + max + ")");
            }

            else if (hasMin) {
              var min = doc["definitions"][key]["properties"][prop]["minimum"];
              if (min < 32){
                tableString += ("\t" + prop + " varchar(" + 32 + ")");
              } else {
                tableString += ("\t" + prop + " varchar(" + min + ")");
              }
            }
            else {
              tableString += ("\t" + prop + " varchar(64)");
            }
          }

          else if (flag == "number") {
            console.log("NUMBER DETECTED");
            if (typeFormat == "float"){
              tableString += ("\t" + prop + " varchar(32)");
            } else {
              tableString += ("\t" + prop + " varchar(64)");
            }
          }

          else if (flag == "boolean") {
            tableString += ("\t" + prop + " varchar(1)");
          }

          else {
            //tableString += ("\t" + prop + " other\n");
            console.log("DATA FALLEN: " + prop);
          }

          var isRequired = false;

          for (x in requiredList){
            if (requiredList[x] == prop){
              tableString += " not null,";
              isRequired = true;
              break; // Redesign before final
            }
          }

          console.log("Prop: " + prop);
          console.log("Flag: " + flag);
          console.log(requiredList);
          console.log();

          if (!isRequired && flag == "string"){
            tableString += ",\n";
          } else if (isRequired){
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
      return console.error();
    }
    console.log("Table Created and Exported Successfully");
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
Return: JSON object
*/
exports.serializeYML = function(sourcePath, destPath){
  // Constants
  var CURRENT_TIME = Date();
  // Attempt to Serialize YAML file
  try {
    // Load YAML from file and convert to java object
    var doc = yam.safeLoad(fs.readFileSync(sourcePath, 'utf8'));

    // Print object to screen for confirmation
    console.log(doc);
    fs.writeFile(destPath, JSON.stringify(doc), function(error) {
      if (error) {
        return console.error();
      }
      console.log("Object to File Write Successful");

    });
  } catch (error) {
    console.log(error);
  }
  return doc;
}
