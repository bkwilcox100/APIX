/*
 * 
 * Fixes some typos in the initial creation of some context filters
 * 
 */
use middle_layer;

delete from middle_layer.ls_context_filters where data_type_name='apiCollection';
delete from middle_layer.ls_context_filters where data_type_name='serviceDescription';

insert into middle_layer.ls_context_filters values('apiCollection', 'apiCollection is part of Api Discovery', 
'{\"dataItemName\":\"apiCollection\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"collectionId\",\"propertyName\":\"id\"},{\"jsonKey\":\"name\",\"propertyName\":\"name\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"contactInfo\",\"propertyName\":\"contactInfo\"},{\"jsonKey\":\"serviceDescriptions\",\"propertyName\":\"serviceDescriptions\"}]},{\"name\":\"AdminPortal\",\"filterMap\":[{\"jsonKey\":\"collectionId\",\"propertyName\":\"id\"},{\"jsonKey\":\"name\",\"propertyName\":\"name\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"contactInfo\",\"propertyName\":\"contactInfo\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"},{\"jsonKey\":\"serviceDescriptions\",\"propertyName\":\"serviceDescriptions\"}]}],\"creationRequiredProperties\":[\"name\"],\"restrictedProperties\":[\"collectionId\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.ls_context_filters values('serviceDescription', 'serviceDescription is part of Api Discovery', 
'{\"dataItemName\":\"serviceDescription\",\"filters\":[{\"name\":\"default\",\"filterMap\":[{\"jsonKey\":\"serviceDescriptionId\",\"propertyName\":\"id\"},{\"jsonKey\":\"name\",\"propertyName\":\"name\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"labels\",\"propertyName\":\"labels\"},{\"jsonKey\":\"openApiSpecUrl\",\"propertyName\":\"openApiSpecUrl\"},{\"jsonKey\":\"documentationUrl\",\"propertyName\":\"documentationUrl\"},{\"jsonKey\":\"currentVersion\",\"propertyName\":\"currentVersion\"},{\"jsonKey\":\"serviceVersions\",\"propertyName\":\"serviceVersions\"}]},{\"name\":\"AdminPortal\",\"filterMap\":[{\"jsonKey\":\"serviceDescriptionId\",\"propertyName\":\"id\"},{\"jsonKey\":\"name\",\"propertyName\":\"name\"},{\"jsonKey\":\"description\",\"propertyName\":\"description\"},{\"jsonKey\":\"labels\",\"propertyName\":\"labels\"},{\"jsonKey\":\"openApiSpecUrl\",\"propertyName\":\"openApiSpecUrl\"},{\"jsonKey\":\"documentationUrl\",\"propertyName\":\"documentationUrl\"},{\"jsonKey\":\"currentVersion\",\"propertyName\":\"currentVersion\"},{\"jsonKey\":\"creationDate\",\"propertyName\":\"creationDate\"},{\"jsonKey\":\"lastModifiedDate\",\"propertyName\":\"lastModifiedDate\"},{\"jsonKey\":\"serviceVersions\",\"propertyName\":\"serviceVersions\"}]}],\"creationRequiredProperties\":[\"name\"],\"restrictedProperties\":[\"serviceDescriptionId\",\"creationDate\",\"lastModifiedDate\"]}', 
now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();