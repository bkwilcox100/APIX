package com.heb.liquidsky.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.Constants;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.common.ConfigurationConstants;
import com.heb.liquidsky.endpoints.auth.User;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.OAuthRequestException;
import com.heb.liquidsky.endpoints.response.PartialSuccessException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.ListItemTools;
import com.heb.liquidsky.endpoints.tools.ShoppingListTools;
import com.heb.liquidsky.endpoints.wrapper.ItemListWrapper;
import com.heb.liquidsky.endpoints.wrapper.ShoppingListDetailsWrapper;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Defines v1 of the Shopping List rest endpoints
 * Documentation can be found here:
 * https://confluence.heb.com:8443/display/ESELLING/Middle+Layer+Shopping+List+Endpoints
 * 
 * @author Scott McArthur
 *
 */
public class ShoppingListInterface {

	private static final Logger logger = Logger.getLogger(ShoppingListInterface.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ShoppingListInterface.class);

//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  All Owners Lists Operations
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Gets a list of all items for the calling owner.
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload
	 * 
	 * @return	list of shoppingLists
	 */
	public Map<String, Object> getAllLists(
			HttpServletRequest request,
			User user) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getAllLists");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			
			// Validate input
			String ownerId = this.getOwnerID(request, user);
			return ShoppingListTools.getAllUserShoppingListHeaders(ownerId);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Full List Operations
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Creates a new Shopping List for the user
	 * EXAMPLE CALL:  POST http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists
	 * 
	 * TODO: add ability to accept [shoppingListItems] also.
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload
	 * 
	 * @return	shoppingListDetails - map of the new shopping list details
	 */
	public Map<String, Object> createShoppingList(
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user,
			Boolean singleList) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("createShoppingList");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			String ownerId = this.getOwnerID(request, user);
			String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
	
			String listId = ShoppingListTools.createShoppingList(ownerId, requestBody);
			
