#/bin/sh
echo "copying the flyway sql file"
mv ./*.sql ../heb-liquidsky-core/src/main/resources/db/migration
echo "Checking the master pom file for this module"
# get the project name from the current directory
PROJECT_NAME=${PWD##*/}
if grep -Fq "<module>$PROJECT_NAME</module>" ../pom.xml
then
    echo "Found $PROJECT_NAME in the pom, skipping..."
else
	echo "could not find $PROJECT_NAME in the pom.  Adding this new module"
	sed -i '/<\/modules>/i \\t\t<module>'"$PROJECT_NAME"'<\/module>' ../pom.xml
fi

while true; do
    read -p "Do you want to run Flyway to install the database changes to your local database?" yn
    case $yn in
# This next line can be used instead if you are repeatedly testing this script
#        [Yy]* ) mvn -f ../heb-liquidsky-core/pom.xml initialize flyway:clean flyway:migrate;break;;
		[Yy]* ) mvn -f ../heb-liquidsky-core/pom.xml initialize flyway:migrate;break;;
        [Nn]* ) break;;
        * ) echo "Please answer yes or no.";;
    esac
done
# append the new properties to the default.properties file
echo "adding properties to the ../build/environment-default.properties file"
cat environment-addToDefault.properties >> ../build/environment-default.properties
echo "project incorporation is complete"