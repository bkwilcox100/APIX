use middle_layer;

CREATE TABLE IF NOT EXISTS heb_app_properties (
	appId varchar(64) not null,
	description varchar(1024),
	orderNumber varchar(64),
	creationDate datetime default current_timestamp,
	lastModifiedDate datetime default current_timestamp on update current_timestamp,
	primary key (appId)
);

CREATE TABLE IF NOT EXISTS heb_app_version (
	appVersionId varchar(64) not null,
	osName varchar(1024) not null,
	osVersion varchar(1024) not null,
	creationDate datetime default current_timestamp,
	lastModifiedDate datetime default current_timestamp on update current_timestamp,
	primary key (appVersionId)
);

CREATE TABLE IF NOT EXISTS heb_resource_path (
	batchPath varchar(1024),
	description varchar(1024),
	id varchar(64),
	name varchar(1024),
	path varchar(1024),
	primary key (id)
);

CREATE TABLE IF NOT EXISTS heb_service_description (
	currentVersion varchar(1024),
	description varchar(1024),
	documentation varchar(1024),
	id varchar(64),
	labels varchar(1024),
	name varchar(1024),
	openApiSpecUrl varchar(1024),
	primary key (id)
);

CREATE TABLE IF NOT EXISTS heb_service_version (
	basePath varchar(1024),
	description varchar(1024),
	hostName varchar(1024),
	id varchar(64),
	openApiSpecUrl varchar(1024),
	versionNumber varchar(1024),
	primary key (id)
);

CREATE TABLE IF NOT EXISTS heb_api_collection (
	contactInfo varchar(1024),
	description varchar(1024),
	id varchar(64),
	name varchar(1024),
	primary key (id)
);

