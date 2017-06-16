package com.heb.liquidsky.endpoints.tools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.PartialSuccessException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.response.ServiceExceptionErrorItem;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Useful utility Methods
 * 
 * @author Scott McArthur
 *
 */
public final class EndpointUtils {
	private static final Logger logger = Logger.getLogger(EndpointUtils.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(EndpointUtils.class);

	private EndpointUtils() {
		// singleton instance
	}

	/* 
	 * =================================================================================================
	 * Data Item Operations
	 * =================================================================================================
	 */
	
	/**
	 * This should only be called after a validation call checks that the data item exists and inputs are valid
	 * 
	 * @param id - String containing the id of the DataItem to get
	 * @param type - String containing the name of the DataItemType of the desired DataItem
	 * 
	 * @return DataItem that was requested or null
	 */
	public static DataItem getDataItem(String id, String type) throws InternalServerErrorException {
		HebTraceContext context = TRACER.startSpan("getDataItem");
		try {
			return DataStore.getInstance().readItemImmutable(id, type);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception.", e);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Checks if a specific resource exists
	 * 
	 * @param id - String containing the id of the resource
	 * @param dataTypeName - String containing the name of the resources DataType
	 * 
	 * @return boolean - true if exists, false if not, or item cannot be read.
	 */
	public static boolean resourceExists(String id, String dataTypeName) throws InternalServerErrorException {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(dataTypeName)){
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(CloudUtil.getMethodName() + ": Invalid Parameters.");
			}
			return false;
		}
		DataItem dataItem = getDataItem(id, dataTypeName);
		return (dataItem != null);
	}
	
	/**
	 * Intended for DataItems with auto generated Id's.
	 * This creates a new MutableDataItem with the specified Id.  
	 * Throws a ServiceException if unable to do so.
	 * 
	 * @param dataTypeName - String containing the name of the resources DataType
	 * @return a new MutableDataItem, ready to be filled with data.
	 * @throws ServiceException containing a JSON messageObject on failure
	 */
	public static MutableDataItem newMutableDataItem(String dataTypeName) throws ServiceException {
		try {
			return DataStore.getInstance().createItem(dataTypeName);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception", e);
		}
	}
	
	/**
	 * Intended for DataItems that do not have auto generated Id's.
	 * This creates a new MutableDataItem with the specified Id.  
	 * Throws a ServiceException if unable to do so.
	 * 
	 * @param id - String containing the id of the DataItem to create.  Sending null will auto generate an id  
	 * @param dataTypeName - String containing the name of the resources DataType
	 * 
	 * @return MutableDataItem of type dataTypeName with id "id"
	 * @throws ServiceException containing a JSON messageObject on failure
	 */
	public static MutableDataItem newMutableDataItem(String id, String dataTypeName) throws ServiceException {
		try {
			return DataStore.getInstance().createItem(id, dataTypeName);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception", e);
		}
	}

	/**
	 * Gets an existing existing data item for update
	 * 
	 * @param id - String containing the id of the DataItem  
	 * @param dataTypeName - String containing the name of the resources DataType
	 * 
	 * @return MutableDataItem of type dataTypeName with id "id"
	 * @throws ServiceException
	 */
	public static MutableDataItem getMutableDataItem(String id, String dataTypeName) throws ServiceException {
		try {
			return DataStore.getInstance().readItemForUpdate(id, dataTypeName);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception", e);
		}
	}
	
	/*
	 * =================================================================================================
	 * Messaging Methods
	 * see https://confluence.heb.com:8443/display/ESELLING/Liquid+Sky+Microservice+Best+Practices#LiquidSkyMicroserviceBestPractices-BatchResponseObject
	 * =================================================================================================
	 */

	/**
	 * This creates a batchResponseObject suitable for return in a request
	 * 
	 * @param successList - JsonArray of Strings that contain id's of resources successfully operated on.
	 * @param errorList - JsonArray of responseError objects indicating problems that occurred with operations performed on one or more items.
	 * 
	 * @return JsonObject containing success, error and code.  The "code" element represents the http response that should be returned. 
	 * @throws ServiceException 
	 */
	public static Map<String, Object> batchResponse(List<String> successes, List<ServiceExceptionErrorItem> errors) throws ServiceException {
		if (errors != null && !errors.isEmpty()) {
			if (successes == null || successes.isEmpty()) {
				// no success, just failure
				BadRequestException bre = new BadRequestException("No items were successfully processed");
				bre.setErrors(errors);
				throw bre;
			} else {
				// partial success
				throw new PartialSuccessException("Some items were successfully processed", successes, errors);
			}
		}
		Map<String, Object> batchResponse = new HashMap<>();
		if (successes != null && !successes.isEmpty()) {
			batchResponse.put("success", successes);
		}
		batchResponse.put("code", HttpServletResponse.SC_OK);
		return batchResponse;
	}
	
	/**
	 * This creates a ResponseError object
	 * 
	 * @param String id - String containing the id (if known) of the resource accessed that caused the error
	 * @param String status - String containing http status code that should be returned or would have been returned if this is added to a batch response
	 * @param JsonObject failedObject - JsonObject from the request that caused the error (if known)
	 * @param JsonObjectmessageObject - JsonObject containing a responseMessage
	 * 
	 * @return JsonObject - responseError
	 */
	public static List<Map<String, Object>> responseError(String id, String status, Map<String, Object> failedObject, Map<String, Object> messageObject){
		// check if the message object is null and is a valid JsonObject
		if (messageObject == null) {
			messageObject = new HashMap<>();
		}
		if (id != null) {
			messageObject.put("id", id);
		}
		if (status != null) {
			messageObject.put("status", status);
		}
		if (failedObject != null) {
			messageObject.put("object", failedObject);
		}
		List<Map<String, Object>> failedItems = new ArrayList<>();
		failedItems.add(messageObject);
		return failedItems;
	}
	
	/**
	 * Creates a generic response message object
	 * TODO: This should be extended to use the message data items.
	 * 
	 * @param code - String containing the LiquidSky message code
	 * @param text - Text of the message
	 * 
	 * @return JsonObject representing a ResponseMessage
	 */
	public static Map<String, Object> responseMessage(String code, String text){
		Map<String, Object> errorMessage = new HashMap<>();
		Map<String, Object> responseMessage = new HashMap<>();
		errorMessage.put("code", code);
		errorMessage.put("text", text);
		responseMessage.put("message", errorMessage);
		return responseMessage;
	}
	
	/**
	 * This creates a proper success response array of a single item.  Useful for creating a success response array when there is only one item
	 * 
	 * @param id - String with the id that was successful.
	 * 
	 * @return JsonObject containing a success message with one id.
	 */
	public static JsonObject responseSuccessList(String id){
		JsonArray successArray = new JsonArray();
		JsonObject responseSuccessJson = new JsonObject();
		successArray.add(id);
		responseSuccessJson.add("success", successArray);
		return responseSuccessJson;
	}
	
	/**
	 * This adds an array of ids to a success response.
	 *  
	 * @param successArray
	 * 
	 * @return JsonObject containing a success message with an array of id's
	 */
	public static JsonObject responseSuccessList(JsonArray successArray){
		JsonObject responseSuccessJson = new JsonObject();
		responseSuccessJson.add("success", successArray);
		return responseSuccessJson;
	}

	public static JsonElement getRequestBodyAsJsonElement(String requestBody) throws BadRequestException {
		if (requestBody == null || StringUtils.isBlank(requestBody)) {
			throw new BadRequestException("Null or empty request body found when JSON was expected");
		}
		try {
			return new JsonParser().parse(requestBody);
		} catch (JsonSyntaxException | JsonIOException e) {
			throw new BadRequestException("Invalid Json in the request body: " + requestBody, e);
		}
	}

	public static Map<String, Object> jsonToMap(JsonElement json) {
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		return new Gson().fromJson(json, type);
	}

	public static JsonElement mapToJson(Map<String, Object> map) {
		return new Gson().toJsonTree(map);
	}
}
