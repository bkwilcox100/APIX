#/bin/sh

gsutil mb -p heb-mls-dev-tom gs://heb-mls-dev-tom-tmpdata
cd /home/heb/git/middle-layer/heb-liquidsky-core/src/main/resources/db/migration/
gsutil cp *.sql gs://heb-mls-dev-tom-tmpdata/

cd /home/heb/git/middle-layer/heb-liquidsky-core/src/main/resources/db/development/
gsutil cp V160101.0900__drop_all_tables.sql gs://heb-mls-dev-tom-tmpdata/

gsutil acl ch -u AllUsers:R gs://heb-mls-dev-tom-tmpdata/*
gsutil list -p heb-mls-dev-tom gs://heb-mls-dev-tom-tmpdata/

gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V160101.0900__drop_all_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V161130.0858__initial_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V161206.0858__shopping_list_tables_01.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V161208.1210__prod_sku_timestamps.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V161212.1345__heb_id_generator.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V161216.1019__shopping_list_tables_02.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170105.1042__app_settings.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170120.1343__atg_product_store_assortment_data_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170123.1114__modify_atg_product_store_assortment_data_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170124.1327__add_shopping_list_site_id.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170125.1012__add_atg_recipe_table.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170202.1030__remove_item_xref_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170206.1042__drop_app_settings.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170206.2042__drop_test_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170214.1117__modify_owner_id_length.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170313.0945__add_atg_table_pk_and_index.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170313.1003__add_recipe_table_pk.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170313.1039__add_ownerId_index.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170324.1152__initial_add_appversion_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170405.1055__EXAMPLE_initial_add_site_and_message_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170411.0830__initial_add_api_discovery_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170502.0802__initial_add_translation_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170503.0536__initial_add_audit_log.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170505.0914__translation_tables_to_context_filter_tables.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170511.0948__context_filters_for_api_discovery.sql' --project=heb-mls-dev-tom -q
gcloud sql instances import heb-mysql 'gs://heb-mls-dev-tom-tmpdata/V170526.0931__bugfix_context_filters.sql' --project=heb-mls-dev-tom -q

gsutil rm gs://heb-mls-dev-tom-tmpdata/*
gsutil rb gs://heb-mls-dev-tom-tmpdata/

cd ~/git/middle-layer
mvn -Dliquidsky.build=tom clean install

gcloud service-management deploy heb-liquidsky-service-adminrest/target/openapi.yaml --project=heb-mls-dev-tom
gcloud service-management deploy heb-liquidsky-service-apidiscovery/target/openapi.yaml --project=heb-mls-dev-tom
gcloud service-management deploy heb-liquidsky-service-appversion/target/openapi.yaml --project=heb-mls-dev-tom

echo "update the build file build/environment-tom.properties with the api version number from the above command, like: 2017-06-02r0"
gedit build/environment-tom.properties
read -p "Press enter to continue"

cd heb-liquidsky-web-default
mvn -Dliquidsky.build=tom appengine:deploy
mvn -Dliquidsky.build=tom -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-dev-tom appengine:update

echo "this will open several command windows and start multiple deployments.  a 30 second delay prevents build collision on shared assets"
cd ../heb-liquidsky-service-adminrest
gnome-terminal -x bash -c "mvn -Dliquidsky.build=tom -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-dev-tom appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 30

cd ../heb-liquidsky-service-apidiscovery
gnome-terminal -x bash -c "mvn -Dliquidsky.build=tom -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-dev-tom appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 30

cd ../heb-liquidsky-service-appversion
gnome-terminal -x bash -c "mvn -Dliquidsky.build=tom -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-dev-tom appengine:deploy; read -p 'Build complete, Press enter to continue'"
echo "this only has a 10 second delay because the admin web doesn't have dependancies"
sleep 10

cd ../heb-liquidsky-web-admin-standard
gnome-terminal -x bash -c "mvn -Dliquidsky.build=tom -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-dev-tom appengine:update; read -p 'Build complete, Press enter to continue'"


#gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:antbyteslabs@gmail.com' --role='roles/owner'
gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:rholliday@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:emccrorie@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:tsteiger@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:bhewett67@gmail.com' --role='roles/editor'
gcloud projects add-iam-policy-binding heb-mls-dev-tom --member='user:vijayakarthikeyanarul@gmail.com' --role='roles/viewer'
echo "Cannot create Owner roles, so you will need to manually upgrade anyones access to owner who needs it."

echo "Build complete!"
echo "Build complete!"
echo "Build complete!"
echo "Build complete!"
echo "Build complete!"
echo "Make API Keys here:  https://console.cloud.google.com/apis/credentials"
echo "make oAuth2 Client Id's (then add to heb-liquidsky-web-admin project and redeploy:  https://console.cloud.google.com/apis/credentials"
echo "edit the file /heb-liquidsky-web-admin/src/main/webapp/admin/resources/adminWeb.js to enter the api key and adminrest url"



# For some reason cloud sql api was not enabled for the project.  had to go to https://console.cloud.google.com/apis/api/sqladmin/overview?project=heb-mls-dev-tom  to enable it.
# same for google plus api  https://console.developers.google.com/apis/api/plus.googleapis.com/overview?project=heb-mls-dev-tom

