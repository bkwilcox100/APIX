package com.heb.liquidsky.pubsub.data;

import com.heb.liquidsky.pubsub.GooglePubSubImpl;

/**
 * Data contract for sharing shopping list data via pub/sub.
 */
public class ShoppingListData extends PubSubDataImpl {

	private static final long serialVersionUID = 1l;
	public static final String DATA_TYPE_SHOPPING_LIST = "shoppinglist";

	private final String listName;

	public ShoppingListData(GooglePubSubImpl pubsub, String id, PUBSUB_ACTION action, String listName) {
		super(pubsub, DATA_TYPE_SHOPPING_LIST, id, action);
		this.listName = listName;
	}

	public ShoppingListData(ShoppingListData data) {
		super(data);
		this.listName = data.getListName();
	}

	public String getListName() {
		return this.listName;
	}
}
