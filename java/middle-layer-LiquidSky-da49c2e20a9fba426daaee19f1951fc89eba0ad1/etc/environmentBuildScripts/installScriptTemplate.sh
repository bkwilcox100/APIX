#/bin/sh

gsutil mb -p {{gcp.project.name}} gs://{{gcp.project.name}}-tmpdata
cd /home/heb/git/middle-layer/heb-liquidsky-core/src/main/resources/db/migration/
gsutil cp *.sql gs://{{gcp.project.name}}-tmpdata/

cd /home/heb/git/middle-layer/heb-liquidsky-core/src/main/resources/db/development/
gsutil cp V160101.0900__drop_all_tables.sql gs://{{gcp.project.name}}-tmpdata/

gsutil acl ch -u AllUsers:R gs://{{gcp.project.name}}-tmpdata/*
gsutil list -p {{gcp.project.name}} gs://{{gcp.project.name}}-tmpdata/

gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V160101.0900__drop_all_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V161130.0858__initial_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V161206.0858__shopping_list_tables_01.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V161208.1210__prod_sku_timestamps.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V161212.1345__heb_id_generator.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V161216.1019__shopping_list_tables_02.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170105.1042__app_settings.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170120.1343__atg_product_store_assortment_data_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170123.1114__modify_atg_product_store_assortment_data_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170124.1327__add_shopping_list_site_id.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170125.1012__add_atg_recipe_table.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170202.1030__remove_item_xref_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170206.1042__drop_app_settings.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170206.2042__drop_test_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170214.1117__modify_owner_id_length.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170313.0945__add_atg_table_pk_and_index.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170313.1003__add_recipe_table_pk.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170313.1039__add_ownerId_index.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170324.1152__initial_add_appversion_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170405.1055__EXAMPLE_initial_add_site_and_message_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170411.0830__initial_add_api_discovery_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170502.0802__initial_add_translation_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170503.0536__initial_add_audit_log.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170505.0914__translation_tables_to_context_filter_tables.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170511.0948__context_filters_for_api_discovery.sql' --project={{gcp.project.name}} -q
gcloud sql instances import heb-mysql 'gs://{{gcp.project.name}}-tmpdata/V170526.0931__bugfix_context_filters.sql' --project={{gcp.project.name}} -q

gsutil rm gs://{{gcp.project.name}}-tmpdata/*
gsutil rb gs://{{gcp.project.name}}-tmpdata/

cd ~/git/middle-layer
mvn -Dliquidsky.build={{liquidsky.build.file}} clean install

gcloud service-management deploy heb-liquidsky-service-adminrest/target/openapi.yaml --project={{gcp.project.name}}
gcloud service-management deploy heb-liquidsky-service-apidiscovery/target/openapi.yaml --project={{gcp.project.name}}
gcloud service-management deploy heb-liquidsky-service-appversion/target/openapi.yaml --project={{gcp.project.name}}

# Set the Endpoints version properties
echo "You will now need to enter some settings in a properties file.  First, make note of the the Endpoints api version numbers from the above commands for each service, like: 2017-06-02r0" 
echo "This script will now open Chrome and the properties file.  In the properties file, set the Endpoints version numbers for each service Example:"
echo "appengine.app.modules.adminrest.endpoints.config.id=2017-06-02r0"
echo "appengine.app.modules.apidiscovery.endpoints.config.id=2017-06-02r0"
echo "appengine.app.modules.appversion.endpoints.config.id=2017-06-02r0"
echo "appengine.app.modules.shoppinglist.endpoints.config.id=2017-06-02r0"
echo "--"
echo "Additionally, Chrome will now open to the API key page.  Click the OAuth2.0 Client Id that is auto created with the name: Web client (auto created by Google Service)"
echo "Copy and paste the client ID and Client Secret into the properties file.  Example:"
echo "appengine.app.modules.adminweb.clientID=743148339646-1kmll624ev5svodar9mfdidfnacna4e6.apps.googleusercontent.com"
echo "appengine.app.modules.adminweb.clientSecret=ra3F-hq35QW-kMMzbqBcrXik"
echo "--"
echo "Finally, this will also open a js file where the Api Key and restHost need to be updated."  
echo "The Rest Host should be set to adminrest-dot-{{gcp.project.name}}.appspot.com"
echo "The Api key should be set to the default key under API Keys in the GCP console that was opened in Chrome"
echo "Example:"
echo "restHost: \"adminrest-dot-{{gcp.project.name}}.appspot.com\","
echo "apiKey: \"AIzaSyC18AzEVq7UOTlbWVLjJSKGTpdwcnBdnuk\","
echo " -- "
read -p "Press enter to open chrome and gedit with the files to edit."

