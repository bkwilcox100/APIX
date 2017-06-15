use middle_layer;

CREATE TABLE IF NOT EXISTS heb_app_properties (
     appid varchar(64) not null,
    description varchar(1024),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (appid)
);

CREATE TABLE IF NOT EXISTS heb_app_version (
     app_version_id varchar(40) not null,
     appid varchar(64) not null,
    os_name varchar(64),
    os_version  varchar(64),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (app_version_id),
    index appid (appid),
    foreign key (appid) references heb_app_properties(appid)
);

CREATE TABLE IF NOT EXISTS heb_api_collection (
     collection_id varchar(40) not null,
    name varchar(128),
    description varchar(2048),
    contact_info varchar(2048),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (collection_id)
);

CREATE TABLE IF NOT EXISTS heb_api_service_description (
     service_id varchar(40) not null,
    collection_id varchar(40) not null,
     name varchar(128) not null,
    description varchar(2048),
    labels varchar(2048),
    open_api_spec_url varchar(2083),
    documentation_url varchar(2083),
    current_version varchar(10),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (service_id),
    index name (name),
    foreign key (collection_id) references heb_api_collection(collection_id)
);

CREATE TABLE IF NOT EXISTS heb_api_service_version (
     version_id varchar(40) not null,
    service_id varchar(40) not null,
    version_number varchar(10),
    description varchar(2048),
    host_name varchar(1024),
    base_path varchar(256),
    open_api_spec_url varchar(2083),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (version_id),
    foreign key (service_id) references heb_api_service_description(service_id)
);

CREATE TABLE IF NOT EXISTS heb_api_resource_path (
     path_id varchar(40) not null,
    version_id varchar(40) not null,
    name varchar(128),
    description varchar(2048),
    path varchar(1024),
    batch_path varchar(1024),
     creation_date datetime default current_timestamp,
     last_modified_date datetime default current_timestamp on update current_timestamp,
     primary key (path_id),
    foreign key (version_id) references heb_api_service_version(version_id)
);
