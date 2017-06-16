package com.heb.liquidsky.data;

import java.util.List;
import java.util.logging.Level;

import org.junit.Test;

import com.heb.liquidsky.test.HEBTestCase;

public class ShoppingListTest extends HEBTestCase {

	private static final String DATA_TYPE_PRODUCT_LIST_ITEM = "productItem";
	private static final String DATA_TYPE_SHOPPING_LIST = "shoppinglist";
	private static final String PROP_LIST_NAME = "listName";
	private static final String PROP_OWNER_ID = "ownerId";
	private static final String PROP_PARENT_SHOPPING_LIST = "parentShoppingList";
	private static final String PROP_PRODUCT_ID = "productId";
	private static final String PROP_SHOPPING_LIST_PRODUCT_ITEMS = "productItems";
	private static final String TEST_SHOPPING_LIST_ID_1 = "test-shopping-list-1";
	private static final String TEST_SHOPPING_LIST_ID_2 = "test-shopping-list-2";
	private static final String TEST_LIST_PRODUCT_ID_1 = "test-shopping-list-product-1";
	private static final String TEST_LIST_PRODUCT_ID_2 = "test-shopping-list-product-2";
	private static final String TEST_LIST_PRODUCT_ID_3 = "test-shopping-list-product-3";

	private void purgeTestItem(String id, String dataType) {
		// delete the data item first since the deletion will
		// attempt to publish an event notifying subscribers about
		// the deletion
		try {
			MutableDataItem mutableDataItem = DataStore.getInstance().readItemForUpdate(id, dataType);
			if (mutableDataItem != null) {
				DataStore.getInstance().deleteItem(mutableDataItem);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failure while deleting test " + dataType + " with ID " + id, e);
		}
	}

	@Test
	public void testShoppingList() {
		try {
			// create empty shopping lists
			MutableDataItem mutableShoppingList1 = DataStore.getInstance().createItem(TEST_SHOPPING_LIST_ID_1, DATA_TYPE_SHOPPING_LIST);
			mutableShoppingList1.setProperty(PROP_OWNER_ID, "test123");
			mutableShoppingList1.setProperty(PROP_LIST_NAME, "first list");
			DataStore.getInstance().insertItem(mutableShoppingList1);
			List<DataItem> shoppingLists = DataStore.getInstance().executeNamedQuery(DATA_TYPE_SHOPPING_LIST, "shopping_list_by_owner", "test123");
			assertEquals("Named query shopping_list_by_owner should return one item", 1, shoppingLists.size());
			MutableDataItem mutableShoppingList2 = DataStore.getInstance().createItem(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
			mutableShoppingList2.setProperty(PROP_OWNER_ID, "test123");
			mutableShoppingList2.setProperty(PROP_LIST_NAME, "second list");
			DataStore.getInstance().insertItem(mutableShoppingList2);
			// verify named query works
			shoppingLists = DataStore.getInstance().executeNamedQuery(DATA_TYPE_SHOPPING_LIST, "shopping_list_by_owner", "test123");
			assertEquals("Named query shopping_list_by_owner should return two items", 2, shoppingLists.size());
			// add items to the shopping lists
			MutableDataItem mutableProductItem1 = DataStore.getInstance().createItem(TEST_LIST_PRODUCT_ID_1, DATA_TYPE_PRODUCT_LIST_ITEM);
			mutableProductItem1.setProperty(PROP_PARENT_SHOPPING_LIST, mutableShoppingList1);
			mutableProductItem1.setProperty(PROP_PRODUCT_ID, "test-123");
			DataStore.getInstance().insertItem(mutableProductItem1);
			MutableDataItem mutableProductItem2 = DataStore.getInstance().createItem(TEST_LIST_PRODUCT_ID_2, DATA_TYPE_PRODUCT_LIST_ITEM);
			mutableProductItem2.setProperty(PROP_PARENT_SHOPPING_LIST, mutableShoppingList1);
			mutableProductItem2.setProperty(PROP_PRODUCT_ID, "test-123");
			DataStore.getInstance().insertItem(mutableProductItem2);
			MutableDataItem mutableProductItem3 = DataStore.getInstance().createItem(TEST_LIST_PRODUCT_ID_3, DATA_TYPE_PRODUCT_LIST_ITEM);
			mutableProductItem3.setProperty(PROP_PARENT_SHOPPING_LIST, mutableShoppingList2);
			mutableProductItem3.setProperty(PROP_PRODUCT_ID, "test-123");
			DataStore.getInstance().insertItem(mutableProductItem3);
			// verify items successfully added
			DataItem shoppingList1 = DataStore.getInstance().readItemImmutable(TEST_SHOPPING_LIST_ID_1, DATA_TYPE_SHOPPING_LIST);
			assertEquals(TEST_SHOPPING_LIST_ID_1 + " should have 2 product items", 2, shoppingList1.getList(PROP_SHOPPING_LIST_PRODUCT_ITEMS).size());
			DataItem shoppingList2 = DataStore.getInstance().readItemImmutable(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
			assertEquals(TEST_SHOPPING_LIST_ID_2 + " should have 1 product item", 1, shoppingList2.getList(PROP_SHOPPING_LIST_PRODUCT_ITEMS).size());
			// delete shopping list 1
			mutableShoppingList1 = DataStore.getInstance().readItemForUpdate(TEST_SHOPPING_LIST_ID_1, DATA_TYPE_SHOPPING_LIST);
			DataStore.getInstance().deleteItem(mutableShoppingList1);
			shoppingList1 = DataStore.getInstance().readItemImmutable(TEST_SHOPPING_LIST_ID_1, DATA_TYPE_SHOPPING_LIST);
			assertNull(TEST_SHOPPING_LIST_ID_1 + " should have been deleted", shoppingList1);
			shoppingList2 = DataStore.getInstance().readItemImmutable(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
			assertNotNull(TEST_SHOPPING_LIST_ID_2 + " should not have been deleted", shoppingList2);
			DataItem productItem1 = DataStore.getInstance().readItemImmutable(TEST_LIST_PRODUCT_ID_1, DATA_TYPE_PRODUCT_LIST_ITEM);
			assertNull(TEST_LIST_PRODUCT_ID_1 + " should have been deleted", productItem1);
			DataItem productItem2 = DataStore.getInstance().readItemImmutable(TEST_LIST_PRODUCT_ID_2, DATA_TYPE_PRODUCT_LIST_ITEM);
			assertNull(TEST_LIST_PRODUCT_ID_2 + " should have been deleted", productItem2);
			DataItem productItem3 = DataStore.getInstance().readItemImmutable(TEST_LIST_PRODUCT_ID_3, DATA_TYPE_PRODUCT_LIST_ITEM);
			assertNotNull(TEST_LIST_PRODUCT_ID_3 + " should not have been deleted", productItem3);
			// delete shopping list 2
			mutableShoppingList2 = DataStore.getInstance().readItemForUpdate(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
			DataStore.getInstance().deleteItem(mutableShoppingList2);
			shoppingList2 = DataStore.getInstance().readItemImmutable(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
			assertNull(TEST_SHOPPING_LIST_ID_2 + " should have been deleted", shoppingList2);
			productItem3 = DataStore.getInstance().readItemImmutable(TEST_LIST_PRODUCT_ID_3, DATA_TYPE_PRODUCT_LIST_ITEM);
			assertNull(TEST_LIST_PRODUCT_ID_3 + " should have been deleted", productItem3);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_LIST_PRODUCT_ID_1, DATA_TYPE_PRODUCT_LIST_ITEM);
			this.purgeTestItem(TEST_LIST_PRODUCT_ID_2, DATA_TYPE_PRODUCT_LIST_ITEM);
			this.purgeTestItem(TEST_LIST_PRODUCT_ID_3, DATA_TYPE_PRODUCT_LIST_ITEM);
			this.purgeTestItem(TEST_SHOPPING_LIST_ID_1, DATA_TYPE_SHOPPING_LIST);
			this.purgeTestItem(TEST_SHOPPING_LIST_ID_2, DATA_TYPE_SHOPPING_LIST);
		}
	}
}
