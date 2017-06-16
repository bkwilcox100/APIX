package com.heb.liquidsky.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the client IDs and scopes for allowed clients consuming the API.
 */
public class ShoppingListConstants {
	// Set Max Limits
	public static final int SHOPPING_LIST_MAX_LISTS = 100;
	public static final int SHOPPING_LIST_MAX_LIST_ITEMS = 1000;
	public static final int MAX_MAP_DEPTH = 99;
	
	// Header Item Names
	public static final String HEADER_OWNER_ID = "ownerId";
	public static final String HEADER_STORE_ID = "storeId";
	
	// Database Access Names
	public static final String SHOPPING_LIST_DATA_ITEM_NAME = "shoppinglist";
	public static final String SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME = "productItem";
	public static final String SHOPPING_LIST_COUPON_DATA_ITEM_NAME = "couponItem";
	public static final String SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME = "freeformItem";
	public static final String SHOPPING_LIST_RECIPE_DATA_ITEM_NAME = "recipeItem";
	public static final String ATG_PRODUCT_ITEM_NAME = "atgProduct";
	public static final String ATG_ASSORTMENT_ITEM_NAME = "atgAssortment";
	public static final String ATG_STORE_ITEM_NAME = "atgStore";
	public static final String ATG_RECIPE_ITEM_NAME = "atgRecipe";
	public static final String NAMED_QUERY_SHOPPING_LIST_BY_OWNER = "shopping_list_by_owner";
	
	// data-store properties for Shopping Lists
	public static final String PROPERTY_NAME_LIST_ID = "id";
	public static final String PROPERTY_NAME_SHOPPING_LIST_SITE_ID = "siteId";
	public static final String PROPERTY_NAME_SHOPPING_LIST_OWNER_ID = "ownerId";
	public static final String PROPERTY_NAME_SHOPPING_LIST_LIST_NAME = "listName";
	public static final String PROPERTY_NAME_SHOPPING_LIST_PRODUCT_ITEMS = "productItems";
	public static final String PROPERTY_NAME_SHOPPING_LIST_FREEFORM_ITEMS = "freeformItems";
	
	// data-store properties common to all list item types
	public static final String PROPERTY_NAME_LIST_ITEM_ID = "id";
	public static final String PROPERTY_NAME_LIST_ITEM_PARENT_SHOPPING_LIST = "parentShoppingList";
	public static final String PROPERTY_NAME_LIST_ITEM_STATUS = "status";
	public static final String PROPERTY_NAME_LIST_ITEM_QUANTITY = "quantity";
	public static final String PROPERTY_NAME_LIST_ITEM_NOTES = "notes";
	
	// data-store properties for external ID's (name for freeform is used to detect duplicates, not external data)
	public static final String PROPERTY_NAME_LIST_ITEM_PRODUCT_ID = "productId";
	public static final String PROPERTY_NAME_LIST_ITEM_RECIPE_ID = "recipeId";
	public static final String PROPERTY_NAME_LIST_ITEM_COUPON_ID = "couponId";
	public static final String PROPERTY_NAME_LIST_ITEM_FREEFORM_ID = "freeformName";
	
	//  These define expected keys in response messages
	public static final String RESPONSE_SHOPPING_LIST_KEY = "shoppingListDetails";
	public static final String RESPONSE_UPSERT_ITEMS_RESULT_KEY = "upsertItemsResult";
	public static final String RESPONSE_UPSERT_LIST_RESULT_KEY = "upsertListResult";
	public static final String RESPONSE_PRODUCT_LIST_KEY = "productList";
	public static final String RESPONSE_COUPON_LIST_KEY = "couponList";
	public static final String RESPONSE_RECIPE_LIST_KEY = "recipeList";
	public static final String RESPONSE_FREEFORM_LIST_KEY = "freeformList";
	
	//  Defines the Site ID for the site that is using the list service.  
	//  Currently, this is only used to determine the data set to use for external data like product details.
	public static final int SITE_ID_HEB_ATG = 0;
	public static final int SITE_ID_HEB_API_HUB = 1;
	public static final int SITE_ID_CENTRAL_MARKET = 2;
	public static final long SITE_ID_DEFAULT = 0l;

