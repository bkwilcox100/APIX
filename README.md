<p align="center">
  <img src="./apix_logo.png" width="200" />
</p>

# APIX or API Expander

A simple tool to expand Open API definitions into useful documentation and resources.

## Requirements
+ NodeJS
+ NPM

Along with the specifications found on the [Open API Repo](https://github.com/OAI/OpenAPI-Specification), here are some things to keep in mind:
+ Must include a tags field with a name and description item (See Example)
+ The ID in every definition MUST end in "ID" (case-insensitive)
+ Any Date objects must end in "Date" (case-insensitive)
+ The primary key must be required and be the first property listed

## Installation
To install locally, download the APIX project and navigate to its directory. Run:

    $ npm install
    $ node apix -g

*UNTESTED*

To install globally, download the APIX and navigate to its directory. Run:

    $ npm install -g
    $ apix

## Usage
To generate a starter Open API spec:

    $ node apix init

To run with a graphical interface:

    $ node apix -g

To run with a specified source and destination:

    $ node apix -s <source> -d <destination>

If no destination is specified, the current directory will be default

## Features
+ Auto Generates:
    + Open API Spec
    + App Engine Spec
    + XML Datastore
    + SQL Tables
    + Java Interface Files
    + Java Servlet Files

## Example
    swagger: "2.0"

    info:
      title: "Batcave API"
      description: "The API HEB deserves, but not the one it needs right now"
      version: "1.0.0"
      contact:
        name: "Bruce Wayne"
        email: "notbrucewayne@batcave.com"

    host: localhost:8080
    basePath: /api/

    tags:
      - name: serviceName
        description: batman-rest

    x-google-endpoints:
        - name: adminrest-dot-${appengine.app.id}.appspot.com
          allowCors: true

    consumes:
        - application/json
    produces:
        - application/json
    schemes:
        - https

    paths:
      /criminals:
        get:
          description: "Returns all criminals in databases"
          responses:
            200:
              description: "Successfully returns list of criminals"
              schema:
                type: "array"
                items:
                  $ref: "#/definitions/criminal"
            default:
              description: "Failed to return criminals"
              schema:
                type: object
                $ref: "#/definitions/error"
        post:
          description: "Add new criminal to database"
          parameters:
            - name: criminal
              in: body
              description: "The criminal added to database"
              required: true
              schema:
                type: object
          responses:
            200:
              description:  "New criminal was successfully created"
              schema:
                type: object
                $ref: "#/definitions/BatchResponse"
            default:
              description: "An error occurred"
              schema:
                $ref: "#/definitions/error"

    definitions:
      criminal:
        type: object
        required:
          - "criminalID"
          - "name"
          - "crime"
        properties:
          criminalID:
            type: integer
            format: int64
          name:
            type: string
          crime:
            type: string

      hero:
        type: object
        required:
          - "id"
          - "name"
          - "power"
        properties:
          id:
            type: integer
            format: int64
          name:
            type: string
          power:
            type: string


## Credits

Developer: Brandon Wilcox

Property of HEB
