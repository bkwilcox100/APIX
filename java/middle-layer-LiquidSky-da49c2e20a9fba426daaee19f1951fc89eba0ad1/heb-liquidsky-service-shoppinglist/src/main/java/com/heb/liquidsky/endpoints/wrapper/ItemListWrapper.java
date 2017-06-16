package com.heb.liquidsky.endpoints.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.endpoints.tools.ShoppingListTools;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This a list of ItemDetailsWrapper items
 * This wraps a list of ItemDetailsWrapper items in a list for a meaningful json return to the requesting endpoints.
 * @author Scott McArthur
 *
 */
public class ItemListWrapper{
	private static final Logger logger = Logger.getLogger(ItemListWrapper.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ItemListWrapper.class);
	
    private Map<String, Object> itemList;
    private final DataItem shoppingListDataItem;
    private final String itemType;	// type of items to be returned
    private final String ownerId;		// this is the owner Id from the request and is used to validate against the given list. 
    private String storeId;		// from the request.  used to determine availability
    private String siteId;		// used to determine the product data to use
    private String itemId;		// used when making a list of a single item.

    /**
     * Makes an item list based on items of a type in the specified list.
     * 
     * @param listId
     * @param itemType
     * @param ownerId
     * @param storeId
     */
	public ItemListWrapper(String listId, String itemType, String ownerId, String storeId) throws ServiceException {
		DataItem shoppingList = ShoppingListTools.readShoppingList(listId);
		if (shoppingList == null) {
			throw new NotFoundException("No shopping list exists with ID " + listId);
		}
		this.shoppingListDataItem = shoppingList;
		if (StringUtils.isBlank(itemType)) {
			throw new BadRequestException("Item type cannot be empty");
		}
		this.itemType = itemType;
		if (StringUtils.isBlank(ownerId)) {
			throw new BadRequestException("Owner ID cannot be empty");
		}
		this.ownerId = ownerId;
		this.setStoreId(storeId);
	}
	
	/**
	 * Makes an item list based on items of a type in the specified list.
	 * This is the preferred constructor as it is more efficient than looking up the data item again.
	 * 
	 * @param shoppingListDataItem
	 * @param itemType
	 * @param ownerId
	 * @param storeId
	 */
	public ItemListWrapper(DataItem shoppingList, String itemType, String ownerId, String storeId) throws BadRequestException {
		if (shoppingList == null) {
			throw new BadRequestException("Shopping list cannot be null");
		}
		this.shoppingListDataItem = shoppingList;
		if (StringUtils.isBlank(itemType)) {
			throw new BadRequestException("Item type cannot be empty");
		}
		this.itemType = itemType;
		if (StringUtils.isBlank(ownerId)) {
			throw new BadRequestException("Owner ID cannot be empty");
		}
		this.ownerId = ownerId;
		this.setStoreId(storeId);
	}
	
	/**
	 * Used for making a list of a single item specified by itemId
	 * 
	 * @param listId
	 * @param type
	 * @param ownerId
	 * @param storeId
	 * @param itemId
	 */
	public ItemListWrapper(String listId, String itemType, String ownerId, String storeId, String itemId) throws ServiceException {
		DataItem shoppingList = ShoppingListTools.readShoppingList(listId);
		if (shoppingList == null) {
			throw new NotFoundException("No shopping list exists with ID " + listId);
		}
		this.shoppingListDataItem = shoppingList;
		if (StringUtils.isBlank(itemType)) {
			throw new BadRequestException("Item type cannot be empty");
		}
		this.itemType = itemType;
		if (StringUtils.isBlank(ownerId)) {
			throw new BadRequestException("Owner ID cannot be empty");
		}
		this.ownerId = ownerId;
		this.setStoreId(storeId);
		this.setItemId(itemId);
	}

