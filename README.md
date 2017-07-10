<p align="center">
  <img src="./apix_logo.png" width="200" />
</p>

# APIX or API Expander

A simple tool to expand Open API definitions into useful documentation and resources.

## Requirements
+ NodeJS
+ NPM

Along with the specifications found on the [Open API Repo](https://github.com/OAI/OpenAPI-Specification), here are some things to keep in mind:
+ The ID in every definition MUST end in "ID" (case-insensitive)
+ Any Date objects must end in "Date" (case-insensitive)
+ The primary key must be required and be the first property listed
+ All definitions must include a title property with a group attribute

## Installation
To install locally, download the APIX project and navigate to its directory. Run:

    $ npm install
    $ node apix -g

*UNTESTED*

To install globally, download the APIX and navigate to its directory. Run:

    $ npm install -g
    $ apix

## Usage
To run with a graphical interface:

    $ node apix -g

To run with a specified source and destination:

    $ node apix -s <source> -d <destination>

If no destination is specified, the current directory will be default

## Features
+ Auto Generates:
    + XML Datastore
    + SQL Tables
    + Java Interface Files
    + Overview of Info (HTML Interface)

## Credits

Developer: Brandon Wilcox

Property of HEB
