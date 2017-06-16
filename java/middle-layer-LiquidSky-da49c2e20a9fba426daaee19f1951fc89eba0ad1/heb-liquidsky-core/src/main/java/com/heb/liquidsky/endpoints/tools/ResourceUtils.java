package com.heb.liquidsky.endpoints.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataItemPropertyDescriptor;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.response.ServiceExceptionErrorItem;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Abstraction utility methods to support CRUD operations on Api Resources and Collections
 * 
 * @author Scott McArthur
 *
 */
public final class ResourceUtils {
	private static final Logger logger = Logger.getLogger(ResourceUtils.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ResourceUtils.class);

	private static final String ATTRIBUTE_NAME_REQUIRED = "requiredProperty";
	private static final String ATTRIBUTE_NAME_RESTRICTED = "restrictedProperty";
	private static final String ATTRIBUTE_NAME_MIN_LENGTH = "minLength";
	private static final String ATTRIBUTE_NAME_MAX_LENGTH = "maxLength";

	private ResourceUtils() {
		// singleton instance
	}

	/*
	 * =================================================================================================
	 * CRUD Operations
	 * =================================================================================================
	 */
	
	/**
	 * Creates many child resources based on the contents of an array of Json Objects.
	 * 
	 * @param dataTypeName - String containing the type of resources to be created
	 * @param resourceArray - Json Array of resource data for new resources
	 * 
	 * @return BatchResponse Json Object
	 * @throws ServiceException
	 */
	public static Map<String, Object> createResources(String dataTypeName, JsonElement resourceArray) throws ServiceException {
		return createResources(dataTypeName, null, null, resourceArray, null);
	}
	
	/**
	 * Creates many child resources with NON auto generated ID's based on the contents of an array of Json Objects.
	 * 
	 * @param dataTypeName - String containing the type of resources to be created
	 * @param resourceArray - Json Array of resource data for new resources
	 * @param idKey - String holding the property name that holds a non auto generated Id for this resource.  
	 * 					If the Id property is not found in the resource data, an error is added to the response.
	 * 
	 * @return BatchResponse Json Object
	 * @throws ServiceException
	 */
	public static Map<String, Object> createResources(String dataTypeName, JsonElement resourceArray, String idKey) throws ServiceException {
		return createResources(dataTypeName, null, null, resourceArray, idKey);
	}
	
	/**
	 * Creates many child resources as part of a sub collection of a 
	 * parent resource based on the contents of an array of Json Objects.
	 * 
	 * @param dataTypeName - String containing the type of resources to be created
	 * @param parentId - String containing the id of the parent that the new resources will belong to.
	 * @param parentDataTypeName - String containing the data type name of the parent resource.
	 * @param resourceArray - Json Array of resource data for new resources
	 * 
	 * @return BatchResponse Json Object
	 * @throws ServiceException
	 */
	public static Map<String, Object> createResources(String dataTypeName, String parentId, String parentDataTypeName, JsonElement resourceArray) throws ServiceException {
		return createResources(dataTypeName, parentId, parentDataTypeName, resourceArray, null);
	}
	