	/**
	 * This is the main point of this class.  This creates the formatted list of items.
	 * @return
	 */
	public Map<String, Object> getItemList() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getItemList");
		try {
			// If itemList is already set, then just return it 
			if (this.itemList == null){
				this.initializeItemList();
			}
			return this.itemList;
		} finally {
			TRACER.endSpan(context);
		}
	}

	private void initializeItemList() throws ServiceException {
		// Validate that the list is owned by the owner passed in to the endpoint from oAuth or header
		ShoppingListTools.validateOwner(this.getShoppingListDataItem(), this.getOwnerId());
		List<DataItem> dataItemList = null; // holds the list of all DataItems of the specified type from the given list
		// Get the list of items of the given type from the shopping list
		try {
			dataItemList = this.getShoppingListDataItem().getList(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(this.getItemType()));
			if (dataItemList == null){
				throw new NotFoundException("Error reading items of type " + this.getItemType() + " from List with Id " + this.getListId());
			}
		} catch (DataStoreException e) {
			logger.log(Level.SEVERE, "Failure while trying to read item " + this.getItemId() + " of type " + this.getItemType(), e);
			throw new InternalServerErrorException("Error getting Item List of type " + this.getItemType() + " from Shopping List with Id " + this.getListId());
		}
		// Fill itemDetailsWrapperList with wrapped data items
		List<Object> itemDetailsWrapperList = new ArrayList<>(); // this will be the list of items that will be added to itemList along with the item type properties.
		if (!StringUtils.isBlank(this.getItemId())){
			// make a list of a single item
			// lookup the item
			DataItem singleItemDataItem = null;
			try {
				singleItemDataItem = DataStore.getInstance().readItemImmutable(this.getItemId(), this.getItemType());
				if (singleItemDataItem == null){
					throw new NotFoundException("Item " + this.getItemId() + " of type " + this.getItemType() + " Could not be found");
				}
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, "Failure while trying to read item " + this.getItemId() + " of type " + this.getItemType(), e);
				throw new InternalServerErrorException("Exception while trying to read item " + this.getItemId() + " of type " + this.getItemType());
			}
			// validate that it is contained in the given list.
			if (!dataItemList.contains(singleItemDataItem)){
				throw new BadRequestException("Item " + this.getItemId() + " does not belong to list " + this.getListId());
			}
			// add this one item to the wrapper list to be returned alone
			ItemDetailsWrapper itemDetails = new ItemDetailsWrapper(singleItemDataItem, this.getOwnerId(), this.getSiteId(), this.getStoreId());
			itemDetailsWrapperList.add(itemDetails.getItemDetailsMap());
		} else {
			// make the list of all items
			for (DataItem currentItem : dataItemList) {
				ItemDetailsWrapper itemDetails = new ItemDetailsWrapper(currentItem, this.getOwnerId(), this.getSiteId(), this.storeId);
				itemDetailsWrapperList.add(itemDetails.getItemDetailsMap());
			}
		}
		// Now we can build the itemList
		this.itemList = new HashMap<String, Object>();
		// This adds the properties section for the given type.
		// TODO: the properties should be database driven
		switch (this.getItemType()) {
		case ShoppingListConstants.SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME:
			this.itemList.put("productListProperties", ShoppingListConstants.PRODUCT_ITEM_LIST_PROPERTIES);
			break;
		case ShoppingListConstants.SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME:
			this.itemList.put("freeformListProperties", ShoppingListConstants.FREEFORM_ITEM_LIST_PROPERTIES);
			break;
		case ShoppingListConstants.SHOPPING_LIST_COUPON_DATA_ITEM_NAME:
			this.itemList.put("couponListProperties", ShoppingListConstants.COUPON_ITEM_LIST_PROPERTIES);
			break;
		case ShoppingListConstants.SHOPPING_LIST_RECIPE_DATA_ITEM_NAME:
			this.itemList.put("recipeListProperties", ShoppingListConstants.RECIPE_ITEM_LIST_PROPERTIES);
			break;
		}
		this.itemList.put(ShoppingListConstants.LIST_ITEM_TYPE_MAP.get(this.getItemType()), itemDetailsWrapperList);
	}

	/**
	 * gets the shopping list that we are interested in.
	 * @return
	 */
	private DataItem getShoppingListDataItem() {
		return this.shoppingListDataItem;
	}

	/**
	 * siteId has to be passed in the constructor, there is no way to look it up, but we can set the default
	 * @return
	 */
	private String getSiteId() {
		if (StringUtils.isBlank(this.siteId)){
			this.setSiteId(String.valueOf(ShoppingListConstants.SITE_ID_DEFAULT));
		}
		return siteId;
	}
	private void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * Item type is required to be passed in the constructor
	 * @return
	 */
	private String getItemType() {
		return itemType;
	}

	private String getListId() {
		return this.getShoppingListDataItem().getId();
	}

	/**
	 * gets the owner Id.  This should only be from the constructor and contain what is passed in from the endpoint request to ensure validation.
	 * @return
	 */
	private String getOwnerId() {
		return ownerId;
	}

	/**
	 * Only available from the constructor as it is a request item and never stored.
	 * @return
	 */
	private String getStoreId() {
		return storeId;
	}
	private void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	/**
	 * If this is set then we are making a list of a single item.
	 * @return
	 */
	private String getItemId() {
		return itemId;
	}
	private void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
