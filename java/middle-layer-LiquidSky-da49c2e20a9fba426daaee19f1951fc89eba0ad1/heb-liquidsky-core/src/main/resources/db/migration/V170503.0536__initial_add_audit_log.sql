use middle_layer;

CREATE TABLE IF NOT EXISTS ls_admin_audit_log (
	id varchar(64) not null,
	data_item_type varchar(256) not null,
    data_item_id varchar(256) not null,
    user_id varchar(256) not null,
    operation varchar(24),
	json_response mediumtext,
	creation_date datetime default current_timestamp,
	last_modified_date datetime default current_timestamp on update current_timestamp,
    index data_item (data_item_type, data_item_id),
    index user_id (user_id),
	primary key (id)
);

insert into middle_layer.lsdl_translation values('liquidSkyAdminAuditLog', 'Audit log entries for changes made in the Admin Portal', 
'{\"dataItemName\":\"liquidSkyAdminAuditLog\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"changeId\",\"propertyName\":\"changeId\"},{\"jsonKey\":\"dataItemType\",\"propertyName\":\"dataItemType\"},{\"jsonKey\":\"itemId\",\"propertyName\":\"itemId\"},{\"jsonKey\":\"userId\",\"propertyName\":\"userId\"},{\"jsonKey\":\"jsonResponse\",\"propertyName\":\"jsonResponse\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"}]}],\"creationRequiredProperties\":[\"changeId\",\"dataItemType\",\"itemId\",\"userId\"],\"restrictedProperties\":[\"changeId\",\"dataItemType\",\"itemId\",\"userId\",\"jsonResponse\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();