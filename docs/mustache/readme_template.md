# heb-liquidsky-service-{{serviceName}} Module

### Auto-Generated with APIX

# Description
This module contains rest endpoints for the {{serviceName}} service that provides all of the REST interactions for the Admin Portal

# Local Build Instructions

Enable the debugger.

	export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n"

Run the local dev server

	mvn spring-boot:run


# Cloud build (for default)
deploy the openapi.yaml file to cloud endpoints.  Be sure to only deploy the one in the target directory after a build as it will have the interpolated host name where as the one in the base directory does not.

	mvn clean package
	gcloud service-management deploy target/openapi.yaml

should get an output line like

	Service Configuration [2017-05-12r0] uploaded for service [{{serviceName}}-dot-heb-javaapptest.appspot.com]

copy the bit that is in brackets ( like 2017-05-12r0 ) and paste it into the value for appengine.app.modules.{{serviceName}}.endpoints.config.id in the file /heb-liquidsky-base/build/environment-default.properties, then the binary can be built and deployed.

deploy to cloud

	mvn appengine:deploy

# Cloud QA build

	mvn -Dliquidsky.build=qa1 clean package
	gcloud --project=heb-mls-qa1 service-management deploy target/openapi.yaml

copy the bit that is in brackets ( like 2017-05-12r0 ) and paste it into the value for appengine.app.modules.{{serviceName}}.endpoints.config.id in the file /heb-liquidsky-base/build/environment-qa.properties, then the binary can be built and deployed.


	mvn -Dliquidsky.build=qa1 -Dapp.deploy.project=heb-mls-qa1 appengine:deploy
