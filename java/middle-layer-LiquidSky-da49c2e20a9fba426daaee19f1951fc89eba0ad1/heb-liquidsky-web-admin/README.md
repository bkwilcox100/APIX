# heb-liquidsky-web-admin Module

## Description
This module hosts the jsp and static assets for the Admin Web service.  It does not expose any API, just hosts the jsp files.

## Dependencies
None.  While this stands alone, it does talk to a deployed adminrest service that hosts all of the Admin REST api's

## Build Instructions

### Local Dev Server build

	mvn -Plocal clean appengine:devserver
	
### Default Cloud Deployment

	mvn appengine:update
	
### QA Cloud Deployment

	mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:update

## API Keys

While this web application does not require an API key to access it, a key is needed to hit the services that it talks to.  Any keys that are configured for the project can be found here:

[https://console.cloud.google.com/apis/credentials](https://console.cloud.google.com/apis/credentials)

	