	//  This is a list of all possible DataItem types that can be added to a shopping list
	public static final List<String> SHOPPING_LIST_ITEM_DATA_TYPE_LIST = new ArrayList<String>();
	static {
		SHOPPING_LIST_ITEM_DATA_TYPE_LIST.add(SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME);
		SHOPPING_LIST_ITEM_DATA_TYPE_LIST.add(SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME);
		SHOPPING_LIST_ITEM_DATA_TYPE_LIST.add(SHOPPING_LIST_COUPON_DATA_ITEM_NAME);
		SHOPPING_LIST_ITEM_DATA_TYPE_LIST.add(SHOPPING_LIST_RECIPE_DATA_ITEM_NAME);
	}
	
	//  this maps the item data type to the list name of the parent (i.e. items of this type belong to this list)
	public static final Map <String, String> LIST_ITEM_TYPE_MAP = new HashMap<String, String>();
	static {
		LIST_ITEM_TYPE_MAP.put(SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, "productItems");
		LIST_ITEM_TYPE_MAP.put(SHOPPING_LIST_COUPON_DATA_ITEM_NAME, "couponItems");
		LIST_ITEM_TYPE_MAP.put(SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, "freeformItems");
		LIST_ITEM_TYPE_MAP.put(SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, "recipeItems");
	}
	
	//  this maps items DataItem types to the key that they are held in a request 
	public static final Map <String, String> REQUEST_ITEM_TYPE_KEY_MAP = new HashMap<String, String>();
	static {
		REQUEST_ITEM_TYPE_KEY_MAP.put(SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, "productList");
		REQUEST_ITEM_TYPE_KEY_MAP.put(SHOPPING_LIST_COUPON_DATA_ITEM_NAME, "couponList");
		REQUEST_ITEM_TYPE_KEY_MAP.put(SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, "freeformList");
		REQUEST_ITEM_TYPE_KEY_MAP.put(SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, "recipeList");
	}
	
	// This map contains an ordered list of items to be returned in a wrappered response.  
	// The key is what should be sent and the value is the key for where the data is retrieved.
	// A blank key means that the item is calculated or retrieved from another system and not a 1:1 mapping to the DataItem.
	public static final Map <String, String> LIST_TRANSFORM_MAP = new HashMap<String, String>();
	static {
		LIST_TRANSFORM_MAP.put("id", "id");
		LIST_TRANSFORM_MAP.put("ownerId", "ownerId");
		LIST_TRANSFORM_MAP.put("siteId", "siteId");
		LIST_TRANSFORM_MAP.put("storeId", "");
		LIST_TRANSFORM_MAP.put("listName", "listName");
		LIST_TRANSFORM_MAP.put("listItemCount", "listItemCount");
		LIST_TRANSFORM_MAP.put("creationDate", "creationDate");
		LIST_TRANSFORM_MAP.put("lastModifiedDate", "lastModifiedDate");
		LIST_TRANSFORM_MAP.put("shoppingListItems", "");
	}
	
	public static final Map <String, String> PRODUCT_ITEM_TRANSFORM_MAP = new HashMap<String, String>();
	static {
		PRODUCT_ITEM_TRANSFORM_MAP.put("id", "id");
		PRODUCT_ITEM_TRANSFORM_MAP.put("productId", "productId");
		PRODUCT_ITEM_TRANSFORM_MAP.put("name", "name");
		PRODUCT_ITEM_TRANSFORM_MAP.put("location", "");
		PRODUCT_ITEM_TRANSFORM_MAP.put("quantity", "quantity");
		PRODUCT_ITEM_TRANSFORM_MAP.put("notes", "notes");
		PRODUCT_ITEM_TRANSFORM_MAP.put("status", "status");
		PRODUCT_ITEM_TRANSFORM_MAP.put("avaliablity", "");
		PRODUCT_ITEM_TRANSFORM_MAP.put("sceneSevenImageId", "imageId");
		PRODUCT_ITEM_TRANSFORM_MAP.put("creationDate", "creationDate");
	}
	
	public static final Map <String, String> COUPON_ITEM_TRANSFORM_MAP = new HashMap<String, String>();
	static {
		COUPON_ITEM_TRANSFORM_MAP.put("id", "id");
		COUPON_ITEM_TRANSFORM_MAP.put("couponId", "couponId");
		COUPON_ITEM_TRANSFORM_MAP.put("name", "");
		COUPON_ITEM_TRANSFORM_MAP.put("status", "status");
		COUPON_ITEM_TRANSFORM_MAP.put("notes", "notes");
		COUPON_ITEM_TRANSFORM_MAP.put("restrictions", "");
		COUPON_ITEM_TRANSFORM_MAP.put("sceneSevenImageId", "");
		COUPON_ITEM_TRANSFORM_MAP.put("couponExpirationDate", "");
		COUPON_ITEM_TRANSFORM_MAP.put("couponStartDate", "");
		COUPON_ITEM_TRANSFORM_MAP.put("creationDate", "creationDate");
	}
	
