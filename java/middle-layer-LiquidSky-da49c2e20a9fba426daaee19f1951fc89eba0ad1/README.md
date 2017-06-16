#Instructions to build

	echo "Build and install all components"
	cd ~/git/middle-layer
	mvn clean install
	
	echo "Deploy the AppVersion service"
	cd /home/heb/git/middle-layer/heb-liquidsky-service-appversion
	mvn initialize exec:java@GetOpenApiDoc
	mvn initialize exec:java@GetSwaggerUiSpec
	mvn initialize exec:exec@DeployOpenApiSpec
	mvn appengine:update
	
	echo "Deploy the API Discovery service"
	cd /home/heb/git/middle-layer/heb-liquidsky-service-apidiscovery
	mvn initialize exec:java@GetOpenApiDoc
	mvn initialize exec:java@GetSwaggerUiSpec
	mvn initialize exec:exec@DeployOpenApiSpec
	mvn appengine:update

#Project Notes

Projects are organized as follows:

- /src/main/appengine/app.yaml - The Google App Engine configuration descriptor file.
- /src/main/docker/Dockerfile - For Spring Boot WAR applications, this docker file descriptor tells app engine to use the base Java 8 image and to execute the generated Spring Boot WAR on that image.
- /src/main/java - All non-test Java code for the service is found in this directory.
- /src/main/resources/application.properties - For Spring Boot projects, this is the default Spring properties file.
- /src/main/resources/logging.properties - Configures logging for the project, following the java.util.logging specification.
- /src/main/resources/static - For Spring Boot WAR projects, this directory contains static HTML assets like CSS & JS files.
- /src/main/webapp/WEB-INF/web.xml - If the service requires configuration to be done in a web.xml file then that file would be placed here.
- /src/main/webapp/WEB-INF/jsp - For Spring Boot WAR projects, this directory holds all JSP files.
- /src/test/java - All Java code for unit tests are found in this directory.
- /src/test/resources - Property filese used for unit testing should be placed into this directory.
