#/bin/sh
echo "Building and installing the CORE Jar file"
cd ../heb-liquidsky-core
pwd
mvn clean install -Dmaven.test.skip=true

echo "enabling debug port"
export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n"

echo "Building and running local server"
cd ../heb-liquidsky-service-adminrest
pwd
mvn clean spring-boot:run
