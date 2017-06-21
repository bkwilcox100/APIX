// Add any necessary packages.


public class AppPropertiesInterface {

	private static final String CONTEXT_FILTER = "AdminPortal";
	private static final String DEFAULT_PARENT_PROPERTY = "parent";

	private static final String DATA_ITEM_NAME_APP_PROPERTIES = "appProperties";
	private static final String APP_PROPERTIES_APP_ID = "appId";
	private static final String APP_PROPERTIES_DESCRIPTION = "description";
	private static final String APP_PROPERTIES_ORDER_NUMBER = "orderNumber";
	private static final String APP_PROPERTIES_APP_VERSIONS = "appVersions";
	private static final String APP_PROPERTIES_CREATION_DATE = "creationDate";
	private static final String APP_PROPERTIES_LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String APP_PROPERTIES_COLLECTION_QUERY = "all_app_properties";

	private static final String DATA_ITEM_NAME_APP_VERSION = "appVersion";
	private static final String APP_PROPERTIES_APP_VERSION_ID = "appVersionId";
	private static final String APP_PROPERTIES_OS_NAME = "osName";
	private static final String APP_PROPERTIES_OS_VERSION = "osVersion";
	private static final String APP_PROPERTIES_CREATION_DATE = "creationDate";
	private static final String APP_PROPERTIES_LAST_MODIFIED_DATE = "lastModifiedDate";


	private static final String OPERATION_CREATE = "create";
	private static final String OPERATION_UPDATE = "update";
	private static final String OPERATION_DELETE = "delete";
	//private static final String OPERATION_READ = "read";

	// Create Operations

	public Map<String, Object> createBatchAppProperties(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APP_PROPERTIES, null, null, requestBody, APP_PROPERTIES_APP_ID);
		return response;
	}

	public Map<String, Object> createBatchAppVersion(JsonElement requestBody, String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APP_PROPERTIES, appId, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, null);
		return response;
	}


	// Read Operations

	public List<Map<String, Object>> readAppPropertiesCollection() throws ServiceException {
		return ResourceUtils.readCollectionFromQuery(APP_PROPERTIES_COLLECTION_QUERY, DATA_ITEM_NAME_APP_PROPERTIES, CONTEXT_FILTER);
	}

	public Map<String, Object> readAppPropertiesResource(String appId) throws ServiceException {
		return ResourceUtils.readResource(appId, DATA_ITEM_NAME_APP_PROPERTIES, CONTEXT_FILTER);
	}

	public List<Map<String, Object>> readAppVersionCollection() throws ServiceException {
		return ResourceUtils.readSubCollection(appId, DATA_ITEM_NAME_APP_VERSION, CONTEXT_FILTER);
	}

	public Map<String, Object> readAppVersionResource(String appId, String appVersionId) throws ServiceException {
		return ResourceUtils.readResource(appId, DATA_ITEM_NAME_APP_VERSION, CONTEXT_FILTER);
	}


	// Update Operations

	public Map<String, Object> updateAppPropertiesResource(JsonElement requestBody, String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(appId, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, CONTEXT_FILTER);
		return response;
	}

	public Map<String, Object> updateAppVersionResource(JsonElement requestBody, String appId, String appVersionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(appVersionId, DATA_ITEM_NAME_APP_VERSION, appId, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		return response;
	}


	// Delete Operations

	public Map<String, Object> deleteBatchAppPropertiesResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_APP_PROPERTIES);
		return response;
	}

	public Map<String, Object> deleteAppPropertiesResource(String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(appId, DATA_ITEM_NAME_APP_PROPERTIES);
return response;
	}

	public Map<String, Object> deleteBatchAppVersionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_APP_VERSION);
		return response;
	}

	public Map<String, Object> deleteAppVersionResource(String appId, String appVersionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(appVersionId, DATA_ITEM_NAME_APP_VERSION, appId, DEFAULT_PARENT_PROPERTY);
		return response;
	}

}