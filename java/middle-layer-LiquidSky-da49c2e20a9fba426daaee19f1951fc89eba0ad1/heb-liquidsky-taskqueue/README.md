#heb-liquidsky-service-taskqueue Module

#Description
This module is the used for processing long-running tasks in a way that takes load off of customer-facing instances.

#Local Build Instructions

Run these commands in a terminal to build this module in a local development environment.

    echo "Building all modules"
    cd $GIT_HOME/middle-layer/
    mvn clean install -Dmaven.test.skip=true

    echo "Deploying to the cloud"
    cd $GIT_HOME/middle-layer/heb-liquidsky-service-taskqueue
    mvn appengine:deploy
