package com.heb.liquidsky.endpoints;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.AdminAuditLogUtils;
import com.heb.liquidsky.endpoints.tools.ResourceUtils;

/**
 * Defines v1 of the App Properties services for the Admin Rest service
 * AppProperties define various attributes of an app. 
 * This is mainly used to replace what has been known as the App Version Service or Forced upgrade service 
 * by using the AppVersion data contained within the AppProperties object.
 * 
 * @author Scott McArthur
 *
 */
public class AdminRestAppPropertiesInterface {

	private static final String CONTEXT_FILTER = "AdminPortal";
	private static final String DEFAULT_PARENT_PROPERTY = "parent";
	
	private static final String DATA_ITEM_NAME_APP_PROPERTIES = "appProperties";
	private static final String APP_PROPERTIES_APP_VERSION_LIST_PROPERTY = "appVersions";
	private static final String APP_PROPERTIES_COLLECTION_QUERY = "all_app_properties";
	private static final String APP_PROPERTIES_ID_NAME = "appId";
	
	private static final String DATA_ITEM_NAME_APP_VERSION = "appVersion";

	private static final String OPERATION_CREATE = "create";
	private static final String OPERATION_UPDATE = "update";
	private static final String OPERATION_DELETE = "delete";
	//private static final String OPERATION_READ = "read";
	
	private static final String FAKE_USER_ID = "lol_1337H4X0R";
	
	
	/*
	 * =============================================================================
	 * Create Operations
	 * =============================================================================
	 */
	
	public Map<String, Object> createBatchAppProperties(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APP_PROPERTIES, null, null, requestBody, APP_PROPERTIES_ID_NAME);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_PROPERTIES, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}
	
	public Map<String, Object> createBatchAppVersion(JsonElement requestBody, String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.createResources(DATA_ITEM_NAME_APP_VERSION, appId, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, null);
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_VERSION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_CREATE, response);
		return response;
	}
	
	/*
	 * =============================================================================
	 * Read Operations
	 * (No audit logging for read operations)
	 * =============================================================================
	 */
	
	public List<Map<String, Object>> readAppPropertiesCollection() throws ServiceException {
		return ResourceUtils.readCollectionFromQuery(APP_PROPERTIES_COLLECTION_QUERY, DATA_ITEM_NAME_APP_PROPERTIES, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readAppPropertiesResource(String appId) throws ServiceException {
		return ResourceUtils.readResource(appId, DATA_ITEM_NAME_APP_PROPERTIES, CONTEXT_FILTER);
	}
	
	public List<Map<String, Object>> readAppVersionCollection(String appId) throws ServiceException {
		return ResourceUtils.readSubCollection(appId, DATA_ITEM_NAME_APP_PROPERTIES, APP_PROPERTIES_APP_VERSION_LIST_PROPERTY, CONTEXT_FILTER);
	}
	
	public Map<String, Object> readAppVersionResource(String appId, String appVersionId) throws ServiceException {
		return ResourceUtils.readResource(appVersionId, DATA_ITEM_NAME_APP_VERSION, appId, DEFAULT_PARENT_PROPERTY, CONTEXT_FILTER);
	}
	
	
	/*
	 * =============================================================================
	 * Update Operations
	 * =============================================================================
	 */
	
	public Map<String, Object> updateAppPropertiesResource(JsonElement requestBody, String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(appId, DATA_ITEM_NAME_APP_PROPERTIES, requestBody, CONTEXT_FILTER); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_PROPERTIES, appId, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	public Map<String, Object> updateAppVersionResource(JsonElement requestBody, String appId, String appVersionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.updateResource(appVersionId, DATA_ITEM_NAME_APP_VERSION, appId, DEFAULT_PARENT_PROPERTY, requestBody, CONTEXT_FILTER); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_VERSION, appVersionId, FAKE_USER_ID, OPERATION_UPDATE, response);
		return response;
	}
	
	
	/*
	 * =============================================================================
	 * Delete Operations
	 * =============================================================================
	 */
	
	public Map<String, Object> deleteBatchAppPropertiesResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_APP_PROPERTIES); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_PROPERTIES, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteBatchAppVersionResource(JsonElement requestBody) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResourceList(requestBody, DATA_ITEM_NAME_APP_VERSION); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_VERSION, AdminAuditLogUtils.ID_FOR_BATCH_CHANGE, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteAppPropertiesResource(String appId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(appId, DATA_ITEM_NAME_APP_PROPERTIES); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_PROPERTIES, appId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
	
	public Map<String, Object> deleteAppVersionResource(String appId, String appVersionId) throws ServiceException {
		Map<String, Object> response = ResourceUtils.deleteResource(appVersionId, DATA_ITEM_NAME_APP_VERSION, appId, DEFAULT_PARENT_PROPERTY); 
		AdminAuditLogUtils.addLogEntry(DATA_ITEM_NAME_APP_VERSION, appVersionId, FAKE_USER_ID, OPERATION_DELETE, response);
		return response;
	}
}