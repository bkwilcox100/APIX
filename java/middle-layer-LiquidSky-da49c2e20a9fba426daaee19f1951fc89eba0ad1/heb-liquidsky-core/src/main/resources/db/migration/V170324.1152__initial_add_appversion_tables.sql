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

insert into middle_layer.heb_app_properties values('default', 'Default App Description', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_properties values('couponapp', 'HEB Gamified Digital Coupon App', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_properties values('hebuddygame', 'HEBuddy Phone Game for kids of all ages', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_app_version values('av_test1', 'default', 'iOs', '1.9', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_version values('av_test2', 'default', 'Android', '1.8', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_app_version values('av_test3', 'couponapp', 'iOs', '1.0', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_version values('av_test4', 'couponapp', 'Android', '1.1.1', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_version values('av_test5', 'couponapp', 'Windows 10 Mobile', '0.9', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_app_version values('av_test6', 'hebuddygame', 'iOs', '1.0', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_app_version values('av_test7', 'hebuddygame', 'Android', '1.0', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
