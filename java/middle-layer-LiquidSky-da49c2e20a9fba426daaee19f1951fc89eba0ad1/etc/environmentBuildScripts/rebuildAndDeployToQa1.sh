#/bin/sh

read -p 'This will build and deploy all services to the heb-mls-qa1 project.  Press enter to continue or ctrl+c to cancel...'

echo "clean install of all services."
date
cd ~/git/middle-layer
mvn -Dliquidsky.build=qa1 -Dapp.deploy.project=heb-mls-qa1 clean install

echo "this will open several command windows and start multiple deployments.  A delay prevents build collision on shared assets."
cd heb-liquidsky-web-default
gnome-terminal -x bash -c "mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:update; read -p 'Build complete, Press enter to continue'"
sleep 5

cd ../heb-liquidsky-service-adminrest
gnome-terminal -x bash -c "mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 20

cd ../heb-liquidsky-service-apidiscovery
gnome-terminal -x bash -c "mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:deploy; read -p 'Build complete, Press enter to continue'"
sleep 20

cd ../heb-liquidsky-service-appversion
gnome-terminal -x bash -c "mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:deploy; read -p 'Build complete, Press enter to continue'"
echo "this only has a 10 second delay because the admin web doesn't have dependencies"
sleep 10

cd ../heb-liquidsky-web-admin
gnome-terminal -x bash -c "mvn -Dliquidsky.build=qa1 -Dmaven.test.skip=true -Dapp.deploy.project=heb-mls-qa1 appengine:update; read -p 'Build complete, Press enter to continue'"

echo "Build complete!"