google-chrome-stable https://console.developers.google.com/apis/credentials?project={{gcp.project.name}}
gedit build/environment-{{liquidsky.build.file}}.properties
gedit heb-liquidsky-web-admin/src/main/webapp/admin/resources/adminWeb.js
read -p "Press enter to continue"
read -p "You really finished adding the api keys and saved the properties file, right? Press enter to continue"

cd heb-liquidsky-web-default
mvn -Dliquidsky.build={{liquidsky.build.file}} -Dmaven.test.skip=true -Dapp.deploy.project={{gcp.project.name}} appengine:update

echo "this will open several command windows and start multiple deployments.  a 30 second delay prevents build collision on shared assets"
cd ../heb-liquidsky-service-adminrest
gnome-terminal -x bash -c "mvn -Dliquidsky.build={{liquidsky.build.file}} -Dmaven.test.skip=true -Dapp.deploy.project={{gcp.project.name}} appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 30

cd ../heb-liquidsky-service-apidiscovery
gnome-terminal -x bash -c "mvn -Dliquidsky.build={{liquidsky.build.file}} -Dmaven.test.skip=true -Dapp.deploy.project={{gcp.project.name}} appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 30

cd ../heb-liquidsky-service-appversion
gnome-terminal -x bash -c "mvn -Dliquidsky.build={{liquidsky.build.file}} -Dmaven.test.skip=true -Dapp.deploy.project={{gcp.project.name}} appengine:deploy; read -p 'Build complete, Press enter to continue'"
echo "this only has a 10 second delay because the admin web doesn't have dependencies"
sleep 10

cd ../heb-liquidsky-web-admin
gnome-terminal -x bash -c "mvn -Dliquidsky.build={{liquidsky.build.file}} -Dmaven.test.skip=true -Dapp.deploy.project={{gcp.project.name}} appengine:update; read -p 'Build complete, Press enter to continue'"


#gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:antbyteslabs@gmail.com' --role='roles/owner'
gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:rholliday@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:emccrorie@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:tsteiger@commerce-architects.com' --role='roles/editor'
gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:bhewett67@gmail.com' --role='roles/editor'
gcloud projects add-iam-policy-binding {{gcp.project.name}} --member='user:vijayakarthikeyanarul@gmail.com' --role='roles/viewer'
echo "Cannot create Owner roles, so you will need to manually upgrade anyones access to owner who needs it."

echo "Build complete!"
echo "Build complete!"
echo "Build complete!"
echo "Build complete!"
echo "Build complete!"

echo "Make API Keys here:  https://console.cloud.google.com/apis/credentials"
echo "make oAuth2 Client Id's (then add to heb-liquidsky-web-admin project and redeploy:  https://console.cloud.google.com/apis/credentials"
echo "edit the file /heb-liquidsky-web-admin/src/main/webapp/admin/resources/adminWeb.js to enter the api key and adminrest url"



# For some reason cloud sql api was not enabled for the project.  had to go to https://console.cloud.google.com/apis/api/sqladmin/overview?project={{gcp.project.name}}  to enable it.
# same for google plus api  https://console.developers.google.com/apis/api/plus.googleapis.com/overview?project={{gcp.project.name}}
# TODO: Add those api's to the ant script.

