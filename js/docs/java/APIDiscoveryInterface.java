// Add any necessary packages.


public class APIDiscoveryInterface {

	private static final String CONTEXT_FILTER = "AdminPortal";
	private static final String DEFAULT_PARENT_PROPERTY = "parent";

	private static final String DATA_ITEM_NAME_RESOURCE_PATH = "resourcePath";
	private static final String APP_PROPERTIES_BATCH_PATH = "batchPath";
	private static final String APP_PROPERTIES_DESCRIPTION = "description";
	private static final String APP_PROPERTIES_ID = "id";
	private static final String APP_PROPERTIES_NAME = "name";
	private static final String APP_PROPERTIES_PATH = "path";

	private static final String DATA_ITEM_NAME_SERVICE_DESCRIPTION = "serviceDescription";
	private static final String APP_PROPERTIES_CURRENT_VERSION = "currentVersion";
	private static final String APP_PROPERTIES_DESCRIPTION = "description";
	private static final String APP_PROPERTIES_DOCUMENTATION = "documentation";
	private static final String APP_PROPERTIES_ID = "id";
	private static final String APP_PROPERTIES_LABELS = "labels";
	private static final String APP_PROPERTIES_NAME = "name";
	private static final String APP_PROPERTIES_OPEN_API_SPEC_URL = "openApiSpecUrl";
	private static final String APP_PROPERTIES_SERVICE_VERSIONS = "serviceVersions";

	private static final String DATA_ITEM_NAME_SERVICE_VERSION = "serviceVersion";
	private static final String APP_PROPERTIES_BASE_PATH = "basePath";
	private static final String APP_PROPERTIES_DESCRIPTION = "description";
	private static final String APP_PROPERTIES_HOST_NAME = "hostName";
	private static final String APP_PROPERTIES_ID = "id";
	private static final String APP_PROPERTIES_OPEN_API_SPEC_URL = "openApiSpecUrl";
	private static final String APP_PROPERTIES_RESOURCE_PATHS = "resourcePaths";
	private static final String APP_PROPERTIES_VERSION_NUMBER = "versionNumber";

	private static final String DATA_ITEM_NAME_API_COLLECTION = "apiCollection";
	private static final String APP_PROPERTIES_CONTACT_INFO = "contactInfo";
	private static final String APP_PROPERTIES_DESCRIPTION = "description";
	private static final String APP_PROPERTIES_ID = "id";
	private static final String APP_PROPERTIES_NAME = "name";
	private static final String APP_PROPERTIES_SERVICE_DESCRIPTIONS = "serviceDescriptions";
	private static final String APP_PROPERTIES_COLLECTION_QUERY = "all_api_collection";


	private static final String OPERATION_CREATE = "create";
	private static final String OPERATION_UPDATE = "update";
	private static final String OPERATION_DELETE = "delete";
	//private static final String OPERATION_READ = "read";

	// Create Operations

	public Map<String, Object> createBatchResourcePath(JsonElement requestBody, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APIDISCOVERY, id, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, null);
		return response;
	}

	public Map<String, Object> createBatchServiceDescription(JsonElement requestBody, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APIDISCOVERY, id, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, null);
		return response;
	}

	public Map<String, Object> createBatchServiceVersion(JsonElement requestBody, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APIDISCOVERY, id, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, null);
		return response;
	}

	public Map<String, Object> createBatchApiCollection(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APIDISCOVERY, null, null, requestBody, APP_PROPERTIES_ID);
		return response;
	}


	// Read Operations

	public List<Map<String, Object>> readResourcePathCollection() throws ServiceException {
		return ResourceUtils.readSubCollection(id, DATA_ITEM_NAME_RESOURCE_PATH, CONTEXT_FILTER);
	}

	public Map<String, Object> readResourcePathResource(String id, String id) throws ServiceException {
		return ResourceUtils.readResource(id, DATA_ITEM_NAME_RESOURCE_PATH, CONTEXT_FILTER);
	}

	public List<Map<String, Object>> readServiceDescriptionCollection() throws ServiceException {
		return ResourceUtils.readSubCollection(id, DATA_ITEM_NAME_SERVICE_DESCRIPTION, CONTEXT_FILTER);
	}

	public Map<String, Object> readServiceDescriptionResource(String id, String id) throws ServiceException {
		return ResourceUtils.readResource(id, DATA_ITEM_NAME_SERVICE_DESCRIPTION, CONTEXT_FILTER);
	}

	public List<Map<String, Object>> readServiceVersionCollection() throws ServiceException {
		return ResourceUtils.readSubCollection(id, DATA_ITEM_NAME_SERVICE_VERSION, CONTEXT_FILTER);
	}

	public Map<String, Object> readServiceVersionResource(String id, String id) throws ServiceException {
		return ResourceUtils.readResource(id, DATA_ITEM_NAME_SERVICE_VERSION, CONTEXT_FILTER);
	}

	public List<Map<String, Object>> readApiCollectionCollection() throws ServiceException {
		return ResourceUtils.readCollectionFromQuery(APP_PROPERTIES_COLLECTION_QUERY, DATA_ITEM_NAME_API_COLLECTION, CONTEXT_FILTER);
	}

	public Map<String, Object> readApiCollectionResource(String id) throws ServiceException {
		return ResourceUtils.readResource(id, DATA_ITEM_NAME_API_COLLECTION, CONTEXT_FILTER);
	}


	// Update Operations

	public Map<String, Object> updateResourcePathResource(JsonElement requestBody, String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(id, DATA_ITEM_NAME_RESOURCE_PATH, id, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		return response;
	}

	public Map<String, Object> updateServiceDescriptionResource(JsonElement requestBody, String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(id, DATA_ITEM_NAME_SERVICE_DESCRIPTION, id, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		return response;
	}

	public Map<String, Object> updateServiceVersionResource(JsonElement requestBody, String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(id, DATA_ITEM_NAME_SERVICE_VERSION, id, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		return response;
	}

	public Map<String, Object> updateApiCollectionResource(JsonElement requestBody, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(id, DATA_ITEM_NAME_API_COLLECTION, requestBody, CONTEXT_FILTER);
		return response;
	}


	// Delete Operations

	public Map<String, Object> deleteBatchResourcePathResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_RESOURCE_PATH);
		return response;
	}

	public Map<String, Object> deleteResourcePathResource(String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(id, DATA_ITEM_NAME_RESOURCE_PATH, id, DEFAULT_PARENT_PROPERTY);
		return response;
	}

	public Map<String, Object> deleteBatchServiceDescriptionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_SERVICE_DESCRIPTION);
		return response;
	}

	public Map<String, Object> deleteServiceDescriptionResource(String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(id, DATA_ITEM_NAME_SERVICE_DESCRIPTION, id, DEFAULT_PARENT_PROPERTY);
		return response;
	}

	public Map<String, Object> deleteBatchServiceVersionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_SERVICE_VERSION);
		return response;
	}

	public Map<String, Object> deleteServiceVersionResource(String id, String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(id, DATA_ITEM_NAME_SERVICE_VERSION, id, DEFAULT_PARENT_PROPERTY);
		return response;
	}

	public Map<String, Object> deleteBatchApiCollectionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_API_COLLECTION);
		return response;
	}

	public Map<String, Object> deleteApiCollectionResource(String id) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(id, DATA_ITEM_NAME_API_COLLECTION);
return response;
	}

}