			if (listId == null) throw new InternalServerErrorException("Failed to create a new list for user " + ownerId);

			ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
			if (shoppingListDetailsWrapper.getShoppingListDetailsMap().containsKey(Constants.RESPONSE_ERROR_KEY)){
				throw new InternalServerErrorException((String) shoppingListDetailsWrapper.getShoppingListDetailsMap().get(Constants.RESPONSE_ERROR_KEY));
			}
			Map<String, Object> returnValue = null;
			if (singleList == null || !singleList){
				try {
					returnValue = ShoppingListTools.getAllUserShoppingListHeaders(ownerId);
				} catch (ServiceException e) {
					throw new PartialSuccessException("List created successfully, but could not read all lists for user " + ownerId, null, null);
				}
				returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsHeader());
			} else {
				returnValue = new HashMap<>();
				returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
				if (returnValue.get(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY) == null){
					throw new InternalServerErrorException("Failed to read list " + listId);
				}
			}
			
			return returnValue;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Reads the specified shopping list and returns it to the user.
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list to read.  This is typically passed in the URL
	 *  
	 * @return	shoppingListDetails	map of the shopping list details
	 */
	public Map<String, Object> readShoppingList(
			HttpServletRequest request,
			String listId,
			User user) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("readShoppingList");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + " triggered.");
			String ownerId = this.getOwnerID(request, user);
			String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
			
			ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
			if (shoppingListDetailsWrapper.getShoppingListDetailsMap().containsKey(Constants.RESPONSE_ERROR_KEY)){
				throw new InternalServerErrorException((String) shoppingListDetailsWrapper.getShoppingListDetailsMap().get(Constants.RESPONSE_ERROR_KEY));
			}
			Map<String, Object> returnValue = new HashMap<>();
			returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
			if (returnValue.get(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY) == null){
				throw new InternalServerErrorException("Failed to read list " + listId);
			}
			return returnValue;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	/**
	 * Updates a Shopping List for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload
	 * 
	 * @return	shoppingListDetails - map of the new shopping list details
	 */
	public Map<String, Object> updateShoppingList(
			HttpServletRequest request,
			Map<String, Object> requestBody,
			String listId,
			Boolean singleList,
			User user) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("updateShoppingList");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			String ownerId = this.getOwnerID(request, user);
			String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
			
			if (ShoppingListTools.updateShoppingList(ownerId, listId, requestBody) == null){
				throw new InternalServerErrorException("Unable to update list with ID " + listId);
			}
			
			ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
			if (shoppingListDetailsWrapper.getShoppingListDetailsMap().containsKey(Constants.RESPONSE_ERROR_KEY)){
				throw new InternalServerErrorException((String) shoppingListDetailsWrapper.getShoppingListDetailsMap().get(Constants.RESPONSE_ERROR_KEY));
			}
			Map<String, Object> returnValue = null;
			if (singleList == null || !singleList){
				try {
					returnValue = ShoppingListTools.getAllUserShoppingListHeaders(ownerId);
				} catch (ServiceException e) {
					throw new PartialSuccessException("List ID " + listId + " updated successfully, but could not read all lists.", null, null);
				}
				returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsHeader());
			} else {
				returnValue = new HashMap<>();
				returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
				if (returnValue.get(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY) == null){
					throw new InternalServerErrorException("Failed to read list " + listId);
				}
			}
			return returnValue;
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	
	/**
	 * Deletes the specified shopping list.
	 * EXAMPLE CALL:  DELETE http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list to deleted.  This is typically passed in the URL
	 *  
	 * @return	success or error message
	 */
	public Map<String, Object> deleteShoppingList(
			HttpServletRequest request,
			String listId,
			User user) throws ServiceException {
		HebTraceContext context = TRACER.startSpan("deleteShoppingList");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			String ownerId = this.getOwnerID(request, user);
			ShoppingListTools.deleteShoppingList(listId, ownerId);
			return this.makeSuccessObject("Successfully Deleted List " + listId);
		} finally {
			TRACER.endSpan(context);
		}
	}
	
	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Product List Item Operations
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Creates new products Item(s) in a shopping list
	 * EXAMPLE CALL:  POST http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [productItems] expected
	 * 
	 * @return	[shoppingListDetails] - map of the shopping list details with new item added.
	 */
	public Map<String, Object> createProductItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// input validation is done in upsertListItems
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Reads all product items for this list
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[productList] will contain all product items in this list.
	 */
	public Map<String, Object> readProductItems(
			String listId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, ownerId, storeId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_PRODUCT_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Reads the specified product item
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to return
	 *  
	 * @return	[productList] will contain a single product item from the list
	 */
	public Map<String, Object> readProductItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + "itemId " + CloudUtil.secureLogMessage(itemId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, ownerId, storeId, itemId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_PRODUCT_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Updates many Product Items at once for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [productList] with one or more items expected.
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateProductItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Updates a specific Product Item for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [productList] with one item expected.
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to update
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateProductItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// ensure that there is only one item and force the ID to be the id in the URL.
		if (requestBody.containsKey(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> productItems = (List<Map<String, Object>>) requestBody.get(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME));
			if (productItems.size() > 1){
				throw new BadRequestException("More than one item was found in the payload body");
			}
			// force overwrite id in payload with URL parameter or add it if it is missing
			productItems.get(0).put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, itemId);
		}
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	
	/**
	 * Deletes the specified product item and removes it from the shopping list.
	 * EXAMPLE CALL:  DELETE http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/products/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list containing the item to deleted.
	 * @param	itemId	The Id of the item to delete
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> deleteProductItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		ListItemTools.deleteListItemDataItem(listId, ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, itemId, ownerId);
		return this.makeSuccessObject("Successfully deleted Item " + itemId);
	}
	

	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Coupon List Items
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Creates new coupons Item(s) in a shopping list
	 * EXAMPLE CALL:  POST http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [couponItems] expected
	 * 
	 * @return	[shoppingListDetails] - map of the shopping list details with new item added.
	 */
	public Map<String, Object> createCouponItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// input validation is done in upsertListItems
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Reads all coupon items for this list
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[couponList] will contain all coupon items in this list.
	 */
	public Map<String, Object> readCouponItems(
			String listId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, ownerId, storeId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_COUPON_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Reads the specified coupon item
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to return
	 *  
	 * @return	[couponList] will contain a single coupon item from the list
	 */
	public Map<String, Object> readCouponItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + "itemId " + CloudUtil.secureLogMessage(itemId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, ownerId, storeId, itemId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_COUPON_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Updates a many Coupon Items at once for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [couponList] with one or more items expected.
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateCouponItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Updates a specific Coupon Item for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [couponList] with one item expected.
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to update
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateCouponItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// ensure that there is only one item and force the ID to be the id in the URL.
		if (requestBody.containsKey(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> productItems = (List<Map<String, Object>>) requestBody.get(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME));
			if (productItems.size() > 1){
				throw new BadRequestException("More than one item was found in the payload body");
			}
			// force overwrite id in payload with URL parameter or add it if it is missing
			productItems.get(0).put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, itemId);
		}
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	
	/**
	 * Deletes the specified coupon item and removes it from the shopping list.
	 * EXAMPLE CALL:  DELETE http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/coupons/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list containing the item to deleted.
	 * @param	itemId	The Id of the item to delete
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> deleteCouponItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		ListItemTools.deleteListItemDataItem(listId, ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME, itemId, ownerId);
		return this.makeSuccessObject("Successfully deleted Item " + itemId);
	}
	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Recipe List Items
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Creates new recipes Item(s) in a shopping list
	 * EXAMPLE CALL:  POST http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [recipeItems] expected
	 * 
	 * @return	[shoppingListDetails] - map of the shopping list details with new item added.
	 */
	public Map<String, Object> createRecipeItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// input validation is done in upsertListItems
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Reads all recipe items for this list
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[recipeList] will contain all recipe items in this list.
	 */
	public Map<String, Object> readRecipeItems(
			String listId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);

		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, ownerId, storeId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_RECIPE_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Reads the specified recipe item
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to return
	 *  
	 * @return	[recipeList] will contain a single recipe item from the list
	 */
	public Map<String, Object> readRecipeItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + "itemId " + CloudUtil.secureLogMessage(itemId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);

		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, ownerId, storeId, itemId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_RECIPE_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Updates a many Recipe Items at once for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [recipeList] with one or more items expected.
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateRecipeItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Updates a specific Recipe Item for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [recipeList] with one item expected.
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to update
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateRecipeItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// ensure that there is only one item and force the ID to be the id in the URL.
		if (requestBody.containsKey(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> productItems = (List<Map<String, Object>>) requestBody.get(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME));
			if (productItems.size() > 1){
				throw new BadRequestException("More than one item was found in the payload body");
			}
			// force overwrite id in payload with URL parameter or add it if it is missing
			productItems.get(0).put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, itemId);
		}
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	
	/**
	 * Deletes the specified recipe item and removes it from the shopping list.
	 * EXAMPLE CALL:  DELETE http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/recipes/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list containing the item to deleted.
	 * @param	itemId	The Id of the item to delete
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> deleteRecipeItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		ListItemTools.deleteListItemDataItem(listId, ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, itemId, ownerId);
		return this.makeSuccessObject("Successfully deleted Item " + itemId);
	}
	
	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Freeform List Items
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
	
	/**
	 * Creates new freeforms Item(s) in a shopping list
	 * EXAMPLE CALL:  POST http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [freeformItems] expected
	 * 
	 * @return	[shoppingListDetails] - map of the shopping list details with new item added.
	 */
	public Map<String, Object> createFreeformItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// input validation is done in upsertListItems
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Reads all freeform items for this list
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[freeformList] will contain all freeform items in this list.
	 */
	public Map<String, Object> readFreeformItems(
			String listId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);

		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, ownerId, storeId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_FREEFORM_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Reads the specified freeform item
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to return
	 *  
	 * @return	[freeformList] will contain a single freeform item from the list
	 */
	public Map<String, Object> readFreeformItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": listId " + CloudUtil.secureLogMessage(listId) + "itemId " + CloudUtil.secureLogMessage(itemId) + " triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);

		Map<String, Object> itemList = new ItemListWrapper(listId, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, ownerId, storeId, itemId).getItemList();
		if (itemList.containsKey(Constants.RESPONSE_ERROR_KEY)){
			throw new InternalServerErrorException((String) itemList.get(Constants.RESPONSE_ERROR_KEY));
		}
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_FREEFORM_LIST_KEY, itemList);
		return returnValue;
	}
	
	/**
	 * Updates a many Freeform Items at once for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [freeformList] with one or more items expected.
	 * @param	listId	The Id of the list containing the item to return
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateFreeformItems(
			String listId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	/**
	 * Updates a specific Freeform Item for the user
	 * EXAMPLE CALL:  PUT http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBody - a linked hash map containing the body payload.  [freeformList] with one item expected.
	 * @param	listId	The Id of the list containing the item to return
	 * @param	itemId	The Id of the item to update
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> updateFreeformItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			Map<String, Object> requestBody,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		String storeId = request.getHeader(ShoppingListConstants.HEADER_STORE_ID);
		
		// ensure that there is only one item and force the ID to be the id in the URL.
		if (requestBody.containsKey(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> productItems = (List<Map<String, Object>>) requestBody.get(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME));
			if (productItems.size() > 1){
				throw new BadRequestException("More than one item was found in the payload body");
			}
			// force overwrite id in payload with URL parameter or add it if it is missing
			productItems.get(0).put(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_ID, itemId);
		}
		
		List<Map<String, Object>> upsertItemsResult = ListItemTools.upsertListItems(requestBody, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, listId, ownerId);
		ShoppingListDetailsWrapper shoppingListDetailsWrapper = new ShoppingListDetailsWrapper(listId, ownerId, storeId);
		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put(ShoppingListConstants.RESPONSE_SHOPPING_LIST_KEY, shoppingListDetailsWrapper.getShoppingListDetailsMap());
		returnValue.put(ShoppingListConstants.RESPONSE_UPSERT_ITEMS_RESULT_KEY, upsertItemsResult);
		return returnValue;
	}
	
	
	/**
	 * Deletes the specified freeform item and removes it from the shopping list.
	 * EXAMPLE CALL:  DELETE http://localhost:8080/_ah/api/shoppinglist/v1/shoppingLists/sl1/freeforms/slp1
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	listId	The list containing the item to deleted.
	 * @param	itemId	The Id of the item to delete
	 *  
	 * @return	[shoppingListDetails]
	 */
	public Map<String, Object> deleteFreeformItem(
			String listId,
			String itemId,
			HttpServletRequest request,
			User user) throws ServiceException {
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		String ownerId = this.getOwnerID(request, user);
		ListItemTools.deleteListItemDataItem(listId, ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, itemId, ownerId);
		return this.makeSuccessObject("Successfully deleted Item " + itemId);
	}
	
	
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----
//  Helper Methods
//  ----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----====----

	/**
	 * Gets the ownerId from the request.  This will use the user from the oAuth request first, but uses a header key named "ownerId" if it is not available
	 * Using the header key can be turned on or off by using the setting ConfigurationShoppingListConstants.ALLOW_HEADER_OWNER_ID
	 * @param request
	 * @param user
	 * @return
	 * @throws OAuthRequestException
	 */
	public String getOwnerID (HttpServletRequest request, User user) throws OAuthRequestException{
		String ownerId = null;
		if (user != null) {
			ownerId = user.getId();
		} else if (!StringUtils.isBlank(request.getHeader(ShoppingListConstants.HEADER_OWNER_ID)) && ConfigurationConstants.ALLOW_HEADER_OWNER_ID) {
			ownerId = request.getHeader(ShoppingListConstants.HEADER_OWNER_ID);
			logger.warning("Unauthorized user is accessing the shopping list.  Using a manually entered ownerID: " + CloudUtil.secureLogMessage(ownerId));
		} else { 
			throw new OAuthRequestException("Must have a authorized user or manually submit the ownerId. It cannot be blank or null when requesting authorized REST endpoints");
		}
		return ownerId;
	}

	/**
	 * @deprecated Only remains to support original Shopping List POC
	 * Make a returnable SUCCESS object
	 * @param successMessage
	 * @return
	 */
	private Map<String, Object> makeSuccessObject(String successMessage){
		if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
		Map<String, Object> successMessageObject = new HashMap<String, Object>();
		
		successMessageObject.put(Constants.RESPONSE_SUCCESS_KEY, successMessage);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(CloudUtil.getMethodName() + ".successMessage: " + CloudUtil.secureLogMessage(successMessage));
		}
		return successMessageObject;
	}
}
