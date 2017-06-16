/*
 * 
 * Fixes some typos in the initial creation of some context filters
 * 
 */
use middle_layer;

delete from middle_layer.ls_context_filters where data_type_name='appProperties';

insert into middle_layer.ls_context_filters values('appProperties', 'AppProperties give information about a given app', 
'{\"dataItemName\":\"appProperties\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"appId\",\"propertyName\":\"appId\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"appVersions\",\"propertyName\":\"appVersions\"}]},{\"name\":\"AdminPortal\",\"filterMap\":[{\"jsonKey\":\"appId\",\"propertyName\":\"appId\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"},{\"jsonKey\":\"appVersions\",\"propertyName\":\"appVersions\"}]}],\"creationRequiredProperties\":[\"appId\"],\"restrictedProperties\":[\"appId\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
