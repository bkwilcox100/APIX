package com.heb.liquidsky.data.custom;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;

/**
 * This custom property gets the name for the product from an external table depending on which siteId the list is for.
 * @author Scott McArthur
 */
public class ShoppingListProductItemName extends AbstractCustomPropertyDescriptor {
	private static final Logger logger = Logger.getLogger(ShoppingListProductItemName.class.getName());
	private static final String ATTR_DEFAULT_VALUE = "defaultValue";

	public ShoppingListProductItemName(Map<String, String> attributes) throws InstantiationException {
		super(attributes);
		if (attributes == null || !attributes.containsKey(ATTR_DEFAULT_VALUE)) {
			throw new InstantiationException("ShoppingListProductItemName: must specify a " + ATTR_DEFAULT_VALUE + " attribute");
		}
	}

	@Override
	public String getPropertyValue(DataItem dataItem) {
		String productName = this.getAttributeValue(ATTR_DEFAULT_VALUE);
		String productId = null;
		DataItem parentListDataItem = null;
		
		try {
			productId = dataItem.getString(ShoppingListConstants.PROPERTY_NAME_LIST_ITEM_PRODUCT_ID);
			parentListDataItem = dataItem.getItem("parentShoppingList");
		} catch (DataStoreException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Data layer failure", e);
			}
			// Just return the default if things go wrong
			return productName;
		}
		
		// get the site_id from the parent list
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
		} else {
			productName = "NO PARENT";
		}
		if (productId != null && siteId != null) {
			productName = this.lookupProductTitle(productId, siteId.intValue());
		}
		return productName;
	}

	private String lookupProductTitle(String productId, int siteId){
		switch (siteId) {  // this will be used to determine which product data source is used (atg, api hub, or central market)
			case ShoppingListConstants.SITE_ID_HEB_API_HUB:   // api hub data
				break;
			case ShoppingListConstants.SITE_ID_CENTRAL_MARKET:   // Central Market Data
				break;
			case ShoppingListConstants.SITE_ID_HEB_ATG:  // atg based heb.com data
				try {
					DataItem atgProductDataItem = DataStore.getInstance().readItemImmutable(productId, ShoppingListConstants.ATG_PRODUCT_ITEM_NAME);
					if (atgProductDataItem != null){
						return atgProductDataItem.getString("displayName");
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
