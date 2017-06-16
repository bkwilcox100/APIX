package com.heb.liquidsky.endpoints.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataItemPropertyDescriptor;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;

/**
 * This class has many tool methods for manipulating shopping list items
 * @author Scott McArthur
 *
 */
public class ListItemTools{
	
	private static final Logger logger = Logger.getLogger(ListItemTools.class.getName());
	
	/**
	 * Gets a List Item DataItem based on the ID and type that is passed in.
	 * 
	 * @param itemId
	 * @param type
	 * @return
	 */
	public static DataItem readListItem(String itemId, String type){
		if (StringUtils.isBlank(itemId) || StringUtils.isBlank(type)) {
			return null;
		}
		try {
			DataItem dataItem = DataStore.getInstance().readItemImmutable(itemId, type);
			if (dataItem != null){
				return dataItem;
			}
		} catch (DataStoreException e) {
			logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Exception while reading list item of type " + CloudUtil.secureLogMessage(type) + " with Id " + CloudUtil.secureLogMessage(itemId), e);
		}
		return null;
	}
	
	public static DataItem readParentShoppingListItem(DataItem listItemDataItem){
		if (listItemDataItem == null) {
			return null;
		}
		
		DataItem parentListDataItem = null;
		try{
			parentListDataItem = listItemDataItem.getItem(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PARENT_SHOPPING_LIST);
		} catch (DataStoreException e) {
			logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure while trying to validate ownership of item " + listItemDataItem.getId(), e);
		}
		return parentListDataItem;
	}
	
	/**
	 * looks at the parent list to get the owner of this list item
	 * @param listItemDataItem
	 * @return
	 */
	public static String readParentShoppingListOwner(DataItem listItemDataItem){
		if (listItemDataItem == null) {
			return null;
		}
		
		DataItem parentListDataItem = null;
		String listOwnerId = null;
		try{
			parentListDataItem = listItemDataItem.getItem(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PARENT_SHOPPING_LIST);
			listOwnerId = parentListDataItem.getString(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_OWNER_ID);
		} catch (DataStoreException e) {
			logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to get owner id", e);
		}
		return listOwnerId;
	}

	/**
	 * Validates a list item exists for a given list and is owned by the owner.
	 *
	 * @param itemId
	 * @param itemType
	 * @param ownerId
	 * @return - null on success or error message string.
	 */
	public static void validateListItem(String listId, String itemId, String itemType, String ownerId) throws ServiceException {
		if (StringUtils.isBlank(ownerId) || StringUtils.isBlank(itemId) || StringUtils.isBlank(itemType) || StringUtils.isBlank(listId)){
			throw new BadRequestException("listItemValidation: Invalid Parameters");
		}
		DataItem itemDataItem = readListItem(itemId, itemType);
		if (itemDataItem == null) {
			throw new NotFoundException("Could not find item " + itemId + " of type " + itemType);
		}
		
		validateParentListId (itemDataItem, listId);
		validateOwner(itemDataItem, ownerId);
	}

	/**
	 * Validates that a given list item is in a list owned by the given owner
	 * 
	 * @param listItemDataItem
	 * @param ownerId
	 * @return null on success or error message string on failure
	 */
	public static void validateOwner(DataItem listItemDataItem, String ownerId) throws BadRequestException {
		if (listItemDataItem == null || StringUtils.isBlank(ownerId)){
			if (logger.isLoggable(Level.WARNING)) logger.warning(CloudUtil.getMethodName() + "validateOwner: Invalid Parameters");
			throw new BadRequestException("validateOwner: Invalid Parameters");
		}
		String listOwnerId = readParentShoppingListOwner(listItemDataItem);
		if (StringUtils.isBlank(listOwnerId)) {
			throw new BadRequestException("Unable to validate owner");
		}
		if (!listOwnerId.equals(ownerId)){
			throw new BadRequestException("Item " + listItemDataItem.getId() + " is not in a list owned by " + ownerId);
		}
	}

	
	public static void validateParentListId(DataItem listItemDataItem, String listId) throws BadRequestException {
		if (listItemDataItem == null || StringUtils.isBlank(listId)){
			throw new BadRequestException("validateParentListId: Invalid Parameters");
		}
		DataItem parentList = readParentShoppingListItem(listItemDataItem);
		if (parentList == null) {
			throw new BadRequestException("Unable to validate owner");
		}
		if (!listId.equals(parentList.getId())){
			throw new BadRequestException("Item " + listItemDataItem.getId() + " is not in the list with Id " + listId);
		}
	}

