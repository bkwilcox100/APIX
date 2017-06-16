# Description
This directory holds any sql files that can be used by developers for adding test data or other needs.  Best practice is to fillow the same guideines as Flyway scripts that are in the ../migration directory

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
