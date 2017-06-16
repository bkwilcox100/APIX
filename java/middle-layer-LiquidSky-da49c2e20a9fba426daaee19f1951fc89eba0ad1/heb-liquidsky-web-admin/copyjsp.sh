#/bin/sh

cp src/main/webapp/*.jsp target/heb-liquidsky-web-admin-1.0.0/
cp src/main/webapp/includes/* target/heb-liquidsky-web-admin-1.0.0/includes/
cp -r src/main/webapp/admin/* target/heb-liquidsky-web-admin-1.0.0/admin
echo "done copying jsp files"
date
