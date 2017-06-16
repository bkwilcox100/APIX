package com.heb.liquidsky.endpoints.tools;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataItemPropertyDescriptor;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Liquid Sky Translation Repository and Filter Operations
 * 
 * @author Scott McArthur
 *
 */
public final class TranslationUtils {
	private static final Logger logger = Logger.getLogger(TranslationUtils.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(TranslationUtils.class);

	private static final String DATA_ITEM_NAME_CONTEXT_FILTERS = "contextFilters";
	private static final String CONTEXT_FILTERS_DEFAULT = "default";
	private static final List<String> DEFAULT_RESTRICTED_PROPERTIES_LIST = Arrays.asList("id", "creationDate", "lastModifiedDate");
	private static final Map<String, JsonObject> DATA_ITEM_TEANSLATION_OBJECT_MAP = new HashMap<>();

	private TranslationUtils() {
		// singleton instance
	}

	/**
	 * This takes a DataItem and a filterMap and adds all items 
	 * from the DataItem that are found in the filterMap to the returned JSON object.
	 * This method uses the default context filter for child objects. 
	 * 
	 * @param dataItem - DataItem containing the resource data
	 * @param filterMap - Map containing the context filter to use when generating the JsonObject
	 * 
	 * @return A JSON object containing values from the DataItem found in the filter map
	 * @throws ServiceException
	 */
	public static Map<String, Object> filterDataItemToMap(DataItem dataItem, JsonArray filter) throws ServiceException {
		return filterDataItemToMap(dataItem, filter, CONTEXT_FILTERS_DEFAULT);
	}
	
	/**
	 * This takes a DataItem and a filterMap and adds all items 
	 * from the DataItem that are found in the filterMap to the returned JSON object.
	 * This method allows for specifying the context filter to use for child items.
	 * 
	 * @param dataItem - DataItem containing the resource data
	 * @param filterMap - Map containing the context filter to use when generating the JsonObject
	 * @param contextFilter - The context filter to use when mapping child objects.
	 * 
	 * @return A JSON object containing values from the DataItem found in the filter map
	 * @throws ServiceException
	 */
	public static Map<String, Object> filterDataItemToMap(DataItem dataItem, JsonArray filter, String contextFilter) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("filterDataItemToJson");
		try {
			if (dataItem == null){
				throw new NotFoundException("Not Found");
			}

			if (filter == null || filter.isJsonNull()) {
				filter = makeFilterJsonArrayFromDataItem(dataItem);
			}
			Map<String, Object> filteredReturnMap = new HashMap<>();

			for (JsonElement currentFilterEntry : filter){
				String key = currentFilterEntry.getAsJsonObject().get("jsonKey").getAsString();
	    		String value = currentFilterEntry.getAsJsonObject().get("propertyName").getAsString();

	    		try {
	    			if (!StringUtils.isBlank(value)){
		    			if (dataItem.getObject(value) instanceof String){
		    				filteredReturnMap.put(key, dataItem.getString(value));
	    				} else if (dataItem.getObject(value) instanceof Timestamp){
	    					filteredReturnMap.put(key, dataItem.getTimestamp(value).toString());
			    		} else if (dataItem.getObject(value) instanceof Long){
			    			filteredReturnMap.put(key, dataItem.getLong(value).toString());
			    		} else if (dataItem.getObject(value) instanceof Integer){
			    			filteredReturnMap.put(key, dataItem.getInt(value).toString());
			    		} else if (dataItem.getObject(value) != null && dataItem.getObject(value).getClass().isArray()){
			    			List<DataItem> childItemList = dataItem.getList(value);
			    			List<Map<String, Object>> childList = new ArrayList<>();
			    			for (DataItem currentChildItem : childItemList){
			    				childList.add(filterDataItemToMap(currentChildItem, getFilterJsonArray(currentChildItem.getDataType().getName(), contextFilter), contextFilter));
			    			}
			    			filteredReturnMap.put(key, childList);
			    		} else {
			    			// log a message but ignore the property
			    			if (logger.isLoggable(Level.FINEST)) {
			    				logger.finest(CloudUtil.getMethodName() + " data type for " + value + " was unhandled");
			    			}
			    		}
		    		}
	    		} catch (DataStoreException e) {
    				throw new InternalServerErrorException("DataStore Exception while reading properties for id: " + dataItem.getId(), e);
    			}
	    	}
			return filteredReturnMap;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Gets a list of keys that are required for creating a particular resource type
	 * 
	 * @param dataTypeName - The type of resource
	 * 
	 * @return List of strings containing required data when creating a resource 
	 * @throws ServiceException
	 */
	public static List<String> getCreationRequiredList(String dataTypeName) throws ServiceException {
		JsonObject translationObject = getTranslationObject(dataTypeName);
		ArrayList<String> creationRequiredList = new ArrayList<String>();
		
		for (JsonElement currentItem : translationObject.getAsJsonArray("creationRequiredProperties")){
			if (currentItem.isJsonPrimitive()){
				creationRequiredList.add(currentItem.getAsString());
			}
		}
		return creationRequiredList;
	}
	
	/**
	 * Gets a list of restricted properties that can not be updated once a resource has been created.
	 *  
	 * @param dataTypeName - The type of resource
	 * 
	 * @return List of strings containing restricted properties
	 * @throws ServiceException
	 */
	public static List<String> getRestrictedPropertiesList(String dataTypeName) throws ServiceException {
		JsonObject translationObject = getTranslationObject(dataTypeName);
		ArrayList<String> restrictedPropertiesList = new ArrayList<String>();
		
		for (JsonElement currentItem : translationObject.getAsJsonArray("restrictedProperties")){
			if (currentItem.isJsonPrimitive()){
				restrictedPropertiesList.add(currentItem.getAsString());
			}
		}
		if (restrictedPropertiesList.isEmpty()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info(CloudUtil.getMethodName() + " restrictedPropertiesList not found for DataType " + dataTypeName + " using the default.");
			}
			restrictedPropertiesList = new ArrayList<String>(DEFAULT_RESTRICTED_PROPERTIES_LIST);
		}
		return restrictedPropertiesList;
	}
	
	
	/**
	 * Gets the default filter map for a given resource type
	 * 
	 * @param dataTypeName - The type of resource
	 * 
	 * @return Map containing the default filter map
	 * @throws ServiceException
	 */
	public static JsonArray getFilterJsonArray(String dataTypeName) throws ServiceException {
		return getFilterJsonArray(dataTypeName, CONTEXT_FILTERS_DEFAULT);
	}
	
	/**
	 * Gets the specified filter map for the given resource type
	 * If the specified context filter map is not found, then an attempt is made to get and use the default
	 * 
	 * @param dataTypeName - The type of resource
	 * @param filterName - name of the filter to get
	 * 
	 * @return Map containing the filter map requested
	 * @throws ServiceException
	 */
	public static JsonArray getFilterJsonArray(String dataTypeName, String filterName) throws ServiceException {
		JsonObject translationObject = getTranslationObject(dataTypeName);
		
		if (translationObject == null){
			//  if we cannot find a context filter, then return null and let the calling method handle that.
			return null;
		}
		if (StringUtils.isBlank(filterName)) {
			filterName =  CONTEXT_FILTERS_DEFAULT;
		}
		for (JsonElement currentItem : translationObject.getAsJsonArray("filters")){
			if (filterName.equals(currentItem.getAsJsonObject().get("name").getAsString())){
				if (currentItem.getAsJsonObject().get("filterMap").isJsonArray()){
					return currentItem.getAsJsonObject().get("filterMap").getAsJsonArray();
				}
			}
		}
		// if it falls through without finding the specified one, then retry with the default.
		return getFilterJsonArray(dataTypeName, CONTEXT_FILTERS_DEFAULT);
	}
	
	/**
	 * Makes a JsonArray containing filter entries based on a data item that includes all properties in a dataItem.
	 * This can be used if there is no default filter available and just all properties of a data item will be returned.
	 * 
	 * @param dataItem - the data item to base the filter map on
	 * 
	 * @return - Map containing the generated filter map
	 */
	public static JsonArray makeFilterJsonArrayFromDataItem(DataItem dataItem){
		JsonArray filter = new JsonArray();
		for (DataItemPropertyDescriptor propertyDescriptor : dataItem.dataItemDescriptor().getPropertyDescriptors()){
			JsonObject filterEntry = new JsonObject();
			filterEntry.addProperty("jsonKey", propertyDescriptor.getPropertyName());
			filterEntry.addProperty("propertyName", propertyDescriptor.getPropertyName());
			filter.add(filterEntry);
		}
		return filter;
	}
	
	/**
	 * Gets a Translation Object from storage
	 * 
	 * @param dataTypeName - the type of Translation Object to get.  This equates to the id of the data
	 * @return
	 * @throws ServiceException
	 */
	public static JsonObject getTranslationObject(String dataTypeName) throws ServiceException {
		// JSON parsing is expensive, and required properties shouldn't change, so try to retrieve a cached item
		JsonObject translationObject = DATA_ITEM_TEANSLATION_OBJECT_MAP.get(dataTypeName);
		if (translationObject != null) {
			return translationObject;
		}
		try {
			DataItem dataItem = DataStore.getInstance().readItemImmutable(dataTypeName, DATA_ITEM_NAME_CONTEXT_FILTERS);
			if (dataItem == null){
				// if we can't find a defined context filter, then we will just return null and allow the calling method to handle that.
				return null;
			}
			translationObject = new JsonParser().parse(dataItem.getString("jsonData")).getAsJsonObject();
			DATA_ITEM_TEANSLATION_OBJECT_MAP.put(dataTypeName, translationObject);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("DataStore Exception while looking up translation item " + dataTypeName, e);
		} catch (JsonSyntaxException | IllegalStateException e){
			throw new InternalServerErrorException("Invalid Json Syntax in translation item for " + dataTypeName, e);
		}
		
		return translationObject;
	}
}