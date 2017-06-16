<script>

	var adminPageProperties = {
			forceDebugData: false,
			topLevelResourceType: "apiCollection",
			apiCollection: {
				resourceType: "apiCollection",
				collectionUrl: "/adminrest/v1/apicollection",
				resourceUrl: "/adminrest/v1/apicollection/{{collectionId}}",
				idProperty: "collectionId",
				propertyOrder: ["collectionId", "name", "description", "contactInfo", "creationDate", "lastModifiedDate"],
				childCollections: ["serviceDescriptions"],
				updateProperties: ["contactInfo", "description", "name"],
				createProperties: ["contactInfo", "description", "name"]
			},
			serviceDescriptions: {
				resourceType: "serviceDescription",
				collectionUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions",
				resourceUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions/{{serviceDescriptionId}}",
				idProperty: "serviceDescriptionId",
				propertyOrder: ["serviceDescriptionId", "name", "description", "labels", "openApiSpecUrl", "documentationUrl", "currentVersion", "creationDate", "lastModifiedDate"],
				childCollections: ["serviceVersions"],
				updateProperties: ["name", "description", "labels", "openApiSpecUrl", "documentationUrl", "currentVersion"],
				createProperties: ["name", "description", "labels", "openApiSpecUrl", "documentationUrl", "currentVersion"]
			},
			serviceVersions: {
				resourceType: "serviceVersion",
				collectionUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions/{{serviceDescriptionId}}/serviceVersions",
				resourceUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions/{{serviceDescriptionId}}/serviceVersions/{{serviceVersionId}}",
				idProperty: "serviceVersionId",
				propertyOrder: ["serviceVersionId", "versionNumber", "description", "hostName", "basePath", "openApiSpecUrl", "creationDate", "lastModifiedDate"],
				childCollections: ["resourcePaths"],
				updateProperties: ["versionNumber", "description", "hostName", "basePath", "openApiSpecUrl"],
				createProperties: ["versionNumber", "description", "hostName", "basePath", "openApiSpecUrl"]
			},
			resourcePaths: {
				resourceType: "resourcePath",
				collectionUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions/{{serviceDescriptionId}}/serviceVersions/{{serviceVersionId}}/resourcePaths",
				resourceUrl: "/adminrest/v1/apicollection/{{collectionId}}/serviceDescriptions/{{serviceDescriptionId}}/serviceVersions/{{serviceVersionId}}/resourcePaths/{{resourcePathId}}",
				idProperty: "resourcePathId",
				propertyOrder: ["resourcePathId", "name", "description", "path", "batchPath", "creationDate", "lastModifiedDate"],
				childCollections: [],
				updateProperties: ["name", "description", "path", "batchPath"],
				createProperties: ["name", "description", "path", "batchPath"]
			}
	}
	
	var testdata = JSON.parse("[{\"collectionId\":\"acol_test1\",\"name\":\"eCommerce APIs\",\"description\":\"API\'s to support HEB eCommerce applications\",\"contactInfo\":\"walther.patrick@heb.com\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceDescriptions\":[{\"serviceDescriptionId\":\"asrv_test1\",\"name\":\"Product Service\",\"description\":\"HEB Product Catalog Service\",\"labels\":\"ecommerce, product, sku, upc\",\"openApiSpecUrl\":\"https://api.heb.com/product/openapi.json\",\"documentationUrl\":\"https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice\",\"currentVersion\":\"1\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceVersions\":[{\"serviceVersionId\":\"aver_test1\",\"versionNumber\":\"1\",\"description\":\"V1 Product Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/product/v1/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[{\"resourcePathId\":\"ares1032\",\"name\":\"Passing Test ResourcePath\",\"description\":\"This will work as expected.\",\"path\":\"/whatever/{testId}\",\"batchPath\":\"/whatever\",\"creationDate\":\"2017-05-30 15:32:51.0\",\"lastModifiedDate\":\"2017-05-30 15:32:51.0\"},{\"resourcePathId\":\"ares1033\",\"name\":\"Passing Minimum Info Test ResourcePath\",\"path\":\"/whatever/{testId}\",\"creationDate\":\"2017-05-30 15:32:51.0\",\"lastModifiedDate\":\"2017-05-30 15:32:51.0\"}]}]},{\"serviceDescriptionId\":\"asrv_test2\",\"name\":\"Price and Assortment Service\",\"description\":\"HEB Pricing and store assortment service\",\"labels\":\"ecommerce, product, price, assortment\",\"openApiSpecUrl\":\"https://api.heb.com/assortment/openapi.json\",\"documentationUrl\":\"https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice\",\"currentVersion\":\"1\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceVersions\":[{\"serviceVersionId\":\"aver_test2\",\"versionNumber\":\"1\",\"description\":\"V1 Assortment Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/assortment/v1/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[]}]},{\"serviceDescriptionId\":\"asrv_test3\",\"name\":\"Shopping List Service\",\"description\":\"HEB Shopping List Service\",\"labels\":\"ecommerce, customer, account, shoppinglist\",\"openApiSpecUrl\":\"https://api.heb.com/shoppinglist/openapi.json\",\"documentationUrl\":\"https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice\",\"currentVersion\":\"2\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceVersions\":[{\"serviceVersionId\":\"aver_test3\",\"versionNumber\":\"1\",\"description\":\"V1 Shopping List Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/shoppinglist/v1/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[]},{\"serviceVersionId\":\"aver_test4\",\"versionNumber\":\"2\",\"description\":\"V2 Shopping List Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/shoppinglist/v2/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[]}]}]},{\"collectionId\":\"acol_test2\",\"name\":\"Dropship APIs\",\"description\":\"API\'s to for DSR - Drop Ship Resellers\",\"contactInfo\":\"walther.patrick@heb.com\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceDescriptions\":[{\"serviceDescriptionId\":\"asrv_test4\",\"name\":\"Product Service\",\"description\":\"HEB Drop Ship Product Catalog Service\",\"labels\":\"ecommerce, product, sku, upc\",\"openApiSpecUrl\":\"https://dsr-api.heb.com/dsrproduct/openapi.json\",\"documentationUrl\":\"https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice\",\"currentVersion\":\"1\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceVersions\":[{\"serviceVersionId\":\"aver_test5\",\"versionNumber\":\"1\",\"description\":\"V1 DSR Product Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/dsrproduct/v1/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[]}]},{\"serviceDescriptionId\":\"asrv_test5\",\"name\":\"Warehouse Price Service\",\"description\":\"HEB Product Pricing for the Drop Ship Warehouse\",\"labels\":\"ecommerce, product, price, assortment\",\"openApiSpecUrl\":\"https://dsr-api.heb.com/dsrassortment/openapi.json\",\"documentationUrl\":\"https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice\",\"currentVersion\":\"1\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"serviceVersions\":[{\"serviceVersionId\":\"aver_test6\",\"versionNumber\":\"1\",\"description\":\"V1 DSR Assortment Contract\",\"hostName\":\"api.heb.com\",\"basePath\":\"/_ah/api\",\"openApiSpecUrl\":\"https://api.heb.com/dsrassortment/v1/openapi.json\",\"creationDate\":\"2017-05-23 15:41:27.0\",\"lastModifiedDate\":\"2017-05-30 09:26:02.0\",\"resourcePaths\":[]}]}]}]")

</script>

<%@ include file="resourceCollectionView.jsp" %>