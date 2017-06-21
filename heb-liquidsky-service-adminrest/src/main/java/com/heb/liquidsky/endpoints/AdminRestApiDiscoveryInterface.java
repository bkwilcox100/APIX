package com.heb.liquidsky.endpoints;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.AdminAuditLogUtils;
import com.heb.liquidsky.endpoints.tools.ResourceUtils;

/**
 * Defines v1 of the Admin Rest Api Discovery interface
 * 
 * @author Scott McArthur
 *
 */
public class AdminRestApiDiscoveryInterface {
	
	private static final String CONTEXT_FILTER = "AdminPortal";
	private static final String DEFAULT_PARENT_PROPERTY = "parent";

	private static final String DATA_ITEM_NAME_API_COLLECTION = "apiCollection";
	private static final String DATA_ITEM_NAME_SERVICE_DESCRIPTION = "serviceDescription";
	private static final String DATA_ITEM_NAME_SERVICE_VERSION = "serviceVersion";
	private static final String DATA_ITEM_NAME_RESOURCE_PATH = "resourcePath";

	private static final String CHILD_LIST_SERVICE_DESCRIPTION = "serviceDescriptions";
	private static final String CHILD_LIST_SERVICE_VERSION = "serviceVersions";
	private static final String CHILD_LIST_RESOURCE_PATH = "resourcePaths";

	private static final String QUERY_API_COLLECTION = "getAllApiCollections";

	private static final String OPERATION_CREATE = "create";
	private static final String OPERATION_UPDATE = "update";
	private static final String OPERATION_DELETE = "delete";
	private static final String FAKE_USER_ID = "lol_1337H4X0R";


	/*
	 * =============================================================================
	 * Create Operations
	 * =============================================================================
	 */

	public Map<String, Object> createBatchApiCollection(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_API_COLLECTION, requestBody);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_API_COLLECTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}

	public Map<String, Object> createBatchServiceDescription(JsonElement requestBody, String parentId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_SERVICE_DESCRIPTION, parentId, DATA_ITEM_NAME_API_COLLECTION, requestBody);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_DESCRIPTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}

	public Map<String, Object> createBatchServiceVersion(JsonElement requestBody, String parentId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_SERVICE_VERSION, parentId, DATA_ITEM_NAME_SERVICE_DESCRIPTION, requestBody);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_VERSION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}

	public Map<String, Object> createBatchResourcePath(JsonElement requestBody, String parentId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_RESOURCE_PATH, parentId, DATA_ITEM_NAME_SERVICE_VERSION, requestBody);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_RESOURCE_PATH, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}


	/*
	 * =============================================================================
	 * Read Operations
	 * (No audit logging for read operations)
	 * =============================================================================
	 */
	
	public List<Map<String, Object>> readApiCollectionCollection() throws ServiceException {
		return ResourceUtils.readCollectionFromQuery(QUERY_API_COLLECTION, DATA_ITEM_NAME_API_COLLECTION, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readApiCollectionResource(String collectionId) throws ServiceException {
		return ResourceUtils.readResource(collectionId, DATA_ITEM_NAME_API_COLLECTION, CONTEXT_FILTER);
	}
	
	public List<Map<String, Object>> readServiceDescriptionCollection(String collectionId) throws ServiceException {
		return ResourceUtils.readSubCollection(collectionId, DATA_ITEM_NAME_API_COLLECTION, CHILD_LIST_SERVICE_DESCRIPTION, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readServiceDescriptionResource(String collectionId, String serviceDescriptionId) throws ServiceException {
		return ResourceUtils.readResource(serviceDescriptionId, DATA_ITEM_NAME_SERVICE_DESCRIPTION, collectionId, DEFAULT_PARENT_PROPERTY, CONTEXT_FILTER);
	}
	
	public List<Map<String, Object>> readServiceVersionCollection(String serviceDescriptionId) throws ServiceException {
		return ResourceUtils.readSubCollection(serviceDescriptionId, DATA_ITEM_NAME_SERVICE_DESCRIPTION, CHILD_LIST_SERVICE_VERSION, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readServiceVersionResource(String serviceDescriptionId, String serviceVersionId) throws ServiceException {
		return ResourceUtils.readResource(serviceVersionId, DATA_ITEM_NAME_SERVICE_VERSION, serviceDescriptionId, DEFAULT_PARENT_PROPERTY, CONTEXT_FILTER);
	}
	
	public List<Map<String, Object>> readResourcePathCollection(String serviceVersionId) throws ServiceException {
		return ResourceUtils.readSubCollection(serviceVersionId, DATA_ITEM_NAME_SERVICE_VERSION, CHILD_LIST_RESOURCE_PATH, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readResourcePathResource(String serviceVersionId, String resourcePathId) throws ServiceException {
		return ResourceUtils.readResource(resourcePathId, DATA_ITEM_NAME_RESOURCE_PATH, serviceVersionId, DEFAULT_PARENT_PROPERTY, CONTEXT_FILTER);
	}
	
	
	/*
	 * =============================================================================
	 * Update Operations
	 * =============================================================================
	 */
	
	public Map<String, Object> updateApiCollectionResource(JsonElement requestBody, String collectionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(collectionId, DATA_ITEM_NAME_API_COLLECTION, requestBody, CONTEXT_FILTER);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_API_COLLECTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	public Map<String, Object> updateServiceDescriptionResource(JsonElement requestBody, String collectionId, String serviceDescriptionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(serviceDescriptionId, DATA_ITEM_NAME_SERVICE_DESCRIPTION, collectionId, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_DESCRIPTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	public Map<String, Object> updateServiceVersionResource(JsonElement requestBody, String serviceDescriptionId, String serviceVersionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(serviceVersionId, DATA_ITEM_NAME_SERVICE_VERSION, serviceDescriptionId, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_VERSION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	public Map<String, Object> updateResourcePathResource(JsonElement requestBody, String serviceVersionId, String resourcePathId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(resourcePathId, DATA_ITEM_NAME_RESOURCE_PATH, serviceVersionId, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_RESOURCE_PATH, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	/*
	 * =============================================================================
	 * Delete Operations
	 * =============================================================================
	 */
	
	public Map<String, Object> deleteBatchApiCollectionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_API_COLLECTION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_API_COLLECTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteApiCollectionResource(String resourceId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(resourceId, DATA_ITEM_NAME_API_COLLECTION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_API_COLLECTION, resourceId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteBatchServiceDescriptionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_SERVICE_DESCRIPTION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_DESCRIPTION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}

	public Map<String, Object> deleteServiceDescriptionResource(String resourceId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(resourceId, DATA_ITEM_NAME_SERVICE_DESCRIPTION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_DESCRIPTION, resourceId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteBatchServiceVersionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_SERVICE_VERSION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_VERSION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteServiceVersionResource(String resourceId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(resourceId, DATA_ITEM_NAME_SERVICE_VERSION);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_SERVICE_VERSION, resourceId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}

	public Map<String, Object> deleteBatchResourcePathResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_RESOURCE_PATH);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_RESOURCE_PATH, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteResourcePathResource(String resourceId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(resourceId, DATA_ITEM_NAME_RESOURCE_PATH);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_RESOURCE_PATH, resourceId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
}