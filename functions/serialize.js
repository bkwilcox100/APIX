const fs = require('fs');
const yam = require('js-yaml');

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
exports.YML = function(sourcePath) {
  // Attempt to Serialize YAML file
  try {
    // Load YAML from file and convert to java object
    var doc = yam.safeLoad(fs.readFileSync(sourcePath, 'utf8'));
    console.log("YML Read and Conversion Successful");
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
exports.XML = function(sourcePath, destPath) {
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
