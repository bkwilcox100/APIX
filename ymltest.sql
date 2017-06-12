CREATE TABLE IF NOT EXISTS heb_AppProperties (
	appId varchar(64) not null,
	description varchar(1024),
	creationDate datetime default current_timestamp,
	lastModifiedDate datetime default current_timestamp,
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_AppVersion (
	appVersionId varchar(64) not null,
	osName varchar(1024) not null,
	osVersion varchar(1024) not null,
	creationDate datetime default current_timestamp,
	lastModifiedDate datetime default current_timestamp,
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_ResourcePath (
	batchPath varchar(1024),
	description varchar(1024),
	id varchar(1024),
	name varchar(1024),
	path varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_ServiceDescription (
	currentVersion varchar(1024),
	description varchar(1024),
	documentation varchar(1024),
	id varchar(1024),
	labels varchar(1024),
	name varchar(1024),
	openApiSpecUrl varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_ServiceVersion (
	basePath varchar(1024),
	description varchar(1024),
	hostName varchar(1024),
	id varchar(1024),
	openApiSpecUrl varchar(1024),
	versionNumber varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_ApiCollection (
	contactInfo varchar(1024),
	description varchar(1024),
	id varchar(1024),
	name varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_SuccessMessage (
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_Message (
	code varchar(1024),
	text varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_ErrorResponse (
	id varchar(1024),
	status varchar(1024),
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_BatchResponse (
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_AuditLogEntry (
	changeId varchar(64),
	dataItemType varchar(1024),
	itemId varchar(64),
	userId varchar(64),
	operation varchar(1024),
	jsonResponse varchar(1024),
	creationDate datetime default current_timestamp,
	lastModifiedDate datetime default current_timestamp,
	primary key (primaryKey not set)
);

CREATE TABLE IF NOT EXISTS heb_JsonMap (
	primary key (primaryKey not set)
);