	/**
	 * Creates many child resources with NON auto generated ID's as part of a 
	 * sub collection of a parent resource based on the contents of an array of Json Objects.
	 * 
	 * @param dataTypeName - String containing the type of resources to be created
	 * @param parentId - String containing the id of the parent that the new resources will belong to.
	 * @param parentDataTypeName - String containing the data type name of the parent resource.
	 * @param resourceArray - Json Array of resource data for new resources
	 * @param idKey - String holding the property name that holds a non auto generated Id for this resource.  
	 * 					If the Id property is not found in the resource data, an error is added to the response.
	 * 
	 * @return BatchResponse Json Object
	 * @throws ServiceException
	 */
	public static Map<String, Object> createResources(String dataTypeName, String parentId, String parentDataTypeName, JsonElement resourceArray, String idKey) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("createResources");
		try {
			if (resourceArray == null || resourceArray.isJsonNull() || !resourceArray.isJsonArray()){
				throw new BadRequestException("Invalid Data");
			}
			List<String> successes = new ArrayList<>();
			List<ServiceExceptionErrorItem> errors = new ArrayList<>();
			for (JsonElement currentItem : resourceArray.getAsJsonArray()) {
				if (!currentItem.isJsonObject()){
					throw new BadRequestException("Invalid Data");
				}
				try{
					String newId = null;
					if (idKey != null){
						if (!currentItem.getAsJsonObject().has(idKey) || StringUtils.isBlank(currentItem.getAsJsonObject().get(idKey).getAsString())){
							throw new BadRequestException("Invalid Id supplied");
						}
						newId = currentItem.getAsJsonObject().get(idKey).getAsString();
					}
					MutableDataItem mutableDataItem = null;
					//validateCreateResourceRequest(currentItem, dataTypeName, newId);
					mutableDataItem = EndpointUtils.newMutableDataItem(newId, dataTypeName);
					if (parentId != null){
						populateMutableDataItem(mutableDataItem, currentItem.getAsJsonObject(), parentId, parentDataTypeName);
					} else {
						populateMutableDataItem(mutableDataItem, currentItem.getAsJsonObject());
					}
					try {
						DataStore.getInstance().insertItem(mutableDataItem);
						successes.add(mutableDataItem.getId());
					} catch (DataStoreException e) {
						throw new InternalServerErrorException("DataStore Exception while inserting", e);
					}
				} catch (ServiceException se) {
					// since this is a batch method, we do not want to throw this one.
					errors.add(new ServiceExceptionErrorItem(se, EndpointUtils.jsonToMap(currentItem)));
				}
			}
			return EndpointUtils.batchResponse(successes, errors);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Reads a sub collection of items from a given parent.
	 * 
	 * @param id - id of the parent containing the child collection
	 * @param dataTypeName - name of the PARENT data type
	 * @param childCollectionPropertyName - name of the property in the parent item that contains the child collection
	 * 
	 * @return JsonArray containing filtered Json resources in the collection
	 * @throws ServiceException 
	 */
	public static List<Map<String, Object>> readSubCollection(String id, String dataTypeName, String childCollectionPropertyName) throws ServiceException {
		return readSubCollection(id, dataTypeName, childCollectionPropertyName, null);
	}
	/**
	 * Reads a sub collection of items from a given parent.
	 * 
	 * @param id - id of the parent containing the child collection
	 * @param dataTypeName - name of the PARENT data type
	 * @param childCollectionPropertyName - name of the property in the parent item that contains the child collection
	 * @param contextFilter - String indicating which context filter to use.  Null will result in the default context filter being used.
	 * 
	 * @return JsonArray containing filtered Json resources in the collection
	 * @throws ServiceException 
	 */
	public static List<Map<String, Object>> readSubCollection(String id, String dataTypeName, String childCollectionPropertyName, String contextFilter) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("readSubCollection");
		try {
			DataItem dataItem = EndpointUtils.getDataItem(id, dataTypeName);
			
			if (dataItem == null){
				throw new NotFoundException("Could not find item of type " + dataTypeName + " with ID " + id);
			}
			
			List<DataItem> dataItemList = null;
			try {
				dataItemList = dataItem.getList(childCollectionPropertyName);
				if (dataItemList == null) {
					throw new InternalServerErrorException("No Children");
				}
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("DataStore Exception while reading child item list", e);
			}
			List<Map<String, Object>> subCollection = new ArrayList<>();
			for (DataItem currentItem : dataItemList) {
				subCollection.add(TranslationUtils.filterDataItemToMap(currentItem, TranslationUtils.getFilterJsonArray(currentItem.getDataType().getName(), contextFilter)));
			}
			return subCollection;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Reads a collection from a named query
	 * 
	 * @param queryName - String holding the name of the named query
	 * @param dataTypeName - String holding the name of the DataType that the named query belongs to
	 * 
	 * @return JsonArray containing filtered Json resources in the collection
	 * @throws ServiceException 
	 */
	public static List<Map<String, Object>> readCollectionFromQuery(String queryName, String dataTypeName) throws ServiceException {
		return readCollectionFromQuery(queryName, dataTypeName, null);
	}
	
	/**
	 * Reads a collection from a named query
	 * 
	 * @param queryName - String holding the name of the named query
	 * @param dataTypeName - String holding the name of the DataType that the named query belongs to
	 * @param contextFilter - String indicating which context filter to use.  Null will result in the default context filter being used.
	 *  
	 * @return JsonArray containing filtered Json resources in the collection
	 * @throws ServiceException 
	 */
	public static List<Map<String, Object>> readCollectionFromQuery(String queryName, String dataTypeName, String contextFilter, Object... args) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("readCollectionFromQuery");
		try {
			List<DataItem> dataItemList = null;
			
			try {
				dataItemList = DataStore.getInstance().executeNamedQuery(dataTypeName, queryName, args);
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("DataStore Exception.", e);
			}
			List<Map<String, Object>> resourceCollection = new ArrayList<>();
			if (dataItemList != null) {
				for (DataItem currentItem : dataItemList) {
					resourceCollection.add(TranslationUtils.filterDataItemToMap(currentItem, TranslationUtils.getFilterJsonArray(dataTypeName, contextFilter), contextFilter));
				}
			}
			return resourceCollection;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Reads a resource with no parent validation
	 * Data is returned using the default context filter
	 * 
	 * @param id - id of the resource to read
	 * @param dataTypeName - datatype name of the resource
	 * 
	 * @return Json Object representing the requested resource or an errorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> readResource(String id, String dataTypeName) throws ServiceException {
		return readResource(id, dataTypeName, null, null);
	}
	/**
	 * Reads a resource with no parent validation
	 * Data is returned using the specified context filter
	 * 
	 * @param id - id of the resource to read
	 * @param dataTypeName - datatype name of the resource
	 * @param contextFilter - String indicating which context filter to use.  Null will result in the default context filter being used.
	 * 
	 * @return Json Object representing the requested resource or an errorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> readResource(String id, String dataTypeName, String contextFilter) throws ServiceException {
		return readResource(id, dataTypeName, null, null, contextFilter);
	}
	
	/**
	 * Reads a single resource
	 * This handles parent validation if this is a child resource.
	 * Data is returned using the default context filter
	 * 
	 * @param id - id of the resource to read
	 * @param dataTypeName - datatype name of the resource
	 * @param parentId - id of the parent item that was passed in the request from the client.
	 * @param parentPropertyName - the name of the property that contains the back link to the parent
	 * 
	 * @return Json Object representing the requested resource or an errorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> readResource(String id, String dataTypeName, String parentId, String parentPropertyName) throws ServiceException {
		return readResource(id, dataTypeName, parentId, parentPropertyName, null);
	}
	/**
	 * Reads a single resource
	 * This handles parent validation if this is a child resource.
	 * Data is returned using the specified context filter
	 * 
	 * @param id - id of the resource to read
	 * @param dataTypeName - datatype name of the resource
	 * @param parentId - id of the parent item that was passed in the request from the client.
	 * @param parentPropertyName - the name of the property that contains the back link to the parent
	 * @param contextFilter - String indicating which context filter to use.  Null will result in the default context filter being used.
	 * 
	 * @return Json Object representing the requested resource or an errorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> readResource(String id, String dataTypeName, String parentId, String parentPropertyName, String contextFilter) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("readResource");
		try {
			if (parentId != null) {
				validateParent(parentId, id, dataTypeName, parentPropertyName);
			}
			DataItem dataItem = EndpointUtils.getDataItem(id, dataTypeName);
			if (dataItem == null) {
				throw new NotFoundException("Could not find item of type " + dataTypeName + " with ID " + id);
			}
			return TranslationUtils.filterDataItemToMap(dataItem, TranslationUtils.getFilterJsonArray(dataTypeName, contextFilter), contextFilter);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Updates a resource with no parent validation
	 * Returns data formatted using the default context filter
	 * 
	 * @param id - id of the resource to update
	 * @param dataTypeName - data type of the resource to update
	 * @param requestBody - body payload of the request from the client
	 * 
	 * @return Json Object representing the updated resource or a batchErrorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> updateResource(String id, String dataTypeName, JsonElement requestBody) throws ServiceException {
		return updateResource(id, dataTypeName, null, null, requestBody);
	}
	/**
	 * Updates a resource with no parent validation
	 * Returns data formatted using the specified context filter.
	 * 
	 * @param id - id of the resource to update
	 * @param dataTypeName - data type of the resource to update
	 * @param requestBody - body payload of the request from the client
	 * 
	 * @return Json Object representing the updated resource or a batchErrorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> updateResource(String id, String dataTypeName, JsonElement requestBody, String contextFilter) throws ServiceException {
		return updateResource(id, dataTypeName, null, null, requestBody, contextFilter);
	}
	/**
	 * Updates a resource.  This handles parent validation if this is a child resource.
	 * Returns data formatted using the default context filter
	 * 
	 * @param id - id of the resource to update
	 * @param dataTypeName - data type of the resource to update
	 * @param parentId - id of the parent item that was passed in the request from the client.
	 * @param parentPropertyName - the name of the property that contains the back link to the parent 
	 * @param requestBody - body payload of the request from the client
	 * 
	 * @return Json Object representing the updated resource or a batchErrorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> updateResource(String id, String dataTypeName, String parentId, String parentPropertyName, JsonElement requestBody) throws ServiceException {
		return updateResource(id, dataTypeName, parentId, parentPropertyName, requestBody, null);
	}
	/**
	 * Updates a resource.  This handles parent validation if this is a child resource.
	 * Returns data formatted using the specified context filter.
	 * 
	 * @param id - id of the resource to update
	 * @param dataTypeName - data type of the resource to update
	 * @param parentId - id of the parent item that was passed in the request from the client.
	 * @param parentPropertyName - the name of the property that contains the back link to the parent 
	 * @param requestBody - body payload of the request from the client
	 * @param contextFilter - String indicating which context filter to use.  Null will result in the default context filter being used.
	 * 
	 * @return Json Object representing the updated resource or a batchErrorResponse
	 * @throws ServiceException 
	 */
	public static Map<String, Object> updateResource(String id, String dataTypeName, String parentId, String parentPropertyName, JsonElement requestBody, String contextFilter) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("updateResource");
		try {
			if (!EndpointUtils.resourceExists(id, dataTypeName)){
				NotFoundException nfe = new NotFoundException("Resource with id " + id + " not found.");
				nfe.addError(new ServiceExceptionErrorItem(null, EndpointUtils.jsonToMap(requestBody.getAsJsonObject())));
				throw nfe;
			}
			
			if (parentId != null) {
				validateParent(parentId, id, dataTypeName, parentPropertyName);
			}
			MutableDataItem mutableDataItem = EndpointUtils.getMutableDataItem(id, dataTypeName);
			populateMutableDataItem(mutableDataItem, requestBody.getAsJsonObject());
			try {
				DataStore.getInstance().updateItem(mutableDataItem);
				return TranslationUtils.filterDataItemToMap(mutableDataItem, TranslationUtils.getFilterJsonArray(dataTypeName, contextFilter), contextFilter);
			} catch (DataStoreException e) {
				InternalServerErrorException ise = new InternalServerErrorException("DataStore Exception while updating resource " + id + " of type " + dataTypeName, e);
				ise.addError(new ServiceExceptionErrorItem(null, EndpointUtils.jsonToMap(requestBody.getAsJsonObject())));
				throw ise;
			}
		} finally {
			TRACER.endSpan(context);
		}
	}

	
	
	/**
	 * Deletes a single resource. No parent validation
	 * 
	 * @param id - id of the resource to be deleted
	 * @param dataTypeName - datatype name of the resource
	 * 
	 * @return JsonObject containing responseError indicating success or failure
	 * @throws ServiceException 
	 */
	public static Map<String, Object> deleteResource(String id, String dataTypeName) throws ServiceException {
		return deleteResource(id, dataTypeName, null, null);
	}
	
	/**
	 * Deletes a single resource. This handles parent validation if this is a child resource.
	 * 
	 * @param id - id of the resource to be deleted
	 * @param dataTypeName - datatype name of the resource
	 * @param parentId - id of the parent item that was passed in the request from the client.
	 * @param parentPropertyName - the name of the property that contains the back link to the parent
	 * 
	 * @return JsonObject containing responseError indicating success or failure
	 * @throws ServiceException 
	 */
	public static Map<String, Object> deleteResource(String id, String dataTypeName, String parentId, String parentPropertyName) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("deleteResource");
		try {
			if (parentId != null){ 
				validateParent(parentId, id, dataTypeName, parentPropertyName);
			} else {
				if (!EndpointUtils.resourceExists(id, dataTypeName)){
					throw new NotFoundException("No item of type " + dataTypeName + " with ID " + id + " exists");
				}
			}
			try {
				MutableDataItem mutableDataItem = DataStore.getInstance().readItemForUpdate(id, dataTypeName);
				if (mutableDataItem != null) {
					DataStore.getInstance().deleteItem(mutableDataItem);
				} else {
					InternalServerErrorException ise = new InternalServerErrorException("Could not read DataStore");
					Map<String, Object> objectMap = new HashMap<>();
					objectMap.put("id", id);
					ise.addError(new ServiceExceptionErrorItem(null, objectMap));
					throw ise;
				}
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("DataStore Exception", e);
			}
			Map<String, Object> successValue = new HashMap<>();
			successValue.put("id", id);
			return successValue;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Deletes many resources
	 *  
	 * @param requestBody - JsonElement representing an array containing id strings to be deleted.
	 * @param dataTypeName - datatype name of the resources to be deleted
	 * @return
	 * @throws ServiceException 
	 */
	public static Map<String, Object> deleteResourceList(JsonElement requestBody, String dataTypeName) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("deleteResourceList");
		try {
			if (requestBody == null || requestBody.isJsonNull() || !requestBody.isJsonArray()){
				throw new BadRequestException("Invalid Request");
			}
			return deleteResourceList(requestBody.getAsJsonArray(), dataTypeName);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Abstract method for deleting resources from a JsonArray of id's
	 * 
	 * @param ids - JsonArray containing only strings of id's to be deleted.
	 * @param dataTypeName - datatype name of the resources to be deleted
	 * @return
	 * @throws ServiceException 
	 */
	public static Map<String, Object> deleteResourceList(JsonArray ids, String dataTypeName) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("deleteResourceList");
		try {
			List<String> successes = new ArrayList<>();
			List<ServiceExceptionErrorItem> errors = new ArrayList<>();
			for (JsonElement idElement : ids.getAsJsonArray()) {
				String id = null;
				if (idElement.isJsonPrimitive() && idElement.getAsJsonPrimitive().isString()){
					id = idElement.getAsString();
				} else {
					throw new BadRequestException("Invalid Request");
				}
				try {
					deleteResource(id, dataTypeName);
					successes.add(id);
				} catch (ServiceException e) {
					// catch this one because we want to add it to the error list.
					Map<String, Object> objectMap = new HashMap<>();
					if (id != null) {
						objectMap.put("id", id);
					}
					errors.add(new ServiceExceptionErrorItem(e, objectMap));
				}
			}
			return EndpointUtils.batchResponse(successes, errors);
		} finally {
			TRACER.endSpan(context);
		}
	}

	/*
	 * =================================================================================================
	 * Validation Methods
	 * =================================================================================================
	 */
	
	/**
	 * Validates that a child item belongs to the specified parent item from the child's point of view
	 * 
	 * @param parentId - id of the parent resource
	 * @param childId - id of the child resource
	 * @param dataTypeName - DataType name of the child resource
	 * @param parentPropertyName - name of the property in the child that contains the back link to the parent
	 * 
	 * @throws ServiceException
	 */
	public static void validateParent(String parentId, String childId, String dataTypeName, String parentPropertyName) throws ServiceException {
		if (StringUtils.isBlank(parentId) || StringUtils.isBlank(childId) || StringUtils.isBlank(dataTypeName)){
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(CloudUtil.getMethodName() + ": called with invalid parameters");
			}
			throw new InternalServerErrorException("Invalid Parameters");
		}
		// Validate that the given childId exists and belongs to the given parentId
		// This does not use getResource because we want to explicitly check for a DataStore exception here.
		DataItem dataItem;
		try {
			dataItem = DataStore.getInstance().readItemImmutable(childId, dataTypeName);
			if (dataItem == null){
				throw new NotFoundException("Item of type " + dataTypeName + " with ID " + childId + " not found");
			}
			if (!dataItem.getItem(parentPropertyName).getId().equals(parentId)){
				throw new BadRequestException("Resource does not belong to the specified parent resource.");
			}
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception", e);
		}
	}
	
	/*
	 * 
	 * DataItem Manipulation
	 * 
	 */

	/**
	 * Populates the passed in MutableDataItem with appropriate values found in the currentItem JsonObject and adds the item as a child to the specified parentId
	 * 
	 * @param mutableDataItem - data item to populate
	 * @param currentItem - JsonObject containing the data to populate in the mutableDataItem
	 * @param parentId - parent Id of the mutableDataItem that was passed in the request (for validation)
	 * @param parentDataTypeName - String representing the name of the DataType of the parent
	 * 
	 * @throws ServiceException
	 */
	public static void populateMutableDataItem(MutableDataItem mutableDataItem, JsonObject currentItem, String parentId, String parentDataTypeName) throws ServiceException {
		// validate that the parent exists
		if (!EndpointUtils.resourceExists(parentId, parentDataTypeName)){
			throw new NotFoundException("Parent " + parentDataTypeName + " item with id " + parentId + " does not exist. " + parentId);
		}
		// create the child
		populateMutableDataItem(mutableDataItem, currentItem);
		
		// attach the child to the parent
		for (DataItemPropertyDescriptor propertyDescriptor : mutableDataItem.dataItemDescriptor().getPropertyDescriptors()){
			if (parentDataTypeName.equals(propertyDescriptor.getItemType())) {
				mutableDataItem.setProperty(propertyDescriptor.getPropertyName(), EndpointUtils.getDataItem(parentId, parentDataTypeName));
				break;
			}
		}
	}
	
	/**
	 * Populates the passed in MutableDataItem with appropriate values found in the newResourceData JsonElement
	 * 
	 * @param mutableDataItem - The DataItem item to update
	 * @param newResourceData - JsonObject containing the data to write to the mutableDataItem
	 * @throws ServiceException
	 */
	public static void populateMutableDataItem(MutableDataItem mutableDataItem, JsonObject newResourceData) throws ServiceException{
		for (DataItemPropertyDescriptor propertyDescriptor : mutableDataItem.dataItemDescriptor().getPropertyDescriptors()){
			String propertyName = propertyDescriptor.getPropertyName();
			
			if (!mutableDataItem.getDataType().getPropertyByName(propertyName).isReadOnly()){
				int minLength = NumberUtils.toInt(mutableDataItem.getDataType().getPropertyByName(propertyName).getAttributeByName(ATTRIBUTE_NAME_MIN_LENGTH));
				int maxLength = NumberUtils.toInt(mutableDataItem.getDataType().getPropertyByName(propertyName).getAttributeByName(ATTRIBUTE_NAME_MAX_LENGTH));
				boolean requiredProperty = Boolean.parseBoolean(mutableDataItem.getDataType().getPropertyByName(propertyName).getAttributeByName(ATTRIBUTE_NAME_REQUIRED));
				boolean restrictedProperty = Boolean.parseBoolean(mutableDataItem.getDataType().getPropertyByName(propertyName).getAttributeByName(ATTRIBUTE_NAME_RESTRICTED));
				
				try {
					// Check if the property is required for creation and not populated and a value was passed for it
					if ((requiredProperty && mutableDataItem.getObject(propertyName) == null) && !newResourceData.has(propertyName)){
						throw new BadRequestException("Required property: " + propertyName + " is missing.");
					}
					
					//  see if the property is even there.
					if (!newResourceData.has(propertyName)){
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(CloudUtil.getMethodName() + ": skipping " + propertyName + " because it is not in in the payload.");
						}
						continue;
					}
					
					// see if this property is even in the new data
					if (!requiredProperty && !newResourceData.has(propertyName)){
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(CloudUtil.getMethodName() + ": ignoring " + propertyName + " because it is in in the payload.");
						}
						continue;
					}
					
					// make sure this is not a list or a sub object
					if (!newResourceData.get(propertyName).isJsonPrimitive()){
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(CloudUtil.getMethodName() + ": ignoring " + propertyName + " because it is not a primitive value.");
						}
						continue;
					}
					
					// check if the property has a value and is restricted for update
					if (restrictedProperty && mutableDataItem.getObject(propertyName) != null){
						// ignore a restricted property update.
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(CloudUtil.getMethodName() + ": ignoring " + propertyName + " because it is restricted and already populated.");
						}
						continue;
					}
					
					if (newResourceData.get(propertyName).isJsonNull() && minLength > 0){
						// ignore null values if something is expected.
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(CloudUtil.getMethodName() + ": ignoring " + propertyName + " because it is null and a value is expected.");
						}
						continue;
					}
					// get the new value because any operations left need it.
					String newValue = newResourceData.get(propertyName).getAsString().trim();
					
					// ensure that the property meets the minimum length
					if (newValue.length() < minLength){
						throw new BadRequestException("Trimmed property " + propertyName + " does not meet the required minimum length (" + minLength + ")");
					}
					
					// trim the string if it is too long.
					if (maxLength > 0 && newValue.length() > maxLength) {
						newValue = newValue.substring(0, maxLength);
					}
					
					// Seems all good.  set the value.
					mutableDataItem.setProperty(propertyName, newValue);
					
				} catch (DataStoreException e) {
					throw new InternalServerErrorException("DataStore Exception", e);
				}
				
			}
			
		}
	}
}
