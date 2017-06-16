package com.heb.liquidsky.endpoints.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.google.common.base.Preconditions;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.endpoints.tools.ListItemTools;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * 
 * This wraps the shopping list data item for a meaningful json return
 * Ideal constructor: ItemDetailsWrapper(DataItem listItemDataItem, String ownerId, String siteId, String storeId)
 * @author Scott McArthur
 * 
 */
public class ItemDetailsWrapper{

	private static final Logger logger = Logger.getLogger(ItemDetailsWrapper.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ItemDetailsWrapper.class);

	private final String ownerId; // comes from the request
	private final String storeId; // comes from the request
	// TODO: determine if site ID should ever be passed in once parent property is ready.
	private final String siteId;  // stored in the Shopping List
	private final DataItem listItemDataItem;
	private Map<String, Object> itemDetailsMap;

	/**
	 * This is the most commonly used constructor
	 * 
	 * @param listItemDataItem
	 * @param ownerId
	 * @param siteId
	 * @param storeId
	 */
	public ItemDetailsWrapper(DataItem listItemDataItem, String ownerId, String siteId, String storeId){
		this.listItemDataItem = Preconditions.checkNotNull(listItemDataItem);
		this.ownerId = ownerId;
		this.siteId = siteId;
		this.storeId = storeId;
	}

	/**
	 * This is the main point of this class.  This method creates a formatted map containing all of the details of this list item.
	 *  
	 * @return Map of all items details.
	 */
	public Map<String, Object> getItemDetailsMap() {
		HebTraceContext context = TRACER.startSpan("getItemDetailsMap");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " Triggered");
			if (itemDetailsMap == null){
				// Build the return data
				this.transformDataItemToMap();
			}
			return itemDetailsMap;
		} finally {
			TRACER.endSpan(context);
		}
	}

	/**
	 * If listItemDataItem is null and the type and id is set, then it is looked up and set it.
	 * @return
	 */
	private DataItem getListItemDataItem() {
		return this.listItemDataItem;
	}

	/**
	 * Returns the item type.  If it is not set and the DataItem is, then it will extract it from the data item
	 * @return
	 */
	private String getItemType() {
		return this.getListItemDataItem().getDataType().getName();
	}

	/*
	 * ==-- These items cannot be looked up if they are not supplied
	 */
	/**
	 * gets the owner Id.
	 * This is the owner ID from the request for validation purposes only
	 * @return
	 */
	private String getOwnerId() {
		return ownerId;
	}

	/*
	 * Helper Methods
	 */
	
	/**
	 * This will use the DataItem to set itemDetailsMap to a formatted map that is suitable to return to an Endpoint 
	 * @return
	 */
	private boolean transformDataItemToMap(){
		HebTraceContext context = TRACER.startSpan("transformDataItemToMap");
		try {
			// validate the owner if ownerId is set.
			if (!StringUtils.isBlank(this.getOwnerId())){
				// TODO: fix this once Ryan fixes the data layer to allow the parent list to be available to the list item
			}
			this.itemDetailsMap = new HashMap<String, Object>();
			
			for(String key : ShoppingListConstants.DATA_ITEM_NAME_TO_TRANSFORM_MAP.get(this.getItemType()).keySet()){
	    		String value = ShoppingListConstants.DATA_ITEM_NAME_TO_TRANSFORM_MAP.get(this.getItemType()).get(key);
	    		
	    		if (!StringUtils.isBlank(value)){
	    			try {
	        			switch (value) {
	        				// handle timestamps and quantities as they are not stored as strings.
			    			case "creationDate":
			    			case "lastModifiedDate":
			    				itemDetailsMap.put(key, this.listItemDataItem.getTimestamp(value));
			    				break;
			    			case "quantity":
			    			case "status":
			    				itemDetailsMap.put(key, this.listItemDataItem.getLong(value));
			    				break;
				    		default:
				    			itemDetailsMap.put(key, this.listItemDataItem.getString(value));
	        			}
	    			} catch (DataStoreException e) {
	    				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Data layer failure", e);
	    				itemDetailsMap.put(key, value + " NOT FOUND");
	    			} catch (ClassCastException e) {
	    				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Class Cast Exception", e);
	    				itemDetailsMap.put(key, value + " WRONG DATA TYPE CAST");
	    			}
	    		} else {
	    			// Get external data
	    			switch (this.getItemType()) {
	    				case ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME:
	    	    			switch (key) {
				    			case "location":
				    				itemDetailsMap.put(key, ListItemTools.getProductLocation((String) itemDetailsMap.get("productId"), this.storeId));
				    				break;
				    			case "avaliablity":
				    				itemDetailsMap.put(key, ListItemTools.getAvailability((String) itemDetailsMap.get("productId"), this.storeId, Integer.parseInt(this.siteId)));
				    				break;
					    		default:
	    	    			}
	    	    			break;
	    	    		// We don't currently have access to coupon data, so this just fills in some static information
	    	    		// TODO: add proper lookups once we have access to coupon data.
	    				case ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME:
	    					switch (key) {
				    			case "name":
				    				itemDetailsMap.put(key, "STATIC Save $1.25  on any FIVE (5) Campbell's&reg; Condensed Soups");
				    				break;
				    			case "restrictions":
				    				itemDetailsMap.put(key, "Limit 1 per Customer");
				    				break;
				    			case "sceneSevenImageId":
				    				itemDetailsMap.put(key, "00000153420");
				    				break;
				    			case "couponExpirationDate":
				    				itemDetailsMap.put(key, "12/22/2017");
				    				break;
				    			case "couponStartDate":
				    				itemDetailsMap.put(key, "01/08/2017");
				    				break;
					    		default:
	    					}
	    					break;
	    				default:
	    			}
	    		}
			}
			return true;
		} finally {
			TRACER.endSpan(context);
		}
	}
}