	public static final Map <String, String> RECIPE_ITEM_TRANSFORM_MAP = new HashMap<String, String>();
	static {
		RECIPE_ITEM_TRANSFORM_MAP.put("id", "id");
		RECIPE_ITEM_TRANSFORM_MAP.put("recipeId", "recipeId");
		RECIPE_ITEM_TRANSFORM_MAP.put("name", "name");
		RECIPE_ITEM_TRANSFORM_MAP.put("status", "status");
		RECIPE_ITEM_TRANSFORM_MAP.put("notes", "notes");
		RECIPE_ITEM_TRANSFORM_MAP.put("sceneSevenImageId", "imageId");
		RECIPE_ITEM_TRANSFORM_MAP.put("creationDate", "creationDate");
	}

	public static final Map <String, String> FREEFORM_ITEM_TRANSFORM_MAP = new HashMap<String, String>();
	static {
		FREEFORM_ITEM_TRANSFORM_MAP.put("id", "id");
		FREEFORM_ITEM_TRANSFORM_MAP.put("freeformName", "freeformName");
		FREEFORM_ITEM_TRANSFORM_MAP.put("quantity", "quantity");
		FREEFORM_ITEM_TRANSFORM_MAP.put("status", "status");
		FREEFORM_ITEM_TRANSFORM_MAP.put("notes", "notes");
		FREEFORM_ITEM_TRANSFORM_MAP.put("creationDate", "creationDate");
	}
	
	// Used to get the proper Transform map given the item type of the DataItem (See ItemList.java for use)
	public static final Map <String, Map<String, String>> DATA_ITEM_NAME_TO_TRANSFORM_MAP = new HashMap<String, Map<String, String>>();
	static {
		DATA_ITEM_NAME_TO_TRANSFORM_MAP.put(SHOPPING_LIST_PRODUCT_DATA_ITEM_NAME, PRODUCT_ITEM_TRANSFORM_MAP);
		DATA_ITEM_NAME_TO_TRANSFORM_MAP.put(SHOPPING_LIST_COUPON_DATA_ITEM_NAME, COUPON_ITEM_TRANSFORM_MAP);
		DATA_ITEM_NAME_TO_TRANSFORM_MAP.put(SHOPPING_LIST_FREEFORM_DATA_ITEM_NAME, FREEFORM_ITEM_TRANSFORM_MAP);
		DATA_ITEM_NAME_TO_TRANSFORM_MAP.put(SHOPPING_LIST_RECIPE_DATA_ITEM_NAME, RECIPE_ITEM_TRANSFORM_MAP);
	}

	
	//  --==-- Temporary stuff
	
	//  This map contains properties to be returned with the list of product items.
	//  This will eventually be moved to the database to allow for dynamic business configuration, but is just static for now. 
	public static final Map <String, String> PRODUCT_ITEM_LIST_PROPERTIES = new HashMap<String, String>();
	static {
		PRODUCT_ITEM_LIST_PROPERTIES.put("headerText", "Products in list");
	}
	public static final Map <String, String> COUPON_ITEM_LIST_PROPERTIES = new HashMap<String, String>();
	static {
		COUPON_ITEM_LIST_PROPERTIES.put("headerText", "Coupons in list");
	}
	public static final Map <String, String> FREEFORM_ITEM_LIST_PROPERTIES = new HashMap<String, String>();
	static {
		FREEFORM_ITEM_LIST_PROPERTIES.put("headerText", "Other Items in list");
	}
	public static final Map <String, String> RECIPE_ITEM_LIST_PROPERTIES = new HashMap<String, String>();
	static {
		RECIPE_ITEM_LIST_PROPERTIES.put("headerText", "Recipes in list");
	}
	public static final Map <String, String> SHOPPING_LIST_PROPERTIES = new HashMap<String, String>();
	static {
		SHOPPING_LIST_PROPERTIES.put("Title", "Shopping List");
	}
	
	// contains a list of item properties that cannot be changed on update.
	public static final List<String> SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES = new ArrayList<String>();
	static {
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("id");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("parentShoppingList");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("productId");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("couponId");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("recipeId");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("siteId");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("lastModifiedDate");
		SHOPPING_LIST_ITEM_NON_UPDATABLE_PROPERTIES.add("creationDate");
	}	
}

