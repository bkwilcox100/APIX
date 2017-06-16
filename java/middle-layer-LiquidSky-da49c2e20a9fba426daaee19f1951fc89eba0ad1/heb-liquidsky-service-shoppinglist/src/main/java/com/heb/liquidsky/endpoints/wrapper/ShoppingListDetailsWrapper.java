package com.heb.liquidsky.endpoints.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.endpoints.tools.ShoppingListTools;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This wraps the shopping list data item for a meaningful json return
 * @author Scott McArthur
 *
 */
public class ShoppingListDetailsWrapper{
	private static final Logger logger = Logger.getLogger(ShoppingListTools.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ShoppingListDetailsWrapper.class);
	
	private final String ownerId;
	private final String storeId;
	private final DataItem shoppingListDataItem;
	private Map<String, Object> shoppingListDetailsMap;

	/**
	 * This is the preferred constructor for a ShoppingListDetailsWrapper object
	 * 
	 * @param shoppingListDataItem
	 * @param storeId
	 * @param ownerId
	 */
	public ShoppingListDetailsWrapper(DataItem shoppingList, String ownerId, String storeId) throws BadRequestException {
		if (shoppingList == null) {
			throw new BadRequestException("Shopping list cannot be null");
		}
		this.shoppingListDataItem = shoppingList;
		this.ownerId = ownerId;
		this.storeId = storeId;
	}
	
	/**
	 * This is the secondary constructor for a ShoppingListDetailsWrapper object to be used if a DataItem is not already available
	 * 
	 * @param listId
	 * @param storeId
	 * @param ownerId
	 */
	public ShoppingListDetailsWrapper(String listId, String ownerId, String storeId) throws ServiceException {
		DataItem shoppingList = ShoppingListTools.readShoppingList(listId);
		if (shoppingList == null) {
			throw new NotFoundException("No shopping list exists with ID " + listId);
		}
		this.shoppingListDataItem = shoppingList;
		this.ownerId = ownerId;
		this.storeId = storeId;
	}
	
	/**
	 * This is the main point of a ShoppingListDetailsWrapper object and will create a map of the Shopping List Details 
	 * that is ready for consumption by an Endpoints interface
	 * 
	 * @return An ordered map of the Shopping List properties and all items contained in the list or error messages (Should never return null)
	 */
	public Map<String, Object> getShoppingListDetailsMap() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getShoppingListDetailsMap");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " Triggered");
			if (shoppingListDetailsMap == null) {
				this.transformDataItemToMap();
			}
			return this.shoppingListDetailsMap;
		} finally {
			TRACER.endSpan(context);
		}
	}

	private String getStoreId() {
		return storeId;
	}

	private String getOwnerId() {
		return ownerId;
	}

	/**
	 * Gets the shoppingListDataItem.  
	 * @return DataItem containing the saved list information
	 */
	private DataItem getShoppingListDataItem() {
		return this.shoppingListDataItem;
	}

	/*
	 * --== property getters from the Data Object
	 */

	/**
	 * Makes a map containing lists of the different types of Shopping List items
	 * @return Map of lists
	 */
	private Map<String, Object> getShoppingListItems() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getShoppingListItems");
		try {
			if (shoppingListDataItem == null) return null;
			Map<String, Object> shoppingListItems = new HashMap<String, Object>();
			shoppingListItems.put(ShoppingListConstants.RESPONSE_PRODUCT_LIST_KEY, new ItemListWrapper(this.getShoppingListDataItem(), ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, this.getOwnerId(), this.getStoreId()).getItemList());
			shoppingListItems.put(ShoppingListConstants.RESPONSE_FREEFORM_LIST_KEY, new ItemListWrapper(this.getShoppingListDataItem(), ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, this.getOwnerId(), this.getStoreId()).getItemList());
			shoppingListItems.put(ShoppingListConstants.RESPONSE_COUPON_LIST_KEY, new ItemListWrapper(this.getShoppingListDataItem(), ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, this.getOwnerId(), this.getStoreId()).getItemList());
			shoppingListItems.put(ShoppingListConstants.RESPONSE_RECIPE_LIST_KEY,new ItemListWrapper(this.getShoppingListDataItem(), ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, this.getOwnerId(), this.getStoreId()).getItemList());
			return shoppingListItems;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/*
	 * --== Helper Methods
	 */
	
	/**
	 * Generates a ShoppingListDetailsMap that does NOT include the shoppingListItems but still has the other information
	 * Hella faster than looking up all attached items when only header is needed for some responses.
	 * @return Header information for a Shopping List
	 */
	public Map<String, Object> getShoppingListDetailsHeader() {
		HebTraceContext context = TRACER.startSpan("getShoppingListDetailsHeader");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " Triggered");
			return transformDataItemToHeaderMap();
		} finally {
			TRACER.endSpan(context);
		}
	}

	/**
	 * Uses the transform map that is in the ShoppingListConstants file to modify the passed in map and add in shopping list header information
	 * @return a Map of general shopping list header information (no items)
	 */
	private Map<String, Object> transformDataItemToHeaderMap() {
		HebTraceContext context = TRACER.startSpan("transformDataItemToHeaderMap");
		try {
			Map<String, Object> shoppingListHeaderDetailsMap = new HashMap<String, Object>();
	    	for(String key : ShoppingListConstants.LIST_TRANSFORM_MAP.keySet()){
	    		String value = ShoppingListConstants.LIST_TRANSFORM_MAP.get(key);
	    		
	    		if (!StringUtils.isBlank(value)){
	    			try {
	        			switch (value) {
	        				// handle timestamps as they are not stored as strings.
			    			case "creationDate":
			    			case "lastModifiedDate":
			    				shoppingListHeaderDetailsMap.put(key, this.shoppingListDataItem.getTimestamp(value).toString());
			    				break;
			    			case "siteId":
			    				shoppingListHeaderDetailsMap.put(key, this.shoppingListDataItem.getLong(value));
			    				break;
				    		default:
				    			shoppingListHeaderDetailsMap.put(key, this.shoppingListDataItem.getString(value));
	        			}
	    			} catch (DataStoreException e) {
	    				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Data Layer exception", e);
	    				shoppingListHeaderDetailsMap.put(key, value + " NOT FOUND");
	    			}
	    		} else {
	    			// handle special cases
	    			switch (key) {
	    				case "storeId":
	    					shoppingListHeaderDetailsMap.put(key, this.storeId);
		    				break;
		    			case "shoppingListItems":
		    				// ignore shopping list items in this case
		    				break;
			    		default:
		    		}
	    		}
	    	}
	    	return shoppingListHeaderDetailsMap;
		} finally {
			TRACER.endSpan(context);
		}
	}

	/**
	 * Uses the transform map that is in the ShoppingListConstants file to gather information and populate this.shoppingListDetailsMap
	 * @return false if shoppingListDataItem is already set, true if successful
	 */
	private void transformDataItemToMap() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("transformDataItemToMap");
		try {
			ShoppingListTools.validateOwner(this.getShoppingListDataItem(), this.getOwnerId());
			// Build the return data
			this.shoppingListDetailsMap = getShoppingListDetailsHeader();
			this.shoppingListDetailsMap.put("shoppingListProperties", ShoppingListConstants.SHOPPING_LIST_PROPERTIES);
			this.shoppingListDetailsMap.put("shoppingListItems", this.getShoppingListItems());
		} finally {
			TRACER.endSpan(context);
		}
	}
}
