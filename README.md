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
+ Must include "group" attribute in every definition (can be the same group)

## Installation
*DO NOT ATTEMPT*

To install, download the APIX project and navigate to the 'js' folder.

    npm install APIX

## Features
+ Auto Generates Resources
    + XML Datastore
    + SQL Tables
    + Java Interface Files
    + Overview of Info (HTML Interface)

## History

TODO: Write history

## Credits

Developer: Brandon Wilcox

## License

TODO: Write license
