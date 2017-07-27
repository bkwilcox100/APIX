#/bin/sh
echo "copying the flyway sql file"
mv ./*.sql ../heb-liquidsky-core/src/main/resources/db/migration
echo "adding project to the parent pom file"
# get the project name from the current directory
PROJECT_NAME=${PWD##*/}
sed -i '/<\/modules>/i \\t\t<module>'"$PROJECT_NAME"'<\/module>' ../pom.xml
while true; do
    read -p "Do you want to run Flyway to install the database changes to your local database?" yn
    case $yn in
        [Yy]* ) mvn -f ../heb-liquidsky-core/pom.xml initialize flyway:migrate;break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes or no.";;
    esac
done
# append the new properties to the default.properties file
echo "adding properties to the ../build/environment-default.properties file"
cat environment-addToDefault.properties >> ../build/environment-default.properties
echo "project incorporation is complete"
