echo "Deploy the AppVersion service"
# Ex gcloud config set project heb-ml-integration
gcloud config set project $GCLOUD_PROJECT
# gcloud auth activate-service-account --key-file "$JENKINS_HOME/HEB-Middle-Layer-Integration-c6882d20b87c.json" --project heb-ml-integration
gcloud auth activate-service-account --key-file "$JENKINS_HOME/$SECURITY_FILE"
# EX: gcloud config set account jenkins@heb-ml-integration.iam.gserviceaccount.com
gcloud config set account $SERVICE_ACCOUNT

cd "$WORKSPACE/$PROJECT_DIR"
mvn initialize exec:java@GetOpenApiDoc -Dliquidsky.build=$PROPERTIES_FILE
mvn initialize exec:java@GetSwaggerUiSpec -Dliquidsky.build=$PROPERTIES_FILE
mvn initialize exec:exec@DeployOpenApiSpec -Dliquidsky.build=$PROPERTIES_FILE
