<p align="center">
<<<<<<< HEAD
  <img src="./apix_logo.png" width="100" height="100"/>
=======
  <img src="./apix_logo.png" style="width: 100px; height: 100px;"/>
>>>>>>> ac6880db5ec1896ecbdfa64e47dceadc66871aea
</p>

# APIX or API Expander

A simple tool to expand Open API definitions into useful documentation and resources.

## Usage
### Requirements
+ NodeJS
+ NPM

Along with the specifications found on the [Open API Repo](https://github.com/OAI/OpenAPI-Specification), here are some things to keep in mind:
+ The ID in every definition MUST end in "ID" (case-insensitive)
+ Any Date objects must end in "Date" (case-insensitive)
+ The primary key must be required and be the first property listed
+ All definitions must include a title property with a group attribute

## Installation

To install, download the APIX project and navigate to its directory. Run:

    $ npm install
    $ node apix.js

*UNTESTED*
To install globally, download the APIX and navigate to its directory. Run:

    $ npm install -g
    $ apix

## Features
+ Auto Generates:
    + XML Datastore
    + SQL Tables
    + Java Interface Files
    + Overview of Info (HTML Interface)

## History

TODO: Write history

## Credits

Developer: Brandon Wilcox

Property of HEB
