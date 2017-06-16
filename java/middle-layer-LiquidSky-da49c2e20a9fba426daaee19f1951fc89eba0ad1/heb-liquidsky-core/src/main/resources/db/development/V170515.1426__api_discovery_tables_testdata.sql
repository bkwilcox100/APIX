use middle_layer;

insert into middle_layer.heb_api_collection values('acol_test1', 'eCommerce APIs', 'API''s to support HEB eCommerce applications', 'walther.patrick@heb.com', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_collection values('acol_test2', 'Dropship APIs', 'API''s to for DSR - Drop Ship Resellers', 'walther.patrick@heb.com', now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_service_description values('asrv_test1', 'acol_test1', 
	'Product Service', 'HEB Product Catalog Service', 
	'ecommerce, product, sku, upc',
    'https://api.heb.com/product/openapi.json', 
    'https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice', '1',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_description values('asrv_test2', 'acol_test1', 
	'Price and Assortment Service', 
	'HEB Pricing and store assortment service', 
	'ecommerce, product, price, assortment', 
    'https://api.heb.com/assortment/openapi.json', 
    'https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice', '1',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_description values('asrv_test3', 'acol_test1', 
	'Shopping List Service', 
	'HEB Shopping List Service', 
	'ecommerce, customer, account, shoppinglist', 
    'https://api.heb.com/shoppinglist/openapi.json', 
    'https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice', '2',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
    
insert into middle_layer.heb_api_service_description values('asrv_test4', 'acol_test2', 
	'Product Service', 'HEB Drop Ship Product Catalog Service', 
	'ecommerce, product, sku, upc', 
    'https://dsr-api.heb.com/dsrproduct/openapi.json', 
    'https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice', '1',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_description values('asrv_test5', 'acol_test2', 
	'Warehouse Price Service', 'HEB Product Pricing for the Drop Ship Warehouse', 
	'ecommerce, product, price, assortment', 
    'https://dsr-api.heb.com/dsrassortment/openapi.json', 
    'https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice', '1',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_service_version values('aver_test1', 'asrv_test1', 
	'1', 'V1 Product Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/product/v1/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_version values('aver_test2', 'asrv_test2', 
	'1', 'V1 Assortment Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/assortment/v1/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_version values('aver_test3', 'asrv_test3', 
	'1', 'V1 Shopping List Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/shoppinglist/v1/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_version values('aver_test4', 'asrv_test3', 
	'2', 'V2 Shopping List Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/shoppinglist/v2/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_service_version values('aver_test5', 'asrv_test4', 
	'1', 'V1 DSR Product Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/dsrproduct/v1/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_service_version values('aver_test6', 'asrv_test5', 
	'1', 'V1 DSR Assortment Contract', 
    'api.heb.com', 
    '/_ah/api', 
    'https://api.heb.com/dsrassortment/v1/openapi.json',
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
    
insert into middle_layer.heb_api_resource_path values('ares_test1', 'aver_test1', 
	'Product Info', 'Endpoint for all product operations.  Supports GET, PUT, and DELETE with a product ID.  POST at the batch path to add a new product.', 
    '/product/{productId}', 
    '/product', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test2', 'aver_test1', 
	'Sku Info', 'Endpoint for all Sku operations.  Supports GET, PUT, and DELETE with a sku ID.  POST at the batch path to add a new sku.', 
    '/sku/{skuId}', 
    '/sku', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test3', 'aver_test1', 
	'Child Sku', 'Endpoint for information about skus attached to a product.  GET returns a child sku information, DELETE at the batch path removes the child. POST adds a sku as a child,', 
    '/product/{productId}/childsku/{skuId}', 
    '/product/{productId}/childsku/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_resource_path values('ares_test4', 'aver_test2', 
	'Product Assortment in Store', 'Endpoint product assortment', 
    '/assortment/{storeid}/{productId}', 
    '/assortment/{storeid}', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_resource_path values('ares_test5', 'aver_test3', 
	'Customer Shopping List', 'Endpoint for shopping list', 
    '/shoppinglist/{userId}/{listId}', 
    '/shoppinglist/{userId}', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test6', 'aver_test3', 
	'Customer Shopping List', 'Endpoint for shopping list products', 
    '/shoppinglist/{userId}/{listId}/products/{productId}', 
    '/shoppinglist/{userId}/{listId}/products/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_resource_path values('ares_test7', 'aver_test4', 
	'V2 Customer Shopping List', 'Endpoint for shopping list', 
    '/shoppinglist/{listId}', 
    '/shoppinglist/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test8', 'aver_test4', 
	'V2 Customer Shopping List', 'Endpoint for shopping list products', 
    '/shoppinglist/{listId}/products/{productId}', 
    '/shoppinglist/{listId}/products/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test9', 'aver_test4', 
	'V2 Customer Shopping List', 'Endpoint for shopping list coupons', 
    '/shoppinglist/{listId}/coupons/{couponId}', 
    '/shoppinglist/{listId}/coupons/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
    

insert into middle_layer.heb_api_resource_path values('ares_test10', 'aver_test5', 
	'Product Info', 'Endpoint for all DSR Products.  Supports GET only', 
    '/product/{productId}', 
    '/product', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test11', 'aver_test5', 
	'Sku Info', 'Endpoint for all DSR Skus.  Supports GET only', 
    '/sku/{skuId}', 
    '/sku', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
insert into middle_layer.heb_api_resource_path values('ares_test12', 'aver_test5', 
	'Child Sku', 'Endpoint for information about skus attached to a product.  GET support only.  GET at the batch level returns all child skus', 
    '/product/{productId}/childsku/{skuId}', 
    '/product/{productId}/childsku/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();

insert into middle_layer.heb_api_resource_path values('ares_test13', 'aver_test6', 
	'Product Assortment in Warehouse', 'Endpoint product assortment. Supports GET only', 
    '/assortment/{productId}', 
    '/assortment/', 
    now(), now()) ON DUPLICATE KEY UPDATE last_modified_date=now();
    
    