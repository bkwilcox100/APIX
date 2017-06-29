// TODO: Make Function ignore some of the definitions

var fs = require('fs');
var yam = require('js-yaml');
var xml2js = require('xml2js');
var _ = require('underscore');
var path = require('path');

/*
Name: getServiceName
Description: Gets service name from OA Spec
Parameters:
- doc : JSON object of OA Spec
Preconditions:
- doc exists
Postconditions:
- None
Return: String
Status: COMPLETE
*/
exports.getServiceName = function(doc) {
    if (doc.hasOwnProperty('tags')){
        for (i = 0; i < doc['tags'].length; i++) {
            for (tag in doc['tags'][i]) {
                if (doc['tags'][i]['name']) {
                    return doc['tags'][i]['description'];
                }
                console.log(doc['tags'][i][tag]);
            }
        }
    }
    return 'NoN';
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
exports.serializeYML = function(sourcePath) {
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
TODO: Clean Up Logic/Design
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
    var fileName = destPath + "tables.sql";
    fs.writeFile(fileName, wholeCreateTable, function(error) {
        if (error) {
            throw error;
        }
        console.log("SQL Write Successful");
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
Status: COMPLETE
TODO: Clean Up Logic/Design
*/
exports.createXML = function(sourceDoc, destPath) {
    var wholeDoc = "";
    var absPath = path.join(__dirname, '..', 'docs', 'xml', 'xml_ls_template.xml');
    fs.readFile(absPath, 'utf8', function(err, data) {
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
            var isTLC = false;

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

            for (tDef in sourceDoc["definitions"]) {
                if (def == tDef) {
                    isTLC = true;
                    break;
                }
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
            wholeDoc += ("id-column=\"" + toUnderscore(idString) + "\">\n");

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
                    if (sourceDoc["definitions"][def]["properties"][prop]["type"] == "array") {
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

                    wholeDoc += ("\t\t<column column-name=\"" + toUnderscore(prop) + "\" property=\"" + prop + "\">\n");

                    // Go back and add conditions for attributes
                    wholeDoc += ("\t\t\t<attribute name=\"requiredProperty\" value=\"" + isRequired + "\"/>\n");
                    wholeDoc += ("\t\t\t<attribute name=\"restrictedProperty\" value=\"false\"/>\n");
                    wholeDoc += ("\t\t\t<attribute name=\"maxLength\" value=\"" + maxLength + "\"/>\n");
                    wholeDoc += ("\t\t\t<attribute name=\"minLength\" value=\"" + minLength + "\"/>\n");
                }
                wholeDoc += "\t\t</column>\n";
            }
            wholeDoc += "\t</table>\n\n";
            //console.log(arrayPropList);
            //console.log(hasArray);
            if (hasArray) {
                for (i in arrayList) {
                    for (j in arrayPropList) {
                        var tableName = sourceDoc["definitions"][arrayList[i]]["properties"][arrayPropList[j]]["items"]["$ref"];
                        tableName = getArrTableName(tableName);
                        var modTableName = toUnderscore(tableName);
                        wholeDoc += ("\t<table name=\"heb_" + modTableName + "\" type=\"reference\" id-column=\"" + idString + "\">\n");
                        var columnName = "";
                        for (x in sourceDoc["definitions"][tableName]["properties"]) {
                            if (x.slice(-2, x.length).toLowerCase() == "id") {
                                columnName = x;
                                break;
                            }
                        }
                        wholeDoc += ("\t\t<column column-name=\"" + toUnderscore(columnName) + "\" list-item-type=\"" + toCamelCase(tableName) + "\" ");
                        wholeDoc += ("property=\"" + tableName + "s\" read-only=\"true\" cascade=\"true\" />\n");
                        wholeDoc += "\t</table>\n\n";
                    }
                }
            }
            if (isTLC) {
                wholeDoc += ("\t<named-query name=\"getAll" + def + "\">\n");
                wholeDoc += ("\t\tselect " + toUnderscore(idString) + " from heb_" + toUnderscore(def) + "\n");
                wholeDoc += ("\t</named-query>\n");
            }
            wholeDoc += "</data-type>\n\n";
        }
        wholeDoc += "</data-store>";
        //console.log(wholeDoc);
        var fileName = destPath + "data-store.xml";
        fs.writeFile(fileName, wholeDoc, function(err) {
            if (err) {
                throw err;
            }
            console.log("XML Write and Conversion Successful");
        });
    });

}

/*
Name: createJava
Description: Creates Java Class from OA Spec
Parameters:
- source : Source XML file to be converted
Preconditions:
- XML file exists
Postconditions:
- destPath exists
Return: None
Status: COMPLETE
TODO: Clean Up Logic/Design
*/
exports.createJava = function(source, destPath) {
    var groupList = [];
    var TLC = getTLC(source);
    var idString = "";

    for (def in source['definitions']) {
        if (source['definitions'][def].hasOwnProperty('title')) {
            if (!(_.contains(groupList, source['definitions'][def]['title'][0]['group']))) {
                groupList.push(source['definitions'][def]['title'][0]['group']);
            }
        }
    }

    for (group in groupList) {
        wholeDoc = "// Add any necessary packages.\n\n\n";
        wholeDoc += ("public class " + groupList[group] + "Interface {\n\n");
        wholeDoc += "\tprivate static final String CONTEXT_FILTER = \"AdminPortal\";\n";
        wholeDoc += "\tprivate static final String DEFAULT_PARENT_PROPERTY = \"parent\";\n\n";
        for (def in source['definitions']) {
            if (source['definitions'][def]['group'] == groupList[group]) {
                wholeDoc += ("\tprivate static final String DATA_ITEM_NAME_" + toUnderscoreUpper(def) + " = \"" + toCamelCase(def) + "\";\n");
                for (prop in source['definitions'][def]['properties']) {
                    wholeDoc += ("\tprivate static final String APP_PROPERTIES_" + toUnderscoreUpper(prop) + " = \"" + toCamelCase(prop) + "\";\n")
                    if (_.contains(TLC, def)) {
                        if (prop.slice(-2, prop.length).toLowerCase() == "id") {
                            idString = prop;
                        }
                    }
                }
                if (_.contains(TLC, def)) {
                    wholeDoc += ("\tprivate static final String APP_PROPERTIES_COLLECTION_QUERY = \"all_" + toUnderscore(def) + "\";\n");
                }
                wholeDoc += "\n";
            }
        }

        wholeDoc += "\n";
        wholeDoc += "\tprivate static final String OPERATION_CREATE = \"create\";\n";
        wholeDoc += "\tprivate static final String OPERATION_UPDATE = \"update\";\n";
        wholeDoc += "\tprivate static final String OPERATION_DELETE = \"delete\";\n";
        wholeDoc += "\t//private static final String OPERATION_READ = \"read\";\n";

        wholeDoc += "\n\t// Create Operations\n\n";
        for (def in source['definitions']) {
            if (source['definitions'][def]['title'][0]['group'] == groupList[group]) {
                if (_.contains(TLC, def)) {
                    wholeDoc += ("\tpublic Map<String, Object> createBatch" + def + "(JsonElement requestBody) throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + toUnderscoreUpper(groupList[group]) + ", null, null, requestBody, APP_PROPERTIES_" + toUnderscoreUpper(idString) + ");\n");
                    wholeDoc += "\t\treturn response;\n\t}\n\n";
                } else {
                    wholeDoc += ("\tpublic Map<String, Object> createBatch" + def + "(JsonElement requestBody, String " + idString + ") throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_" + toUnderscoreUpper(groupList[group]) + ", " + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(groupList[0]) + ", requestBody, null);\n");
                    wholeDoc += "\t\treturn response;\n\t}\n\n";
                }
            }
        }

        wholeDoc += "\n\t// Read Operations\n\n";
        for (def in source['definitions']) {
            if (source['definitions'][def]['title'][0]['group'] == groupList[group]) {
                if (_.contains(TLC, def)) {
                    wholeDoc += ("\tpublic List<Map<String, Object>> read" + def + "Collection() throws ServiceException {\n");
                    wholeDoc += ("\t\treturn ResourceUtils.readCollectionFromQuery(APP_PROPERTIES_COLLECTION_QUERY, DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", CONTEXT_FILTER);\n\t}\n\n");
                    wholeDoc += ("\tpublic Map<String, Object> read" + def + "Resource(String " + idString + ") throws ServiceException {\n");
                    wholeDoc += ("\t\treturn ResourceUtils.readResource(" + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", CONTEXT_FILTER);\n\t}\n\n");
                } else {
                    var altID = "NO ID FOUND";
                    for (prop in source['definitions'][def]['properties']) {
                        if (prop.slice(-2, prop.length).toLowerCase() == 'id') {
                            altID = prop;
                        }
                    }
                    wholeDoc += ("\tpublic List<Map<String, Object>> read" + def + "Collection() throws ServiceException {\n");
                    wholeDoc += ("\t\treturn ResourceUtils.readSubCollection(" + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", CONTEXT_FILTER);\n\t}\n\n");
                    wholeDoc += ("\tpublic Map<String, Object> read" + def + "Resource(String " + idString + ", String " + altID + ") throws ServiceException {\n");
                    wholeDoc += ("\t\treturn ResourceUtils.readResource(" + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", CONTEXT_FILTER);\n\t}\n\n");
                }
            }
        }

        wholeDoc += "\n\t// Update Operations\n\n";
        for (def in source['definitions']) {
            if (source['definitions'][def]['title'][0]['group'] == groupList[group]) {
                if (_.contains(TLC, def)) {
                    wholeDoc += ("\tpublic Map<String, Object> update" + def + "Resource(JsonElement requestBody, String " + idString + ") throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", requestBody, CONTEXT_FILTER);\n");
                    wholeDoc += ("\t\treturn response;\n\t}\n\n");
                } else {
                    var altID = "NO ID FOUND";
                    for (prop in source['definitions'][def]['properties']) {
                        if (prop.slice(-2, prop.length).toLowerCase() == 'id') {
                            altID = prop;
                        }
                    }
                    wholeDoc += ("\tpublic Map<String, Object> update" + def + "Resource(JsonElement requestBody, String " + idString + ", String " + altID + ") throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.updateResource(" + altID + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", " + idString + ", DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);\n");
                    wholeDoc += ("\t\treturn response;\n\t}\n\n");
                }
            }
        }

        wholeDoc += "\n\t// Delete Operations\n\n";
        for (def in source['definitions']) {
            if (source['definitions'][def]['title'][0]['group'] == groupList[group]) {
                if (_.contains(TLC, def)) {
                    wholeDoc += ("\tpublic Map<String, Object> deleteBatch" + def + "Resource(JsonElement requestBody) throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ");\n");
                    wholeDoc += ("\t\treturn response;\n\t}\n\n");

                    wholeDoc += ("\tpublic Map<String, Object> delete" + def + "Resource(String " + idString + ") throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.deleteResource(" + idString + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ");\n");
                    wholeDoc += "return response;\n\t}\n\n";
                } else {
                    var altID = "NO ID FOUND";
                    for (prop in source['definitions'][def]['properties']) {
                        if (prop.slice(-2, prop.length).toLowerCase() == 'id') {
                            altID = prop;
                        }
                    }
                    wholeDoc += ("\tpublic Map<String, Object> deleteBatch" + def + "Resource(JsonElement requestBody) throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ");\n");
                    wholeDoc += ("\t\treturn response;\n\t}\n\n");

                    wholeDoc += ("\tpublic Map<String, Object> delete" + def + "Resource(String " + idString + ", String " + altID + ") throws ServiceException {\n");
                    wholeDoc += ("\t\tMap<String, Object> response = ResourceUtils.deleteResource(" + altID + ", DATA_ITEM_NAME_" + toUnderscoreUpper(def) + ", " + idString + ", DEFAULT_PARENT_PROPERTY);\n");
                    wholeDoc += "\t\treturn response;\n\t}\n\n";
                }
            }
        }
        wholeDoc += "}";
        var fileName = destPath + groupList[group] + "Interface.java";
        fs.writeFile(fileName, wholeDoc, function(err) {
            if (err) {
                throw err;
            }
            console.log("Java Interface Written Successfully");
        });
        //console.log(wholeDoc);
    }
}

// Utility Functions
var toUnderscore = exports.toUnderscore = function(string) {
    var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
        return "_" + y.toLowerCase()
    }).replace(/^_/, "");
    return newString;
}

var toUnderscoreUpper = exports.toUnderscore = function(string) {
    var newString = string.replace(/\.?([A-Z]+)/g, function(x, y) {
        return "_" + y.toLowerCase()
    }).replace(/^_/, "");
    return newString.toUpperCase();
}

var toCamelCase = exports.toCamelCase = function capitalizeFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

var getArrTableName = exports.getArrTableName = function(str) {
    var lastIndex = str.lastIndexOf('/');
    lastIndex++;
    var newStr = str.slice(lastIndex, str.length);
    return newStr;
}

var getTLC = exports.getTLC = function(doc) {
    var nList = [];
    var tList = [];
    for (def in doc["definitions"]) {
        for (prop in doc["definitions"][def]["properties"]) {

            if (doc["definitions"][def]["properties"][prop]["type"] == "array") {
                nList.push(getArrTableName(doc["definitions"][def]["properties"][prop]["items"]["$ref"]));
            }
        }
    }
    for (def in doc["definitions"]) {
        for (i in nList) {
            if (!(_.contains(nList, def)) && !(_.contains(tList, def))) {
                tList.push(def)
            }
        }
    }
    //console.log(doc['definitions']);
    //console.log(nList);
    return tList;
}
