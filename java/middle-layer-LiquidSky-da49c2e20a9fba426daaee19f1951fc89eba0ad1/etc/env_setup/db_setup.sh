# this script creates a database instance and runs the sql to create the database

# Initialize our own variables:
PROJECT=""
DB_INSTANCE_NAME="middle-layer"
USAGE="db_setup -p <project_id> [-d <database_instance_name>: default='middle-layer]"

while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -p)
    PROJECT="$2"
    shift # past argument
    ;;
    -d)
    DB_INSTANCE_NAME="$2"
    shift # past argument
    ;;
    -h)
    echo "$USAGE"
    exit 0
    ;;
    *)
    echo "unknown option"
    exit 0
    ;;
esac
shift # past argument or value
done

if [ "$PROJECT" = "" ]
then
	echo "Error: PROJECT name must be specified"
	echo "Usage: $USAGE"
	exit 0
fi

echo "PROJECT=$PROJECT"
echo "Database Instance Name=$DB_INSTANCE_NAME"

# Select project to deploy cloud sql instance
gcloud config set project $PROJECT

# create the database instance
gcloud deployment-manager deployments create cloud-sql-deployment --config db-instance-setup.jinja --properties db_instance_name:$DB_INSTANCE_NAME

# make sure the bucket exists
gsutil mb -p $PROJECT gs://$PROJECT

# copy the sql file to the cloud storage
gsutil cp db/middle-layer.sql gs://$PROJECT/db/middle-layer.sql 

#give public permissions
gsutil acl ch -u AllUsers:R gs://$PROJECT/db/middle-layer.sql

# import sql file
gcloud sql instances import $DB_INSTANCE_NAME gs://$PROJECT/db/middle-layer.sql --project $PROJECT

# remove sql file
gsutil rm gs://$PROJECT/db/middle-layer.sql
