#/bin/sh
echo "Building and installing the CORE Jar file"
cd ../heb-liquidsky-core
pwd
mvn clean install

echo "Building and running local server"
cd ../heb-liquidsky-service-adminrest
pwd
mvn clean appengine:deploy
