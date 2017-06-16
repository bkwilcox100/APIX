package com.heb.liquidsky.data.custom;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;

/**
 * This custom property gets the image ID for the product from an external table depending on which siteId the list is for.
 * @author Scott McArthur
 */
public class ShoppingListProductItemImageId extends AbstractCustomPropertyDescriptor {
	private static final Logger logger = Logger.getLogger(ShoppingListProductItemImageId.class.getName());
	private static final String ATTR_DEFAULT_VALUE = "defaultValue";

	public ShoppingListProductItemImageId(Map<String, String> attributes) throws InstantiationException {
		super(attributes);
		if (attributes == null || !attributes.containsKey(ATTR_DEFAULT_VALUE)) {
			throw new InstantiationException("ShoppingListProductItemImageId: must specify a " + ATTR_DEFAULT_VALUE + " attribute");
		}
	}

	@Override
	public String getPropertyValue(DataItem dataItem) throws DataStoreException {
		String returnValue = this.getAttributeValue(ATTR_DEFAULT_VALUE);
		String listId = null;
		String productId = null;
		
		try {
			listId = dataItem.getItem(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PARENT_SHOPPING_LIST).getId();
			productId = dataItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PRODUCT_ID);
		} catch (DataStoreException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Data layer failure", e);
			}
			// Just return the default if things go wrong
			return returnValue;
		}
		
		// get the site_id from the parent list
		DataItem parentListDataItem = DataStore.getInstance().readItemImmutable(listId, ShoppingListConstants.SHOPPING_LIST_DATA_ITEM_NAME);
		Long siteId = null;
		if (parentListDataItem != null){
			try {
				siteId = parentListDataItem.getLong(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_SITE_ID);
			} catch (DataStoreException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Data layer failure", e);
				}
			}
			if (siteId == null) {
				siteId = (long) ShoppingListConstants.SITE_ID_DEFAULT;
			}
		}
		
		if (productId != null) {
			returnValue = this.lookupProductImage(productId, siteId.intValue());
		}
		return returnValue;
	}

	private String lookupProductImage(String productId, int siteId){
		// siteId determines which product data source is used (atg, api hub, or central market)
		switch (siteId) {
			case ShoppingListConstants.SITE_ID_HEB_API_HUB:
				break;
			case ShoppingListConstants.SITE_ID_CENTRAL_MARKET:
				break;
			case ShoppingListConstants.SITE_ID_HEB_ATG:
				try {
					DataItem atgProductDataItem = DataStore.getInstance().readItemImmutable(productId, ShoppingListConstants.ATG_PRODUCT_ITEM_NAME);
					if (atgProductDataItem != null){
						return atgProductDataItem.getString("sceneSevenImage");
					}
				} catch (DataStoreException e) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.log(Level.SEVERE, "Data layer failure", e);
					}
				}
				break;
			default:
		}
		return this.getAttributeValue(ATTR_DEFAULT_VALUE);
	}
}
