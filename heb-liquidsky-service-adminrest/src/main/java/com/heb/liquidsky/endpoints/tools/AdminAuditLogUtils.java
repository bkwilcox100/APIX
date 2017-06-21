package com.heb.liquidsky.endpoints.tools;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Utilities for accessing the Admin Audit Log data
 * 
 * @author Scott McArthur
 *
 */
public class AdminAuditLogUtils{
	private static final Logger logger = Logger.getLogger(AdminAuditLogUtils.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(AdminAuditLogUtils.class);

	private static final String DATA_ITEM_NAME_AUDIT_LOG = "liquidSkyAdminAuditLog";
	public static final String ID_FOR_BATCH_CHANGE = "BATCH";

	public static void addLogEntry(String itemType, String itemId, String userId, String operation, Map<String, Object> response) {
		JsonElement jsonResponse = EndpointUtils.mapToJson(response);
		addLogEntry(itemType, itemId, userId, operation, jsonResponse);
	}

	/**
	 * Adds an audit log entry
	 * 
	 * @param itemType - DataItem type of the resource that was manipulated
	 * @param itemId - id of the resource that was manipulated
	 * @param userId - user id of user that manipulated the resource
	 * @param jsonResponse - optional (can be null) json response that was generated from the manipulation request
	 * @param operation - the operation that was performed (create, update, delete)
	 */
	public static void addLogEntry(String itemType, String itemId, String userId, String operation, JsonElement jsonResponse){
		HebTraceContext context = TRACER.startSpan("addLogEntry");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest("Admin Audit Log: Request for new entry itemType: " + itemType + " itemId: " + itemId + " userId: " + userId);
			try{
				MutableDataItem mutableDataItem = null;
				mutableDataItem = EndpointUtils.newMutableDataItem(DATA_ITEM_NAME_AUDIT_LOG);
				mutableDataItem.setProperty("dataItemType", itemType);
				mutableDataItem.setProperty("itemId", itemId);
				mutableDataItem.setProperty("userId", userId);
				mutableDataItem.setProperty("operation", operation);
				if (jsonResponse != null) mutableDataItem.setProperty("jsonResponse", jsonResponse.toString());
				DataStore.getInstance().insertItem(mutableDataItem);
				if (logger.isLoggable(Level.FINEST)) logger.finest("Admin Audit Log: New entry added with id: " + mutableDataItem.getId());
			} catch (ServiceException | DataStoreException e) {
				logger.log(Level.SEVERE, "Exception while writing to the Admin Audit Log: Request for new entry itemType: " + itemType + " itemId: " + itemId + " userId: " + userId, e);
			}
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	// TODO: These will be implemented as part of the Web Interface development 
	
	/**
	 * Gets a list of all audit log entries for a specific DataItem type
	 * @param itemType - specifies the DataItem type to get logs for
	 * @param limit - specifies to limit number of items returned.  A null value here indicates no limit. 
	 *  
	 * @return JsonElement representing a JsonArray of log entries or a JsonObject representing an error message
	 */
	public static JsonElement getLogListForType(String itemType, Integer limit){
		JsonArray logList = new JsonArray(); 
		return logList;
	}
	
	/**
	 * Gets a list of all audit log entries for a specific DataItem type and id combination
	 * 
	 * @param itemType - specifies the DataItem type to use in the query
	 * @param itemId - specifies the item id to use in the query
	 * @param limit - specifies to limit number of items returned.  A null value here indicates no limit.
	 *  
	 * @return JsonElement representing a JsonArray of log entries or a JsonObject representing an error message
	 */
	public static JsonElement getLogListForItem(String itemType, String itemId, Integer limit){
		JsonArray logList = new JsonArray(); 
		return logList;
	}
	
	/**
	 * Gets a list of all audit log entries for a specific user
	 * 
	 * @param userId - specifies the user id to use in the query
	 * @param limit - specifies to limit number of items returned.  A null value here indicates no limit.
	 * 
	 * @return JsonElement representing a JsonArray of log entries or a JsonObject representing an error message
	 */
	public static JsonElement getLogListForUser(String userId, Integer limit){
		JsonArray logList = new JsonArray(); 
		return logList;
	}
}