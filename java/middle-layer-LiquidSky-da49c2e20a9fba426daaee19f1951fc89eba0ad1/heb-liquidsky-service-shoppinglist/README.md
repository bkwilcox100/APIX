#heb-liquidsky-service-shoppinglist Module

#Description
This module contains rest endpoint for Shopping lists, including retrieval of shopping list(s) for the current user, adding new shopping list(s), and updating shopping list(s).

#Local Build Instructions

Run these commands in a terminal to build this module in a local development environment.

    echo "Building all modules"
    cd $GIT_HOME/middle-layer/
    mvn clean install -Dmaven.test.skip=true

    echo "Deploying the Open API spec to the cloud"
    cd $GIT_HOME/middle-layer/heb-liquidsky-service-shoppinglist
    gcloud service-management deploy target/openapi.yaml
    # The output of the above command will include something like the following:
    #    Service Configuration [config_id] uploaded for service [name]
    # Update the appengine.app.modules.shoppinglist.endpoints.config.id
    # environment property with the config_id value.

    echo "Deploying to the cloud"
    cd $GIT_HOME/middle-layer/heb-liquidsky-service-shoppinglist
    mvn clean install -Dmaven.test.skip=true
    mvn appengine:deploy
