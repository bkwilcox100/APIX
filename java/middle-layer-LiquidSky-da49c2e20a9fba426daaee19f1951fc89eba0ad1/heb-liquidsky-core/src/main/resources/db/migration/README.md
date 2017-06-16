# Description
This directory holds any Flyway files that are used to manipulate the database for this micro service.

| Maven Goal                             | Description                                                                         |
|----------------------------------------|-------------------------------------------------------------------------------------|
| initialize flyway:migrate              | Runs all scripts in the migration directory                                         |
| initialize flyway:clean flyway:migrate | Clears all tables and rebuilds the schema from the scripts in the migration tables. |
|                                        |                                                                                     |

# Notes
Scripts must always start with selecting the schema to use, as these scripts must sometimes be uploaded and run manually in cloud instances.
e.g.

    USE middle_layer;

File names must always follow the format VYYMMDD.HHmm__Description.sql where
YY = 2 character year
MM = 2 character month
DD = 2 character Day
HH = 2 character Hour
mm = 2 character minute

Example:
V161130.0858__initial_tables.sql

This is to ensure that files are always run in order.

It is usually good practice to create tables with an IF NOT EXISTS clause in case scripts need to be re-run and may contain lots of data

Example:

    CREATE TABLE IF NOT EXISTS heb_example
    
When inserting test data, then it is probably a good idea to add a ON DUPLICATE KEY UPDATE clause so that the script will not error out if the data is already there because it had been run before.

Example:

	insert into middle_layer.heb_app_version values('av_test1', 'default', 'iOs', '1.9', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

