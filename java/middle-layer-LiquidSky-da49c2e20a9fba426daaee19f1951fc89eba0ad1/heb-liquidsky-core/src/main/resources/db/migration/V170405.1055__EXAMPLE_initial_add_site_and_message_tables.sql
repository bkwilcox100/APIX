/*
 * This is intended to serve as an example flyway script.
 * There is no immediate need for these tables in the HEB Middle Layer, but may prove to be useful for site segmentation and generic messaging.
 * The intent of these tables will be documented here too so that if they are used generally they will be well understood.
 */

/*
* NEW SERVICE INSTRUCTION:
* Please include a USE statement for the schema to be used.  This is necessary if the script is run directly in mysql
*/
use middle_layer;

/*
* NEW SERVICE INSTRUCTION:
* You will usually want to use an IF NOT EXISTS phrase in a flyway script unless you include a 
* DROP TABLE IF EXISTS statement if you want to blow it all away and recreate it completely.  This is very
* useful to avoid conflicts in other developers environments, especially if the table is pre populated with tons of data 
* that would need to be imported again.
* 
* Liquid Sky Requirements:
* A primary key must be included.
* Creation_date and last_modified_date are required on all tables, exactly as in the example below
* Be mindful of how the data will be accessed and add indexes for any columns that would be queried for directly, like a non PK identifier.
*  
*/
CREATE TABLE IF NOT EXISTS heb_site (
	site_id varchar(40) not null,
    site_name varchar(128),
    description varchar(2048),
	creation_date datetime default current_timestamp,
	last_modified_date datetime default current_timestamp on update current_timestamp,
	primary key (site_id)
);

/*
* NEW SERVICE INSTRUCTION:
* This is an example of a child table that supports a one to many relationship (1 heb_site.site_id to many heb_message.site_id)  
*  
* Liquid Sky Requirements:
* Child tables must have a foreign key constraint.
*/
CREATE TABLE IF NOT EXISTS heb_message (
	message_id varchar(40) not null,
    reference_id varchar(40),
    site_id varchar(40),
    message varchar(2048),
	creation_date datetime default current_timestamp,
	last_modified_date datetime default current_timestamp on update current_timestamp,
	primary key (message_id),
    index reference_id (reference_id),
    foreign key (site_id) references heb_site(site_id)
);

/*
* NEW SERVICE INSTRUCTION:
* You can optionally include test data in the flyway script, but if you do so, then you will want to make sure that the ID names used would not 
* cause conflict with actual data.
* Liquid Sky provides for auto generated ID's with a prefix that is defined in the src/main/java/com/heb/liquidsky/data/data-store.xml
* file.  This is typically a few character prefix and the generated ID is of the format [PREFIX]_[NUMBER], so for test data, you can 
* make ID's like [prefix]_test[number] and there will be no conflict.  
* 
* It's usually a good idea to include the ON DUPLICATE KEY phrase  ON DUPLICATE KEY UPDATE last_modified_date=now();
* at the end of the insert statement so that if a script is run more than once, it will not cause failure.
*/
insert into middle_layer.heb_site values('site_test1', 'heb.com', 'The main HEB.com site', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_site values('site_test2', 'HEB Main App', 'The main HEB phone app for Android and iOs', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_message values('msg_test1', 'ERR_001', 'site_test1', 'An Error has occurred', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test2', 'ERR_002', 'site_test1', 'You can''t do that', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test3', 'ERR_003', 'site_test1', 'An error occurred while your uploaded image', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test4', 'MSG_001', 'site_test1', 'Your upload was processed successfully.', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test5', 'MSG_002', 'site_test1', 'Item successfully added to your shopping list.', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_message values('msg_test6', 'ERROR_10001', 'site_test2', 'You must be logged in to do that', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test7', 'ERROR_10002', 'site_test2', 'Sorry, that item was not found', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test8', 'HEADING_10001', 'site_test2', 'Your Shopping List', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test9', 'HEADING_10002', 'site_test2', 'Product Items', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test10', 'HEADING_10002', 'site_test2', 'Your Coupons', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_message values('msg_test11', 'HEADING_10002', 'site_test2', 'Your Recipes', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
