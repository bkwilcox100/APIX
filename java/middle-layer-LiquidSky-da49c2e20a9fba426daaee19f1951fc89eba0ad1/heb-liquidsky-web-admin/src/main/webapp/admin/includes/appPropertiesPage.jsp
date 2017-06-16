<script>

	var adminPageProperties = {
			forceDebugData: false,
			topLevelResourceType: "appProperties",
			appProperties: {
				resourceType: "appProperties",
				collectionUrl: "/adminrest/v1/appproperties",
				resourceUrl: "/adminrest/v1/appproperties/{{appId}}",
				idProperty: "appId",
				propertyOrder: ["appId", "description", "creationDate", "lastModifiedDate"],
				childCollections: ["appVersions"],
				updateProperties: ["description"],
				createProperties: ["appId", "description"]
			},
			appVersions: {
				resourceType: "appVersion",
				collectionUrl: "/adminrest/v1/appproperties/{{appId}}/appVersion",
				resourceUrl: "/adminrest/v1/appproperties/{{appId}}/appVersion/{{appVersionId}}",
				idProperty: "appVersionId",
				propertyOrder: ["appVersionId", "osName", "osVersion", "creationDate", "lastModifiedDate"],
				childCollections: [],
				updateProperties: ["osName", "osVersion"],
				createProperties: ["osName", "osVersion"]
			}
	}
	
	var testdata = JSON.parse("[{\"appId\":\"default\",\"description\":\"Default HEB App\",\"creationDate\":\"2017-05-30 18:24:52.0\",\"lastModifiedDate\":\"2017-05-30 18:24:52.0\",\"appVersions\":[{\"appVersionId\":\"av1\",\"osName\":\"Android\",\"osVersion\":\"2.0\",\"creationDate\":\"2017-05-30 18:25:00.0\",\"lastModifiedDate\":\"2017-05-30 18:25:00.0\"},{\"appVersionId\":\"av2\",\"osName\":\"iOS\",\"osVersion\":\"2.0\",\"creationDate\":\"2017-05-30 18:25:00.0\",\"lastModifiedDate\":\"2017-05-30 18:25:00.0\"}]},{\"appId\":\"curbside\",\"description\":\"HEB to you curbside app\",\"creationDate\":\"2017-05-30 18:25:06.0\",\"lastModifiedDate\":\"2017-05-30 18:25:06.0\",\"appVersions\":[{\"appVersionId\":\"av3\",\"osName\":\"Android\",\"osVersion\":\"3.0\",\"creationDate\":\"2017-05-30 18:25:00.0\",\"lastModifiedDate\":\"2017-05-30 18:25:00.0\"},{\"appVersionId\":\"av4\",\"osName\":\"iOS\",\"osVersion\":\"1.0\",\"creationDate\":\"2017-05-30 18:25:00.0\",\"lastModifiedDate\":\"2017-05-30 18:25:00.0\"}]}]");

</script>

<%@ include file="resourceCollectionView.jsp" %>