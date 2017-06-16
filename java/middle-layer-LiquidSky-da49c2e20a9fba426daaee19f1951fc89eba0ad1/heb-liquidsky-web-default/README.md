# heb-liquidsky-web-admin Module

## Description
This module hosts the jsp and static assets for the Admin Web service.  It does not expose any API, just hosts the jsp files.

## Dependencies
None.  This does nothing except serve up jsp files to tell people to go away.  May have TOS or other info later.

## Build Instructions

### Local Dev Server build

	mvn clean appengine:devserver
	
### Cloud Deployment

	mvn appengine:update

## API Keys

While this web application does not require an API key to access it, a key is needed to hit the services that it talks to.  Any keys that are configured for the project can be found here:

[https://console.cloud.google.com/apis/credentials](https://console.cloud.google.com/apis/credentials)

## Dispatch information (rout mapping)
This project also holds the dispatch information that maps paths to different services.  The following will deploy the dispatch settings to various environments

### dev (default)

	gcloud app deploy dispatch.yaml

### qa

	gcloud app deploy dispatch.yaml --project=heb-mls-qa1

### Production

	gcloud app deploy dispatch.yaml --project=heb-mls-prd1