	/**
	 * Upsert function for adding items to a list.
	 * With Owner Validation
	 * 
	 * @param itemsMap
	 * @param itemType
	 * @param listId
	 * @param ownerId
	 * @return
	 */
	public static List<Map<String, Object>> upsertListItems(Map<String, Object> itemsMap, String itemType, String listId, String ownerId) throws ServiceException {
		// Validate Input
		if (StringUtils.isBlank(listId)){
			throw new BadRequestException("listId cannot be empty");
		}
		if (StringUtils.isBlank(ownerId)){
			throw new BadRequestException("ownerId cannot be empty");
		}
		// Validate that the list is owned by the passed in owner.
		ShoppingListTools.validateOwner(ownerId, listId);
		if (StringUtils.isBlank(itemType)){
			throw new BadRequestException("itemType cannot be empty");
		}
		if (itemsMap == null || itemsMap.isEmpty()){
			throw new BadRequestException("payload cannot be empty");
		}
		// get the list that the items are being added to.
		DataItem immutableShoppingList = ShoppingListTools.readShoppingList(listId);
		if (immutableShoppingList == null){
			throw new NotFoundException("Could not get Shopping List with Id " + listId);
		}
		// look for the item list in the payload to be added or updated
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> payloadItemListMap = (List<Map<String, Object>>) itemsMap.get(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemType));
		if (payloadItemListMap == null || payloadItemListMap.isEmpty()){
			throw new NotFoundException("No items of type " + itemType + " found in the payload");
		}
		
