var jObj = {
  "swagger": "2.0",
  "info": {
    "title": "HEB Liquid Sky Admin Rest Endpoints",
    "description": "HEB Middle Layer Admin Rest endpoints",
    "version": "1.0.0"
  },
  "externalDocs": {
    "description": "Internal Confluence page",
    "url": "https://confluence.heb.com:8443/display/ESELLING/Admin+Rest+Microservice"
  },
  "host": "adminrest-dot-${appengine.app.id}.appspot.com",
  "x-google-endpoints": [{
    "name": "adminrest-dot-${appengine.app.id}.appspot.com",
    "allowCors": true
  }],
  "consumes": ["application/json"],
  "produces": ["application/json"],
  "schemes": ["https"],
  "paths": {
    "/adminrest/v1/appproperties": {
      "post": {
        "description": "Creates AppProperties resources",
        "operationId": "createBatchAppProperties",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of AppProperties resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all items from the AppProperties collection",
        "operationId": "readAppPropertiesCollection",
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/AppProperties"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes AppProperties resources based on a list of appid's sent in a list",
        "operationId": "deleteBatchAppPropertiesResource",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of AppProperties resource appid's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/appproperties/{appId}": {
      "get": {
        "description": "Reads the AppProperties resource with the given appId",
        "operationId": "readAppPropertiesResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/AppProperties"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the AppProperties resource with the given appId",
        "operationId": "updateAppPropertiesResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Full or partial AppProperties object containing values to update the specified resource with.",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/AppProperties"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the AppProperties resource with the given appId",
        "operationId": "deleteAppPropertiesResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/appproperties/{appId}/appversion": {
      "post": {
        "description": "Creates AppVersion resources",
        "operationId": "createBatchAppVersion",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Array of AppVersion resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "All resources were created successfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all AppVersion resources in the collection",
        "operationId": "readAppVersionCollection",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/AppVersion"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes AppVersion resources based on a list of appVersionId's sent in a list",
        "operationId": "deleteBatchAppVersionResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Array of AppVersion resource appVersionId's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/appproperties/{appId}/appversion/{appVersionId}": {
      "get": {
        "description": "Reads the AppVersion resource with the given appVersionId",
        "operationId": "readAppVersionResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "appVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/AppVersion"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the AppVersion resource with the given appVersionId",
        "operationId": "updateAppVersionResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "appVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "A complete or partial AppVersion object containing items to update",
          "required": true,
          "schema": {
            "$ref": "#/definitions/AppVersion"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/AppProperties"
            }
          },
          "default": {
            "description": "Some error occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the AppVersion resource with the given appVersionId",
        "operationId": "deleteAppVersionResource",
        "parameters": [{
          "name": "appId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "appVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection": {
      "post": {
        "description": "Creates ApiCollection resources",
        "operationId": "createBatchApiCollection",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ApiCollection resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all items from the ApiCollection collection",
        "operationId": "readApiCollectionCollection",
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/ApiCollection"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes ApiCollection resources based on a list of id's sent in a list",
        "operationId": "deleteBatchApiCollectionResource",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ApiCollection resource id's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}": {
      "get": {
        "description": "Reads the ApiCollection resource with the given Id",
        "operationId": "readApiCollectionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ApiCollection"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the ApiCollection resource with the given id",
        "operationId": "updateApiCollectionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Full or partial ApiCollection object containing values to update the specified resource with.",
          "required": true,
          "schema": {
            "$ref": "#/definitions/ApiCollection"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ApiCollection"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the ApiCollection resource with the given id",
        "operationId": "deleteApiCollectionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions": {
      "post": {
        "description": "Creates ServiceDescription resources",
        "operationId": "createBatchServiceDescription",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ServiceDescription resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }, {
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all items from the ServiceDescription collection",
        "operationId": "readServiceDescriptionCollection",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/ServiceDescription"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes ServiceDescription resources based on a list of id's sent in a list",
        "operationId": "deleteBatchServiceDescriptionResource",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ServiceDescription resource id's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }, {
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}": {
      "get": {
        "description": "Reads the ServiceDescription resource with the given Id",
        "operationId": "readServiceDescriptionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ServiceDescription"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the ServiceDescription resource with the given id",
        "operationId": "updateServiceDescriptionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Full or partial ServiceDescription object containing values to update the specified resource with.",
          "required": true,
          "schema": {
            "$ref": "#/definitions/ServiceDescription"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ServiceDescription"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the ServiceDescription resource with the given id",
        "operationId": "deleteServiceDescriptionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions": {
      "post": {
        "description": "Creates ServiceVersion resources",
        "operationId": "createBatchServiceVersion",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ServiceVersion resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }, {
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all items from the ServiceVersion collection",
        "operationId": "readServiceVersionCollection",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/ServiceVersion"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes ServiceVersion resources based on a list of id's sent in a list",
        "operationId": "deleteBatchServiceVersionResource",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ServiceVersion resource id's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }, {
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}": {
      "get": {
        "description": "Reads the ServiceVersion resource with the given Id",
        "operationId": "readServiceVersionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ServiceVersion"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the ServiceVersion resource with the given id",
        "operationId": "updateServiceVersionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Full or partial ServiceVersion object containing values to update the specified resource with.",
          "required": true,
          "schema": {
            "$ref": "#/definitions/ServiceVersion"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ServiceVersion"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the ServiceVersion resource with the given id",
        "operationId": "deleteServiceVersionResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths": {
      "post": {
        "description": "Creates ResourcePath resources",
        "operationId": "createBatchResourcePath",
        "parameters": [{
          "name": "body",
          "in": "body",
          "description": "Array of ResourcePath resources to create",
          "required": true,
          "schema": {
            "type": "object"
          }
        }, {
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "207": {
            "description": "Some resources created successfully and others unsuccessfully",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          }
        }
      },
      "get": {
        "description": "Reads all items from the ResourcePath collection",
        "operationId": "readResourcePathCollection",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/ResourcePath"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes ResourcePath resources based on a list of id's sent in a list",
        "operationId": "deleteBatchResourcePathResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Array of ResourcePath resource id's to delete. (array of strings)",
          "required": true,
          "schema": {
            "type": "object"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/BatchResponse"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths/{resourcePathId}": {
      "get": {
        "description": "Reads the ResourcePath resource with the given Id",
        "operationId": "readResourcePathResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "resourcePathId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ResourcePath"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "put": {
        "description": "Updates the ResourcePath resource with the given id",
        "operationId": "updateResourcePathResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "resourcePathId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "body",
          "in": "body",
          "description": "Full or partial ResourcePath object containing values to update the specified resource with.",
          "required": true,
          "schema": {
            "$ref": "#/definitions/ResourcePath"
          }
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/ResourcePath"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      },
      "delete": {
        "description": "Deletes the ResourcePath resource with the given id",
        "operationId": "deleteResourcePathResource",
        "parameters": [{
          "name": "collectionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceDescriptionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "serviceVersionId",
          "in": "path",
          "required": true,
          "type": "string"
        }, {
          "name": "resourcePathId",
          "in": "path",
          "required": true,
          "type": "string"
        }],
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "$ref": "#/definitions/SuccessMessage"
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    },
    "/adminrest/v1/auditlog": {
      "get": {
        "description": "Reads all items from the Audit Log",
        "operationId": "readAuditLogCollection",
        "responses": {
          "200": {
            "description": "A successful response",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/AuditLogEntry"
              }
            }
          },
          "default": {
            "description": "Some Error Occured",
            "schema": {
              "$ref": "#/definitions/ErrorResponse"
            }
          }
        }
      }
    }
  },
  "security": [{
    "api_key": []
  }],
  "securityDefinitions": {
    "api_key": {
      "type": "apiKey",
      "name": "key",
      "in": "query"
    },
    "google_id_token": {
      "authorizationUrl": "",
      "flow": "implicit",
      "type": "oauth2",
      "x-google-issuer": "https://accounts.google.com",
      "x-google-audiences": "547051530764-1g8ikir28c28v3mhkspemooojlru1mjp.apps.googleusercontent.com"
    },
    "Oauth2": {
      "authorizationUrl": "https://accounts.google.com/o/oauth2/auth",
      "description": "Oauth 2.0 implicit authentication",
      "flow": "implicit",
      "scopes": {
        "https://www.googleapis.com/auth/userinfo.email": "View your email address",
        "https://www.googleapis.com/auth/userinfo.profile": "View your basic profile info"
      },
      "type": "oauth2"
    }
  },
  "definitions": {
    "AppProperties": {
      "type": "object",
      "required": ["appId"],
      "properties": {
        "appId": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "orderNumber": {
          "type": "integer",
          "minimum": 0,
          "maximum": 64
        },
        "appVersions": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/AppVersion"
          }
        },
        "creationDate": {
          "type": "string"
        },
        "lastModifiedDate": {
          "type": "string"
        }
      }
    },
    "AppVersion": {
      "type": "object",
      "required": ["appVersionId", "osName", "osVersion"],
      "properties": {
        "appVersionId": {
          "type": "string"
        },
        "osName": {
          "type": "string"
        },
        "osVersion": {
          "type": "string"
        },
        "creationDate": {
          "type": "string"
        },
        "lastModifiedDate": {
          "type": "string"
        }
      }
    },
    "ResourcePath": {
      "properties": {
        "batchPath": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "id": {
          "type": "string"
        },
        "name": {
          "type": "string"
        },
        "path": {
          "type": "string"
        }
      }
    },
    "ServiceDescription": {
      "properties": {
        "currentVersion": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "documentation": {
          "type": "string"
        },
        "id": {
          "type": "string"
        },
        "labels": {
          "type": "string"
        },
        "name": {
          "type": "string"
        },
        "openApiSpecUrl": {
          "type": "string"
        },
        "serviceVersions": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ServiceVersion"
          }
        }
      }
    },
    "ServiceVersion": {
      "properties": {
        "basePath": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "hostName": {
          "type": "string"
        },
        "id": {
          "type": "string"
        },
        "openApiSpecUrl": {
          "type": "string"
        },
        "resourcePaths": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ResourcePath"
          }
        },
        "versionNumber": {
          "type": "string"
        }
      }
    },
    "ApiCollection": {
      "properties": {
        "contactInfo": {
          "type": "string"
        },
        "description": {
          "type": "string"
        },
        "id": {
          "type": "string"
        },
        "name": {
          "type": "string"
        },
        "serviceDescriptions": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ServiceDescription"
          }
        }
      }
    },
    "SuccessMessage": {
      "type": "object",
      "properties": {
        "success": {
          "type": "array",
          "items": {
            "type": "string"
          }
        }
      }
    },
    "Message": {
      "type": "object",
      "properties": {
        "code": {
          "type": "string"
        },
        "text": {
          "type": "string"
        }
      }
    },
    "ErrorResponse": {
      "type": "object",
      "properties": {
        "id": {
          "type": "string"
        },
        "status": {
          "type": "string"
        },
        "object": {
          "type": "object"
        },
        "message": {
          "$ref": "#/definitions/Message"
        }
      }
    },
    "BatchResponse": {
      "type": "object",
      "properties": {
        "success": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "errors": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/AppVersion"
          }
        }
      }
    },
    "AuditLogEntry": {
      "type": "object",
      "properties": {
        "changeId": {
          "type": "string"
        },
        "dataItemType": {
          "type": "string"
        },
        "itemId": {
          "type": "string"
        },
        "userId": {
          "type": "string"
        },
        "operation": {
          "type": "string"
        },
        "jsonResponse": {
          "type": "string"
        },
        "creationDate": {
          "type": "string"
        },
        "lastModifiedDate": {
          "type": "string"
        }
      }
    },
    "JsonMap": {}
  }
};

function displayBasicInfo() {
  document.getElementById('api-name').innerHTML = jObj.info.title;
  document.getElementById('api-description').innerHTML = ("<b>Description:</b> " + jObj.info.description + "<br>");
  if (jObj.info.hasOwnProperty('contact')){
    if (jObj.info.contact.hasOwnProperty('name')) {
      document.getElementById('api-author').innerHTML = ("<b>Author:</b> " + jObj.info.contact.name + "<br>");
    }
  }
  if (jObj.hasOwnProperty('externalDocs')) {
    document.getElementById('api-ex-doc').innerHTML = ("<b>External Documentation: </b><a href=\"" + jObj.externalDocs.url + "\">Confluence</a>");
  }
  document.getElementById('api-host').innerHTML = ("<b>Host:</b> " + jObj.host);
}


function displayOASpec(){
  document.getElementById('json-plug').innerHTML = JSON.stringify(jObj, null, 2);
}

function createPathTable() {
  var counter = 0;
  var wholeTable = "<table> <tr><th>Name</th><th>GET</th><th>POST</th><th>PUT</th><th>DELETE</th></tr><tr>";
  for(path in jObj["paths"]){
    wholeTable += ("<tr>")
    wholeTable += ("<td>" + path + "</td>");
    wholeTable += ("<td>" + jObj["paths"][path].hasOwnProperty('get') + "</td>");
    wholeTable += ("<td>" + jObj["paths"][path].hasOwnProperty('post') + "</td>");
    wholeTable += ("<td>" + jObj["paths"][path].hasOwnProperty('put') + "</td>");
    wholeTable += ("<td>" + jObj["paths"][path].hasOwnProperty('delete') + "</td>");
    wholeTable += "</tr>";
    counter++;
  }
  wholeTable += "</tr></table>";
  document.getElementById('OATable-paths').innerHTML = wholeTable;
}

function createDefinitionTable() {
  var counter = 0;
  var wholeTable = "<table> <tr><th>Name</th><th>Properties</th><th>Required</th></tr>";
  wholeTable += ("<tr>");
  for(def in jObj["definitions"]){
    wholeTable += ("<td>" + def + "</td>");
    wholeTable += "<td>";
    if (jObj["definitions"][def].hasOwnProperty("properties")){
      for (prop in jObj["definitions"][def]["properties"]){
        wholeTable += (prop + ", ");
      }
    } else {
      wholeTable += "none, ";
    }
    wholeTable = wholeTable.slice(0, wholeTable.length - 2);
    wholeTable += "</td>";
    wholeTable += "<td>";
    if (jObj["definitions"][def].hasOwnProperty("required")){
      for (prop in jObj["definitions"][def]["required"]){
        wholeTable += (jObj["definitions"][def]["required"][prop] + ", ");
      }
    } else {
      wholeTable += "none, ";
    }
    wholeTable = wholeTable.slice(0, wholeTable.length - 2);
    wholeTable += "</td>";
    wholeTable += "</tr>";
    counter++;
  }
  wholeTable += "</tr></table>";
  document.getElementById('OATable-definitions').innerHTML = wholeTable;
}

function runFunctions(){
  displayBasicInfo();
  displayOASpec();
  createPathTable();
  createDefinitionTable();
}
