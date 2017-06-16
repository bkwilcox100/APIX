package com.heb.liquidsky.endpoints.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.response.UnauthorizedException;
import com.heb.liquidsky.endpoints.wrapper.ShoppingListDetailsWrapper;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class has many helper tools to be used for reading or manipulating Shopping List items.
 * @author Scott McArthur
 */
public class ShoppingListTools{
	
	private static final Logger logger = Logger.getLogger(ShoppingListTools.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ShoppingListTools.class);

	/**
	 * Generates a list of all shopping lists for a specified owner.  This only gets the headers and not details for each item in the list.
	 * @param ownerId
	 * @return
	 */
	public static Map<String, Object> getAllUserShoppingListHeaders(String ownerId) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getAllUserShoppingListHeaders");
		try {
			List<DataItem> listIds = null;
			try {
				listIds = DataStore.getInstance().executeNamedQuery(ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME, ShoppingListConstants.NAMED_QUERY_SHOPPING_LIST_BY_OWNER, ownerId);
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("Failure while getting lists for ownerId " + ownerId, e);
			}
			List<Object> returnList = new ArrayList<Object>();
			for (DataItem currentItem : listIds) {
				ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(currentItem, ownerId, null);
				Map<String, Object> details = new HashMap<String, Object>();
				details.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsHeader());
				returnList.add(details);
			}
			Map<String, Object> returnValue = new HashMap<String, Object>();
			returnValue.put(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_OWNER_ID, ownerId);
			returnValue.put("listCount", returnList.size());
			returnValue.put("shoppingLists", returnList);
			return returnValue;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Gets a shopping List data Item based on the listId that is provided.
	 * 
	 * @param listId
	 * @return
	 */
	public static DataItem readShoppingList(String listId) throws InternalServerErrorException {
		HebTraceContext context = TRACER.startSpan("readShoppingList");
		try {
			return DataStore.getInstance().readItemImmutable(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Exception while reading a Shopping List with Id " + listId, e);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Checks if the list is owned by the owner.  
	 * This is useful if there is not a list DataItem already available
	 *   
	 * @param ownerId
	 * @param listId
	 * @return true if isOwner
	 * @throws DataStoreException 
	 */
	private static boolean isOwner(String listId, String ownerId) throws DataStoreException{
		DataItem dataItem = DataStore.getInstance().readItemImmutable(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
		return isOwner(dataItem, ownerId);
	}
	
	/**
	 * Checks if the list is owned by the owner.
	 * 
	 * @param shoppingListDataItem
	 * @param ownerId
	 * @return
	 * @throws DataStoreException
	 */
	private static boolean isOwner(DataItem shoppingListDataItem, String ownerId) throws DataStoreException{
		String listOwnerId = shoppingListDataItem.getString(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_OWNER_ID);
		return (!StringUtils.isBlank(listOwnerId) && StringUtils.equals(listOwnerId, ownerId));
	}
	
	/**
	 * Validates if a list is owned by the given owner Id.
	 * 
	 * @param ownerId
	 * @param listId
	 * @return Returns null if ownerId does match the owner Id in the list or an error message string.
	 */
	public static void validateOwner(String ownerId, String listId) throws ServiceException {
		if (!listExists(listId)){
			throw new NotFoundException("list " + listId + " does not exist.");
		}
		if (StringUtils.isBlank(ownerId)){
			throw new BadRequestException("ownerId not specified");
		}
		try {
			if (!isOwner(listId, ownerId)){
				throw new UnauthorizedException("Could not validate list ownership of list " + listId + " by owner " + ownerId);
			}
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Exception while validating ownership of list " + listId + " by owner " + ownerId, e);
		}
	}
	
	/**
	 * Validates that a given shopping List is owned by the given owner.
	 * 
	 * @param shoppingListDataItem
	 * @param OwnerId
	 * @return null if list is owned by the owner ID, error map if not or error occurs.
	 */
	public static void validateOwner(DataItem shoppingListDataItem, String ownerId) throws ServiceException {
		if (shoppingListDataItem == null || StringUtils.isBlank(ownerId)){
			if (logger.isLoggable(Level.WARNING)) logger.warning(CloudUtil.getMethodName() + " Invalid Parameters");
			throw new BadRequestException("validateOwner: Invalid Parameters");
		}
		try {
			if (!isOwner(shoppingListDataItem, ownerId)){
				throw new UnauthorizedException("Could not validate list ownership of list " + shoppingListDataItem.getId() + " by owner " + ownerId);
			}
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Exception while validating ownership of list " + shoppingListDataItem.getId() + " by owner " + ownerId, e);
		}
	}
	
	/**
	 * Returns true if a list with the given ID is retrievable from the data store
	 * 
	 * @param listId
	 * @return
	 */
	private static boolean listExists(String listId) throws InternalServerErrorException {
		try {
			DataItem dataItem = DataStore.getInstance().readItemImmutable(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
			return (dataItem != null);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Failure while checking the existance of a list listId:" + listId, e);
		}
	}
	
	/**
	 * Gets a list of items of the specified type that are attached to the passed list.
	 * Handles catching any exceptions
	 * @return list of DataItems of the specified type
	 */
	public static List<DataItem> getItemList(DataItem shoppingListDataItem, String type) throws InternalServerErrorException {
		if (shoppingListDataItem == null || StringUtils.isBlank(type)) return null;
		try {
			return shoppingListDataItem.getList(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(type));
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Failure getting " + type + " list for Shopping List listId: " + shoppingListDataItem.getId(), e);
		}
	}

	/**
	 * Returns the number of Shopping Lists for a given owner
	 *  
	 * @param ownerId
	 * @return Integer containing the total count of lists owned by the given owner
	 */
	public static Integer getOwnerListCount(String ownerId){
		List<DataItem> listIds = null;
		Integer listCount = 0;
		
		if (!StringUtils.isBlank(ownerId)){
			try {
				listIds = DataStore.getInstance().executeNamedQuery(ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME, ShoppingListConstants.NAMED_QUERY_SHOPPING_LIST_BY_OWNER, ownerId);
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + "Exception while executing named query " + ShoppingListConstants.NAMED_QUERY_SHOPPING_LIST_BY_OWNER, e);
			}
			if (listIds != null){
				listCount = listIds.size();
			}
		}
		return listCount;
	}
	
	/**
	 * gets a new mutable Shopping List Data Item
	 * @return MutableDataItem or null on failure
	 */
	public static MutableDataItem getListMutableDataItem() throws InternalServerErrorException {
		try {
			return DataStore.getInstance().createItem(ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Could not create list", e);
		}
	}

	/**
	 * This method simply gets a list for reading and does an update with no change.  
	 * This is typically used to trigger a firebase message when updating one to many items in a shopping List
	 * @param listId
	 */
	public static void touchShoppingList(String listId){
		if (!StringUtils.isBlank(listId)){
			try {
				MutableDataItem mutableListDataItem = DataStore.getInstance().readItemForUpdate(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
				DataStore.getInstance().updateItem(mutableListDataItem);
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Could not create list", e);
			}
		}	
	}
	
	/**
	 * Creates a shopping list from a request body
	 * upsertListResult format is defined here:
	 * https://confluence.heb.com:8443/display/ESELLING/Middle+Layer+Shopping+List+Endpoints#MiddleLayerShoppingListEndpoints-upsertListResult
	 * 
	 * @param ownerId
	 * @param requestBody
	 * @return upsertListResult or error message
	 */
	@SuppressWarnings("unchecked")
	public static String createShoppingList(String ownerId, Map<String, Object> requestBody) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("createShoppingList");
		try {
			String listName = (String) requestBody.get(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_LIST_NAME);
			// Validate required fields for creating a list.
			if (StringUtils.isBlank(ownerId)){
				throw new BadRequestException("ownerId cannot be empty");
			}
			if (StringUtils.isBlank(listName)){
				throw new BadRequestException("listName cannot be empty");
			}
			if (getOwnerListCount(ownerId) >= ShoppingListConstants.SHOPPING_LIST_MAX_LISTS){
				throw new InternalServerErrorException("ownerId " + ownerId + " has reached or exceeded the maximum number of allowed Shopping Lists " + ShoppingListConstants.SHOPPING_LIST_MAX_LISTS);
			}
			
			// create a new mutable shopping list
			MutableDataItem mutableListDataItem = getListMutableDataItem();
			//add the basic details
			mutableListDataItem.setProperty(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_OWNER_ID, ownerId);
			mutableListDataItem.setProperty(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_LIST_NAME, listName);
			long siteId = NumberUtils.toLong((String) requestBody.get(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_SITE_ID), ShoppingListConstants.SITE_ID_DEFAULT);
			mutableListDataItem.setProperty(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_SITE_ID, siteId);
			
			// save the list (before adding items so that an ID is generated)
			try {
				DataStore.getInstance().insertItem(mutableListDataItem);
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("Could not insert new list", e);
			}
			// check if there are any items in the requestBody
			if (requestBody.containsKey("shoppingListItems")){
				String listId = mutableListDataItem.getId();
				Map<String, Object> shoppingListItemsMap = (Map<String, Object>) requestBody.get("shoppingListItems");
				int i = 0;
				for (String itemDataTypeName : ShoppingListConstants.SHOPPING_LIST_ITEM_DATA_TYPE_LIST) {
					String requestItemTypeKey = ShoppingListConstants.REQUEST_ITEM_TYPE_KEY_MAP.get(itemDataTypeName);
					String requestItemListKey = ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemDataTypeName);
					
					if (shoppingListItemsMap.containsKey(requestItemTypeKey)){
						Map<String, Object> itemListMap = (Map<String, Object>) shoppingListItemsMap.get(requestItemTypeKey);
						if (itemListMap.containsKey(requestItemListKey)){
							try {
								ListItemTools.upsertListItems(itemListMap, itemDataTypeName, listId, ownerId);
							} catch (ServiceException e) {
								logger.log(Level.WARNING, CloudUtil.getMethodName() + " upsertListItems failed for list with Id " + listId);
								if (i > 0) {
									// we have partial success.  this could be a problem
									// TODO: check if we need to ad some sort of notification to the response
									logger.log(Level.WARNING, CloudUtil.getMethodName() + 
											" Partial success while creating a list.  Some items have been successfully added while others were not. List Id: " + listId);
								} else {
									return null;
								}
							}
							i++;
						}
					}
				}
			}
			
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " Successfully created Shopping List Id: " + mutableListDataItem.getId());
			return mutableListDataItem.getId();

		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Updates a shopping list with data from a request body
	 * @param ownerId
	 * @param listId
	 * @param requestBody
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String updateShoppingList(String ownerId, String listId, Map<String, Object> requestBody) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("updateShoppingList");
		try {
			String listName = (String) requestBody.get(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_LIST_NAME);
			// Validate required fields for creating a list.
			if (StringUtils.isBlank(listId)){
				throw new BadRequestException("Shopping listId cannot be empty");
			}
			if (StringUtils.isBlank(ownerId)){
				throw new BadRequestException("Unable to update list with ID " + listId + " - ownerId cannot be empty");
			}
			// get the mutable item
			MutableDataItem mutableListDataItem = null;
			try {
				mutableListDataItem = DataStore.getInstance().readItemForUpdate(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("Could not get list with ID " + listId, e);
			}
			if (mutableListDataItem == null){
				throw new NotFoundException("Could not find shopping list with ID " + listId);
			}
			//  validate that the passed owner owns the given list
			ShoppingListTools.validateOwner(mutableListDataItem, ownerId);
			// update the basics that are allowed (cannot update owner, id, or site id)
			mutableListDataItem.setProperty(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_LIST_NAME, listName);
			// save the list
			try {
				DataStore.getInstance().updateItem(mutableListDataItem);
			} catch (DataStoreException e) {
				throw new InternalServerErrorException("Unable to update list with ID " + listId, e);
			}
			// check if there are any items in the requestBody
			if (requestBody.containsKey("shoppingListItems")){
				Map<String, Object> shoppingListItemsMap = (Map<String, Object>) requestBody.get("shoppingListItems");
				
				for (String itemDataTypeName : ShoppingListConstants.SHOPPING_LIST_ITEM_DATA_TYPE_LIST) {
					String requestItemTypeKey = ShoppingListConstants.REQUEST_ITEM_TYPE_KEY_MAP.get(itemDataTypeName);
					String requestItemListKey = ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(itemDataTypeName);
					int i = 0;
					
					if (shoppingListItemsMap.containsKey(requestItemTypeKey)){
						Map<String, Object> itemListMap = (Map<String, Object>) shoppingListItemsMap.get(requestItemTypeKey);
						if (!itemListMap.isEmpty() && itemListMap.containsKey(requestItemListKey)){
							try {
								ListItemTools.upsertListItems(itemListMap, itemDataTypeName, listId, ownerId);
							} catch (ServiceException e) {
								logger.log(Level.WARNING, CloudUtil.getMethodName() + " upsertListItems failed for list with Id " + CloudUtil.secureLogMessage(listId));
								if (i > 0) {
									// we have partial success.  this could be a problem
									// TODO: check if we need to ad some sort of notification to the response
									logger.log(Level.WARNING, CloudUtil.getMethodName() + 
											" Partial success while updating a list.  Some items have been successfully added while others were not. List Id: " + CloudUtil.secureLogMessage(listId));
								} else {
									return null;
								}
							}
							i++;
						}
					}
				}
			}
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + " Successfully created Shopping List Id: " + mutableListDataItem.getId());
			return mutableListDataItem.getId();
		} finally {
			TRACER.endSpan(context);
		}
	}

	/**
	 * Deletes a given shopping list.  Validates owner
	 * @param listId
	 * @param ownerId
	 * @return
	 */
	public static void deleteShoppingList(String listId, String ownerId) throws ServiceException {
		ShoppingListTools.validateOwner(ownerId, listId);
		//  Get the list
		try {
			MutableDataItem mutableDataItem = DataStore.getInstance().readItemForUpdate(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
			if (mutableDataItem == null) {
				throw new NotFoundException("Delete failed. List does not exist with Id " + listId);
			}
			DataStore.getInstance().deleteItem(mutableDataItem);
		} catch (DataStoreException e) {
			throw new InternalServerErrorException("Failed to delete shopping list with Id " + listId, e);
		}
	}
}