		// get the item list from the Shopping List
		List<DataItem> childItemList = ShoppingListTools.getItemList(immutableShoppingList, itemType);
		if (childItemList == null){
			throw new NotFoundException("Could not get list of items in property " + ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemType) + " for Shopping List with Id " + listId);
		}
		int childItemCount = childItemList.size();
		// These are used for processing the upserResult return payload
		List<Object> failureList = new ArrayList<Object>();
		Map<String, Object> successMap = new HashMap<String, Object>();
		// Process the new items
		for (Map<String, Object> currentItem : payloadItemListMap) {
			// If id exists, this is an update, ensure that the item exists.
			String currentItemId = null;
			MutableDataItem mutableDataItem = null;

			// check if item already exists in the shopping list and modify the payload accordingly.
			processPayloadData(currentItem, childItemList, itemType);
			
			if (currentItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID)){
			// updating an existing item
				currentItemId = (String) currentItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID);
				try {
					mutableDataItem = DataStore.getInstance().readItemForUpdate(currentItemId, itemType);
					if (mutableDataItem == null){
						currentItem.put("reason", "Could not read data item for update with item Id " + currentItemId);
						failureList.add(currentItem);
						continue;
					}
					// check that this item belongs to the passed in list
					if (mutableDataItem.getItem("parentShoppingList") != null && !mutableDataItem.getItem("parentShoppingList").getId().equals(listId)){
						currentItem.put("reason", "This item does not belong to the Shopping List with the Id " + listId);
						failureList.add(currentItem);
						continue;
					}
				} catch (DataStoreException e) {
					logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure reading list item with Id " + CloudUtil.secureLogMessage(currentItemId), e);
					currentItem.put("reason", "Exception " + e.getMessage());
					failureList.add(currentItem);
					continue;
				}
			} else {
			// Inserting a new Item
				if (childItemCount >= ShoppingListConstants.SHOPPING_LIST_MAX_LIST_ITEMS){
					currentItem.put("reason", "List already contains " + childItemCount + "out of a maximum of" + ShoppingListConstants.SHOPPING_LIST_MAX_LIST_ITEMS + "of type " + CloudUtil.secureLogMessage(itemType));
					failureList.add(currentItem);
					continue;
				} else {
					try {
						mutableDataItem = DataStore.getInstance().createItem(itemType);
					} catch (DataStoreException e) {
						// log the error and continue.  Don't throw an exception
						logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure creating a new item of type " + itemType, e);
						currentItem.put("reason", "Could not create new data item.");
						failureList.add(currentItem);
						continue;
					}
				}
			}
			
			// loop through the properties of this item and add / update the values
			for (DataItemPropertyDescriptor currentPropertyDescriptor : mutableDataItem.dataItemDescriptor().getPropertyDescriptors()){
				String currentPropertyName = currentPropertyDescriptor.getPropertyName();
				// TODO: check some list of required data to ensure that we are good.
				if (currentItem.containsKey(currentPropertyName)){
					String currentPropertyValue = objectToString(currentItem.get(currentPropertyName));
					// TODO: Add in real validation here.  Max size and maybe some db injection checks.
					if (currentPropertyValue == null) currentPropertyValue = "";  // change null to an empty string (for blank notes)
					// don't allow for update of certain values
					if (currentItemId != null && ShoppingListConstants.SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.contains(currentPropertyName)){
						if (!currentPropertyName.equals(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID)) {
							currentItem.put(currentPropertyName, "IGNORED " + currentItem.get(currentPropertyName));
						}
						continue;
					}
					// simple check for length greater than 1000, which is the max notes length.  this should be improved to be diverse for each type
					if (currentPropertyName.equals(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_NOTES) && currentPropertyValue.length() > 1000){
						currentPropertyValue = currentPropertyValue.substring(0, Math.min(currentPropertyValue.length(), 1000));
					}
					mutableDataItem.setProperty(currentPropertyName, currentPropertyValue);
				}
			}
			
			// set parent shopping list explicitly if this is a new item
			if (currentItemId == null){
				mutableDataItem.setProperty("parentShoppingList", immutableShoppingList);
			}
			
			// Write the new item
			try {
				if (currentItemId == null){
					DataStore.getInstance().insertItem(mutableDataItem);
				} else {
					DataStore.getInstance().updateItem(mutableDataItem);
				}
			} catch (DataStoreException e) {
				// log the error and continue.  Don't throw an exception
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure writing item " + mutableDataItem.getId(), e);
				currentItem.put("reason", "Failed to write the item. Exception " + e.getMessage());
				failureList.add(currentItem);
				continue;
			}
			childItemCount ++;
			successMap.put(mutableDataItem.getId(), currentItem);
		}
		
		// force a firebase message
		ShoppingListTools.touchShoppingList(listId);
		
		//  All data has been handled.  Prepare the return message		
		List<Object> successList = new ArrayList<Object>();  // temporary list for formatting return message
		for (String key : successMap.keySet()) {
			successList.add(successMap.get(key));
		}
		Map<String, Object> returnMessageData = new HashMap<String, Object>(); // temporary map for better formatting of the return message
		returnMessageData.put(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemType), successList);
		Map<String, Object> successReturn = new HashMap<String, Object>();
		successReturn.put("success", returnMessageData);
		returnMessageData = new HashMap<String, Object>(); // reset the temporary map to get a new pointer.
		returnMessageData.put(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemType), failureList);
		Map<String, Object> failureReturn = new HashMap<String, Object>();
		failureReturn.put("fail", returnMessageData);
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		returnList.add(successReturn);
		returnList.add(failureReturn);
		return returnList;
	}

	/**
	 * Deletes the list item
	 * With owner validation
	 * 
	 * @param listId
	 * @param itemType
	 * @param itemId
	 * @param ownerId
	 * @return
	 */
	public static void deleteListItemDataItem(String listId, String itemType, String itemId, String ownerId) throws ServiceException {
		ListItemTools.validateListItem(listId, itemId, itemType, ownerId);
		try {
			MutableDataItem mutableDataItem = DataStore.getInstance().readItemForUpdate(itemId, itemType);
			if (mutableDataItem == null) {
				throw new NotFoundException("Delete failed.  " + itemType + " does not exist with Id " + itemId);
			}
			DataStore.getInstance().deleteItem(mutableDataItem);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Failed to delete " + itemType + " with Id " + itemId, e);
		}
	}
	
	/**
	 * Looks up a products availability. This is dependent on the store ID so it cannot be done with a custom property since the store is sent in the request header.
	 * 
	 * @param productId
	 * @param storeId
	 * @param siteId
	 * @return
	 */
	public static Boolean getAvailability(String productId, String storeId, int siteId){
		switch (siteId) {  // this will be used to determine which product data source is used (atg, api hub, or central market)
			case ShoppingListConstants.SITE_ID_HEB_API_HUB:   // api hub data
				break;
			case ShoppingListConstants.SITE_ID_CENTRAL_MARKET:   // Central Market Data
				break;
			case ShoppingListConstants.SITE_ID_HEB_ATG:  // atg based heb.com data
				if (storeId != null){
					try {
						DataItem atgProductDataItem = DataStore.getInstance().readItemImmutable(productId, ShoppingListConstants.ATG_PRODUCT_ITEM_NAME);
						DataItem atgAssortmentDataItem = DataStore.getInstance().readItemImmutable(productId + "_" + storeId, ShoppingListConstants.ATG_ASSORTMENT_ITEM_NAME);
						if (atgProductDataItem != null && atgAssortmentDataItem != null){
							Date now = new Date();
							Date startDate = atgProductDataItem.getTimestamp("startDate");
							Date endDate = atgProductDataItem.getTimestamp("endDate");
							if (startDate == null || (startDate == null && endDate == null)){
								return false;
							}
							if ((endDate == null && now.after(startDate)) || (now.after(startDate) && now.before(endDate))){
								return true;
							}
						}
					} catch (DataStoreException e) {
						logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Exception while reading external ATG", e);
					}
				}
				break;
			default:
		}
		return false;
	}
	
	/**
	 * Looks up a products location in the physical store. 
	 * This is dependent on the store ID so it cannot be done with a custom property since the store is sent in the request header.
	 * 
	 * @param productId
	 * @param storeId
	 * @return
	 */
	public static String getProductLocation(String productId, String storeId){
		if (storeId != null){
			try {
				DataItem atgAssortmentDataItem = DataStore.getInstance().readItemImmutable(productId + "_" + storeId, ShoppingListConstants.ATG_ASSORTMENT_ITEM_NAME);
				if (atgAssortmentDataItem != null){
					return atgAssortmentDataItem.getString("location");
				}
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Exception while reading external ATG Locaiton Data", e);
			}
		}
		return null;
	}
	
	/*
	 * =======-------  Upsert Helper Methods.
	 */
	
	/**
	 * Processes an item that is passed in an endpoint payload to make changes if the item already exists and is being added again.
	 * Basically changes creating an item to updating an item. 
	 * No return, but the payLoadItem that is passed in may be modified.
	 * 
	 * @param payloadItem
	 * @param childItemList
	 * @param itemType
	 */
	private static void processPayloadData(Map<String, Object> payloadItem, List<DataItem> childItemList, String itemType){
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " triggered");
		
		// If id exists, this is an update, ensure that the item exists.
		String externalId = "";

		// check if the item's external ID is already in the list and if it's already there, treat this as an update
		// THIS BIT ONLY MODIFIES THE PAYLOAD DATA so that it will be processed correctly.
		if (!payloadItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID)){
			switch (itemType) {
			case ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME:
				if (payloadItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PRODUCT_ID)){
					externalId = (String) payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PRODUCT_ID);
					if (!StringUtils.isBlank(externalId)){
						for (DataItem currentChildItem : childItemList){
							try {
								if (externalId.equals(currentChildItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PRODUCT_ID))){
									//list already has this external item in it.  add id to change to update and adjust quantity
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, currentChildItem.getId());
									Long currentChildQuantity = currentChildItem.getLong(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY);  
									int newQuantity = objectToInteger(payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY), 1);  // if no new quantity is sent, assume we are just incrementing
									newQuantity += currentChildQuantity.intValue();
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY, Integer.toString(newQuantity)); 
								}
							} catch (DataStoreException e) {
								// couldn't read the external ID from the child item for some reason.  Ignore and move on
								logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure reading list item with Id " + currentChildItem.getId(), e);
							}
						}
					}
				}
				break;
			case ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME:
				if (payloadItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_FREEFORM_ID)){
					// external Id is not really external in this case, but we will use it to detect duplicates.
					externalId = (String) payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_FREEFORM_ID);
					if (!StringUtils.isBlank(externalId)){
						for (DataItem currentChildItem : childItemList){
							try {
								if (externalId.equals(currentChildItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_FREEFORM_ID))){
									//list already has this item in it.  add id to the payload in order to change it to an update and adjust quantity
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, currentChildItem.getId());
									Long currentChildQuantity = currentChildItem.getLong(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY);
									int newQuantity = objectToInteger(payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY), 1);  // if no new quantity is sent, assume we are just incrementing
									newQuantity += currentChildQuantity.intValue();
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_QUANTITY, Integer.toString(newQuantity)); 
								}
							} catch (DataStoreException e) {
								// couldn't read the external ID from the child item for some reason.  Ignore and move on
								logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure reading list item with Id " + currentChildItem.getId(), e);
							}
						}
					}
				}
				break;
			case ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME:
				if (payloadItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_COUPON_ID)){
					externalId = (String) payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_COUPON_ID);
					if (!StringUtils.isBlank(externalId)){
						for (DataItem currentChildItem : childItemList){
							try {
								if (externalId.equals(currentChildItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_COUPON_ID))){
									// add id to change this to an update (for notes or whatever)
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, currentChildItem.getId());
								}
							} catch (DataStoreException e) {
								// couldn't read the external ID from the child item for some reason.  Ignore and move on
								logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure reading list item with Id " + currentChildItem.getId(), e);
							}
						}
					}
				}
				break;
			case ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME:
				if (payloadItem.containsKey(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_RECIPE_ID)){
					externalId = (String) payloadItem.get(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_RECIPE_ID);
					if (!StringUtils.isBlank(externalId)){
						for (DataItem currentChildItem : childItemList){
							try {
								if (externalId.equals(currentChildItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_RECIPE_ID))){
									// add id to change this to an update (for notes or whatever)
									payloadItem.put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, currentChildItem.getId());
								}
							} catch (DataStoreException e) {
								// couldn't read the external ID from the child item for some reason.  Ignore and move on
								logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failure reading list item with Id " + currentChildItem.getId(), e);
							}
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * Utility method for examining a parsed JSON value and
	 * returning it as a String.
	 */
	private static String objectToString(Object object) {
		if (object instanceof String) {
			return (String) object;
		}
		if (object instanceof Integer) {
			return ((Integer) object).toString();
		}
		if (object instanceof Long) {
			return ((Long) object).toString();
		}
		if (object instanceof Double) {
			return ((Double) object).toString();
		}
		return (object != null) ? object.toString() : "";
	}

	/**
	 * Utility method for examining a parsed JSON value and
	 * returning it as an Integer.
	 */
	private static int objectToInteger(Object object, int defaultValueIfNull) {
		if (object == null) {
			return defaultValueIfNull;
		}
		if (object instanceof Integer) {
			return ((Integer) object).intValue();
		}
		return Integer.parseInt(objectToString(object));
	}
}
