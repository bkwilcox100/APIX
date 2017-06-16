#!/bin/bash
echo "Deploying liquid sky default service to the cloud"
# Ex gcloud config set project heb-ml-integration
gcloud config set project $GCLOUD_PROJECT
# gcloud auth activate-service-account --key-file "$JENKINS_HOME/HEB-Middle-Layer-Integration-c6882d20b87c.json" --project heb-ml-integration
gcloud auth activate-service-account --key-file "$JENKINS_HOME/$SECURITY_FILE"
# EX: gcloud config set account jenkins@heb-ml-integration.iam.gserviceaccount.com
gcloud config set account $SERVICE_ACCOUNT

echo "Working directory: $WORKSPACE/heb-liquidsky-service-default"
cd "$WORKSPACE/heb-liquidsky-service-default"
mvn appengine:deploy -DskipTests -Dliquidsky.build=$PROPERTIES_FILE