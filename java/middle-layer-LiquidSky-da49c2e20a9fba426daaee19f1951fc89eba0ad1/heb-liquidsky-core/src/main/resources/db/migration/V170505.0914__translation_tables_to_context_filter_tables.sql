/*
 * The intent of these tables is to store context filter information to support 
 * 		mapping Json Keys to DataType property names
 * 		filtering Json responses for different contexts (i.e. consumer facing vs. admin portal)
 * 		listing DataType properties that are required for creation
 * 		listing DataType properties that are restricted as uneditable after creation
 * 		(the last two may be removed if DataItem Property attributes are supported in the future)
 * 
 * Data is stored in the DB as a simple JSON string that provides for dynamic extensibility of translation information.
 */
use middle_layer;

drop table if exists lsdl_translation;

CREATE TABLE IF NOT EXISTS ls_context_filters (
	data_type_name varchar(256) not null,
	description varchar(2048),
	json_data mediumtext,
	creation_date datetime default current_timestamp,
	last_modified_date datetime default current_timestamp on update current_timestamp,
	primary key (data_type_name)
);

insert into middle_layer.ls_context_filters values('appProperties', 'AppProperties give information about a given app', 
'{\"dataItemName\":\"appProperties\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"appId\",\"propertyName\":\"appId\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"}]},{\"name\":\"AdminPortal\",\"filterMap\":[{\"jsonKey\":\"appId\",\"propertyName\":\"appId\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"},{\"jsonKey\":\"appVersions\",\"propertyName\":\"appVersions\"}]}],\"creationRequiredProperties\":[\"appId\"],\"restrictedProperties\":[\"appId\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.ls_context_filters values('appVersion', 'AppVersion entries belong to AppProperties and tell the latest supported version of an app', 
'{\"dataItemName\":\"appVersion\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"osName\",\"propertyName\":\"osName\"},{\"jsonKey\":\"osVersion\",\"propertyName\":\"osVersion\"}]},{\"name\":\"AdminPortal\",\"filterMap\":[{\"jsonKey\":\"appVersionId\",\"propertyName\":\"id\"},{\"jsonKey\":\"osName\",\"propertyName\":\"osName\"},{\"jsonKey\":\"osVersion\",\"propertyName\":\"osVersion\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"}]}],\"creationRequiredProperties\":[\"osName\",\"osVersion\"],\"restrictedProperties\":[\"id\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();