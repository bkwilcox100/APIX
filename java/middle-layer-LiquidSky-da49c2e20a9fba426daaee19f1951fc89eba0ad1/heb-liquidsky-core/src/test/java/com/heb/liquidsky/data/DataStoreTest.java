package com.heb.liquidsky.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.junit.Test;

import com.heb.liquidsky.data.db.ConnectionManager;
import com.heb.liquidsky.test.HEBTestCase;

public class DataStoreTest extends HEBTestCase {

	private static final String DATA_TYPE_PRODUCT = "test-product";
	private static final String DATA_TYPE_SKU = "test-sku";
	private static final String PROP_DESCRIPTION = "description";
	private static final String PROP_PAGE_TITLE = "pageTitle";
	private static final String PROP_ID = "id";
	private static final String PROP_OPTIONAL_PRODUCT = "optionalProduct";
	private static final String PROP_OPTIONAL_SKUS = "optionalSkus";
	private static final String PROP_CHILD_RELATED_PRODUCTS = "childRelatedProducts";
	private static final String PROP_PARENT_RELATED_PRODUCTS = "parentRelatedProducts";
	private static final String PROP_REQUIRED_PRODUCT = "requiredProduct";
	private static final String PROP_REQUIRED_SKUS = "requiredSkus";
	private static final String PROP_TITLE = "title";
	private static final String TEST_PRODUCT_ID_1 = "test-prod-1";
	private static final String TEST_PRODUCT_ID_2 = "test-prod-2";
	private static final String TEST_PRODUCT_ID_3 = "test-prod-3";
	private static final String TEST_PRODUCT_ID_4 = "test-prod-4";
	private static final String TEST_SKU_ID_1 = "test-sku-1";
	private static final String TEST_SKU_ID_2 = "test-sku-2";
	private static final String TEST_SKU_ID_3 = "test-sku-3";

	private void purgeTestItem(DataItem dataItem) {
		if (dataItem != null) {
			this.purgeTestItem(dataItem.getId(), dataItem.getDataType().getName());
		}
	}

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
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while deleting test " + dataType + " with ID " + id, e);
			}
		}
	}

	private MutableDataItem createTestProduct(String id) throws DataStoreException {
		return this.createTestItem(id, DATA_TYPE_PRODUCT);
	}

	private MutableDataItem createTestSku(String id) throws DataStoreException {
		return this.createTestItem(id, DATA_TYPE_SKU);
	}

	// products and SKUs have the same property names, so use a generic
	// method for creating test items
	private MutableDataItem createTestItem(String id, String dataType) throws DataStoreException {
		// verify item doesn't already exist
		DataItem dataItem = DataStore.getInstance().readItemImmutable(id, dataType);
		assertNull("Query for " + dataType + " with id " + id + " should return no results", dataItem);
		// test item insert
		String title = id + " TITLE";
		MutableDataItem mutableDataItem = this.createNewItem(id, dataType, title, TEST_PRODUCT_ID_1);
		dataItem = DataStore.getInstance().readItemImmutable(id, dataType);
		assertNotNull("Query for " + dataType + " with id " + id + " should return one result", dataItem);
		assertEquals(dataType + " with id " + id + " does not have the expected title", title, dataItem.getString(PROP_TITLE));
		assertNull(dataType + " with id " + id + " does not have the expected description", dataItem.getString(PROP_DESCRIPTION));
		Timestamp creationDate = dataItem.getTimestamp(DataType.PROP_CREATION_DATE);
		Timestamp lastModifiedDate = dataItem.getTimestamp(DataType.PROP_LAST_MODIFIED_DATE);
		assertNotNull(dataType + " with id " + id + " does not have a creation date", creationDate);
		assertEquals(dataType + " with id " + id + " should have the same creation & last modified date since it is newly created", creationDate, lastModifiedDate);
		// test item update
		title += " (Updated)";
		mutableDataItem.setProperty(PROP_TITLE, title);
		DataStore.getInstance().updateItem(mutableDataItem);
		dataItem = DataStore.getInstance().readItemImmutable(id, dataType);
		assertNotNull("Query for " + dataType + " with id " + id + " should return one result", dataItem);
		assertEquals(dataType + " with id " + id + " does not have the expected title", title, dataItem.getString(PROP_TITLE));
		assertNull(dataType + " with id " + id + " does not have the expected description", dataItem.getString(PROP_DESCRIPTION));
		String description = id + " DESCRIPTION";
		mutableDataItem.setProperty(PROP_DESCRIPTION, description);
		// sleep briefly to ensure last modified date will be in the
		// future after updating.
		this.pause(1000);
		DataStore.getInstance().updateItem(mutableDataItem);
		dataItem = DataStore.getInstance().readItemImmutable(id, dataType);
		assertEquals(dataType + " with id " + id + " does not have the expected description", description, dataItem.getString(PROP_DESCRIPTION));
		creationDate = dataItem.getTimestamp(DataType.PROP_CREATION_DATE);
		lastModifiedDate = dataItem.getTimestamp(DataType.PROP_LAST_MODIFIED_DATE);
		assertNotNull(dataType + " with id " + id + " does not have a creation date", creationDate);
		assertTrue(dataType + " with id " + id + " should not have the same creation & last modified date since it is has been updated.  creation date is: " + creationDate + ", last modified date is: " + lastModifiedDate, (creationDate != null && lastModifiedDate != null && creationDate.before(lastModifiedDate)));
		assertEquals ("Title in db does not match expected update value", dataItem.getString(PROP_TITLE), title);
		try {
			mutableDataItem.setProperty(PROP_ID, "1234");
			fail(dataType + " with id " + id + " allowed setProperty to be invoked for " + PROP_ID + ", but data item IDs cannot be modified");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			mutableDataItem.setProperty(DataType.PROP_LAST_MODIFIED_DATE, creationDate);
			fail(dataType + " with id " + id + " allowed setProperty to be invoked for " + DataType.PROP_LAST_MODIFIED_DATE + " which is a read-only property");
		} catch (IllegalArgumentException e) {
			// expected
		}
		return mutableDataItem;
	}

	private MutableDataItem createNewItem(String id, String dataType, String title, String requiredProductId) throws DataStoreException {
		MutableDataItem mutableDataItem = DataStore.getInstance().createItem(id, dataType);
		mutableDataItem.setProperty(PROP_TITLE, title);
		if (dataType.equals(DATA_TYPE_SKU)) {
			DataItem productItem = DataStore.getInstance().readItemImmutable(requiredProductId, DATA_TYPE_PRODUCT);
			mutableDataItem.setProperty(PROP_REQUIRED_PRODUCT, productItem);
		}
		DataStore.getInstance().insertItem(mutableDataItem);
		return mutableDataItem;
	}

	@Test
	public void testDataStore() {
		try {
			// verify basic create and update functionality
			MutableDataItem product1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			MutableDataItem sku1 = this.createTestSku(TEST_SKU_ID_1);
			MutableDataItem sku2 = this.createTestSku(TEST_SKU_ID_2);
			// verify one-to-many relationships work
			List<DataItem> optionalSkus = new ArrayList<>();
			optionalSkus.add(sku1);
			optionalSkus.add(sku2);
			product1.setProperty(PROP_OPTIONAL_SKUS, optionalSkus);
			DataStore.getInstance().updateItem(product1);
			DataItem dataItem = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			optionalSkus = dataItem.getList(PROP_OPTIONAL_SKUS);
			int optionalSkuCount = optionalSkus.size();
			assertEquals("Incorrect number of optional SKUs found for " + DATA_TYPE_PRODUCT + " ID " + TEST_PRODUCT_ID_1, 2, optionalSkuCount);
			try {
				optionalSkus.add(sku1);
				fail("DatabaseDataItem.getList() should return an immutable list; updates should throw an exception");
			} catch (UnsupportedOperationException e) {
				// expected
			}
			dataItem = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_1, DATA_TYPE_SKU);
			DataItem optionalProduct = dataItem.getItem(PROP_OPTIONAL_PRODUCT);
			assertNotNull("Optional product not found for " + DATA_TYPE_SKU + " ID " + TEST_SKU_ID_1, optionalProduct);
			// delete a SKU and verify the product optional SKU property updates
			DataStore.getInstance().deleteItem(sku2);
			dataItem = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			optionalSkuCount = dataItem.getList(PROP_OPTIONAL_SKUS).size();
			assertEquals("Incorrect number of optional SKUs found for " + DATA_TYPE_PRODUCT + " ID " + TEST_PRODUCT_ID_1, 1, optionalSkuCount);
			// MLS-49: verify update doesn't delete children
			product1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			DataStore.getInstance().updateItem(product1);
			dataItem = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			optionalSkuCount = dataItem.getList(PROP_OPTIONAL_SKUS).size();
			assertEquals("Incorrect number of optional SKUs found for " + DATA_TYPE_PRODUCT + " ID " + TEST_PRODUCT_ID_1, 1, optionalSkuCount);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_SKU_ID_1, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_SKU_ID_2, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}

	@Test
	public void testIdGenerator() {
		MutableDataItem productItem1 = null;
		MutableDataItem productItem2 = null;
		MutableDataItem skuItem = null;
		try {
			String purgeExistingIdGeneratorSql = "delete from heb_id_generator where data_type = ?";
			try (Connection conn = ConnectionManager.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(purgeExistingIdGeneratorSql)) {
				pstmt.setString(1, DATA_TYPE_PRODUCT);
				pstmt.executeUpdate();
				pstmt.setString(1, DATA_TYPE_SKU);
				pstmt.executeUpdate();
			}
			String title = "ID GENERATOR TEST";
			productItem1 = this.createNewItem(null, DATA_TYPE_PRODUCT, title, null);
			String productId1 = productItem1.getId();
			assertEquals("Product does not have the expected ID", "test-prod1", productId1);
			// see MLS-421 - last_insert_id gives the last insert ID
			// for any table, so insert two products, which would trigger
			// the bug by initializing the SKU table with an ID of "2"
			productItem2 = this.createNewItem(null, DATA_TYPE_PRODUCT, title, null);
			String productId2 = productItem2.getId();
			assertEquals("Product does not have the expected ID", "test-prod2", productId2);
			skuItem = this.createNewItem(null, DATA_TYPE_SKU, title, productId1);
			String skuId = skuItem.getId();
			assertEquals("SKU does not have the expected ID", "test-sku1", skuId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(skuItem);
			this.purgeTestItem(productItem1);
			this.purgeTestItem(productItem2);
		}
	}

	@Test
	public void testConcurrentUpdate() {
		MutableDataItem mutableDataItem1 = null;
		MutableDataItem mutableDataItem2 = null;
		try {
			// create a new item as mutableDataItem1
			mutableDataItem1 = DataStore.getInstance().createItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			String title = "PRODUCT TITLE";
			mutableDataItem1.setProperty(PROP_TITLE, title);
			DataStore.getInstance().insertItem(mutableDataItem1);
			// sleep briefly to ensure last modified date will be in the future
			this.pause(1000);
			// read the same record into mutableDataItem2, then update it
			mutableDataItem2 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			title = "PRODUCT TITLE (UPDATED)";
			mutableDataItem2.setProperty(PROP_TITLE, title);
			DataStore.getInstance().updateItem(mutableDataItem2);
			// mutableDataItem1 is now out of sync with the data store, so
			// updating it should fail with a concurrent update error
			title = "PRODUCT TITLE (UPDATED TWICE)";
			mutableDataItem1.setProperty(PROP_TITLE, title);
			DataStore.getInstance().updateItem(mutableDataItem1);
			fail("Update was expected to fail due to concurrent modification: current item has last modified date: " + mutableDataItem2.getLastModifiedDate() + ", out of date item has last modified date: " + mutableDataItem1.getLastModifiedDate());
		} catch (ConcurrentModificationException e) {
			// expected
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(mutableDataItem1);
			this.purgeTestItem(mutableDataItem2);
		}
	}

	@Test
	public void testMetadata() {
		try {
			MutableDataItem product1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			DataItemDescriptor descriptor = product1.dataItemDescriptor();
			assertNotNull(TEST_PRODUCT_ID_1 + " should not have a null descriptor", descriptor);
			assertTrue(TEST_PRODUCT_ID_1 + " should have multiple properties", (!descriptor.getPropertyDescriptors().isEmpty()));
			this.verifyItemDescriptorHasProperty(descriptor, PROP_OPTIONAL_SKUS, true);
			this.verifyItemDescriptorHasProperty(descriptor, PROP_REQUIRED_SKUS, true);
			this.verifyItemDescriptorHasProperty(descriptor, DataType.PROP_LAST_MODIFIED_DATE, false);
			this.verifyItemDescriptorHasProperty(descriptor, DataType.PROP_CREATION_DATE, false);
			this.verifyItemDescriptorHasProperty(descriptor, PROP_PAGE_TITLE, false);
			DataItemPropertyDescriptor propertyDescriptor = descriptor.getIdPropertyDescriptor();
			assertNotNull(TEST_PRODUCT_ID_1 + " should not have a null id property descriptor", propertyDescriptor);
			assertEquals(TEST_PRODUCT_ID_1 + " ID property descriptor should be 'id'", propertyDescriptor.getPropertyName(), "id");
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}

	@Test
	public void testCustomProperty() {
		try {
			MutableDataItem item = DataStore.getInstance().createItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			String title = "CUSTOM PROPERTY TEST";
			item.setProperty(PROP_TITLE, title);
			DataStore.getInstance().insertItem(item);
			String pageTitle = item.getString(PROP_PAGE_TITLE);
			String expectedPageTitle = "HEB - " + title;
			assertEquals(TEST_PRODUCT_ID_1 + " " + PROP_PAGE_TITLE + " property value is incorrect", expectedPageTitle, pageTitle);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}

	private void verifyItemDescriptorHasProperty(DataItemDescriptor descriptor, String propertyName, boolean isDataItem) {
		for (DataItemPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors()) {
			if (propertyDescriptor.isDataItem() == isDataItem && propertyDescriptor.getPropertyName().equals(propertyName)) {
				return;
			}
		}
		fail("Item descriptor does not contain expected " + ((isDataItem) ? "DATA ITEM" : "NON-DATA ITEM") + " property");
	}

	/**
	 * Test relationships of type "reference" in the data store that
	 * represent one-to-many relationships.
	 */
	@Test
	public void testMulti() {
		try {
			// create an empty test product
			MutableDataItem mutableProduct1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			// create two empty test skus
			MutableDataItem mutableSku1 = this.createTestSku(TEST_SKU_ID_1);
			MutableDataItem mutableSku2 = this.createTestSku(TEST_SKU_ID_2);
			// verify optional skus and optional product are empty, putting those
			// values in the cache in the process
			this.validateTestMulti(0, true, true);
			// add optional sku via optional sku property
			mutableProduct1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			List<DataItem> optionalSkus = mutableProduct1.getList(PROP_OPTIONAL_SKUS);
			optionalSkus.add(mutableSku1);
			mutableProduct1.setProperty(PROP_OPTIONAL_SKUS, optionalSkus);
			DataStore.getInstance().updateItem(mutableProduct1);
			// verify both product & sku have updated properly
			this.validateTestMulti(1, false, true);
			// add optional sku via optional product property
			mutableSku2 = DataStore.getInstance().readItemForUpdate(TEST_SKU_ID_2, DATA_TYPE_SKU);
			mutableSku2.setProperty(PROP_OPTIONAL_PRODUCT, mutableProduct1);
			DataStore.getInstance().updateItem(mutableSku2);
			// check the values again
			this.validateTestMulti(2, false, false);
			// remove optional sku via optional product property
			mutableSku1 = DataStore.getInstance().readItemForUpdate(TEST_SKU_ID_1, DATA_TYPE_SKU);
			mutableSku1.setProperty(PROP_OPTIONAL_PRODUCT, null);
			DataStore.getInstance().updateItem(mutableSku1);
			// check the values again
			this.validateTestMulti(1, true, false);
			// remove optional sku via optional sku property
			mutableProduct1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			mutableProduct1.setProperty(PROP_OPTIONAL_SKUS, null);
			DataStore.getInstance().updateItem(mutableProduct1);
			// check the values again
			this.validateTestMulti(0, true, true);
			// re-add skus to the product
			mutableProduct1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			optionalSkus = mutableProduct1.getList(PROP_OPTIONAL_SKUS);
			optionalSkus.add(mutableSku1);
			optionalSkus.add(mutableSku2);
			mutableProduct1.setProperty(PROP_OPTIONAL_SKUS, optionalSkus);
			DataStore.getInstance().updateItem(mutableProduct1);
			this.validateTestMulti(2, false, false);
			// delete sku2, make sure things update properly
			mutableSku2 = DataStore.getInstance().readItemForUpdate(TEST_SKU_ID_2, DATA_TYPE_SKU);
			DataStore.getInstance().deleteItem(mutableSku2);
			this.validateTestMulti(1, false, true);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_SKU_ID_1, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_SKU_ID_2, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}

	private void validateTestMulti(int prod1Count, boolean sku1Null, boolean sku2Null) throws DataStoreException {
		DataItem product1 = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		assertNotNull(TEST_PRODUCT_ID_1 + " should not have a null optional SKUs property", product1.getList(PROP_OPTIONAL_SKUS));
		assertEquals(TEST_PRODUCT_ID_1 + " should have " + prod1Count + " optional SKUs", prod1Count, product1.getList(PROP_OPTIONAL_SKUS).size());
		DataItem sku1 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_1, DATA_TYPE_SKU);
		if (!sku1Null) {
			assertNotNull(TEST_SKU_ID_1 + " should not have a null optional product property", sku1.getItem(PROP_OPTIONAL_PRODUCT));
		} else if (sku1 != null) {
			assertNull(TEST_SKU_ID_1 + " should have a null optional product property", sku1.getItem(PROP_OPTIONAL_PRODUCT));
		}
		DataItem sku2 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_2, DATA_TYPE_SKU);
		if (!sku2Null) {
			assertNotNull(TEST_SKU_ID_2 + " should not have a null optional product property", sku2.getItem(PROP_OPTIONAL_PRODUCT));
		} else if (sku2 != null) {
			assertNull(TEST_SKU_ID_2 + " should have a null optional product property", sku2.getItem(PROP_OPTIONAL_PRODUCT));
		}
	}

	/**
	 * Test relationships of type "auxiliary" in the data store that
	 * represent many-to-many relationships.
	 */
	@Test
	public void testMulti2() {
		try {
			// create four empty test products
			MutableDataItem mutableProduct1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			MutableDataItem mutableProduct2 = this.createTestProduct(TEST_PRODUCT_ID_2);
			MutableDataItem mutableProduct3 = this.createTestProduct(TEST_PRODUCT_ID_3);
			MutableDataItem mutableProduct4 = this.createTestProduct(TEST_PRODUCT_ID_4);
			this.validateTestMulti2(0, 0, 0, 0, 0, 0, 0, 0);
			// add related products to product #1
			mutableProduct1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			List<DataItem> relatedProducts = mutableProduct1.getList(PROP_CHILD_RELATED_PRODUCTS);
			relatedProducts.add(mutableProduct2);
			relatedProducts.add(mutableProduct3);
			relatedProducts.add(mutableProduct4);
			mutableProduct1.setProperty(PROP_CHILD_RELATED_PRODUCTS, relatedProducts);
			DataStore.getInstance().updateItem(mutableProduct1);
			this.validateTestMulti2(3, 0, 0, 1, 0, 1, 0, 1);
			// add related products to product #2
			mutableProduct2 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_2, DATA_TYPE_PRODUCT);
			relatedProducts = mutableProduct2.getList(PROP_CHILD_RELATED_PRODUCTS);
			relatedProducts.add(mutableProduct3);
			relatedProducts.add(mutableProduct4);
			mutableProduct2.setProperty(PROP_CHILD_RELATED_PRODUCTS, relatedProducts);
			DataStore.getInstance().updateItem(mutableProduct2);
			this.validateTestMulti2(3, 0, 2, 1, 0, 2, 0, 2);
			// add related products to product #3
			mutableProduct3 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_3, DATA_TYPE_PRODUCT);
			relatedProducts = mutableProduct3.getList(PROP_CHILD_RELATED_PRODUCTS);
			relatedProducts.add(mutableProduct4);
			mutableProduct3.setProperty(PROP_CHILD_RELATED_PRODUCTS, relatedProducts);
			DataStore.getInstance().updateItem(mutableProduct3);
			this.validateTestMulti2(3, 0, 2, 1, 1, 2, 0, 3);
			// add related products to product #4
			mutableProduct4 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_4, DATA_TYPE_PRODUCT);
			relatedProducts = mutableProduct4.getList(PROP_CHILD_RELATED_PRODUCTS);
			relatedProducts.add(mutableProduct1);
			relatedProducts.add(mutableProduct2);
			relatedProducts.add(mutableProduct3);
			mutableProduct4.setProperty(PROP_CHILD_RELATED_PRODUCTS, relatedProducts);
			DataStore.getInstance().updateItem(mutableProduct4);
			this.validateTestMulti2(3, 1, 2, 2, 1, 3, 3, 3);
			// delete product #4 and verify that things still look good
			mutableProduct4 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_4, DATA_TYPE_PRODUCT);
			DataStore.getInstance().deleteItem(mutableProduct4);
			this.validateTestMulti2(2, 0, 1, 1, 0, 2, 0, 0);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_PRODUCT_ID_4, DATA_TYPE_PRODUCT);
			this.purgeTestItem(TEST_PRODUCT_ID_3, DATA_TYPE_PRODUCT);
			this.purgeTestItem(TEST_PRODUCT_ID_2, DATA_TYPE_PRODUCT);
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}

	private void validateTestMulti2(int prod1ChildCount, int prod1ParentCount, int prod2ChildCount, int prod2ParentCount, int prod3ChildCount, int prod3ParentCount, int prod4ChildCount, int prod4ParentCount) throws DataStoreException {
		this.validateTestMulti2(TEST_PRODUCT_ID_1, prod1ChildCount, prod1ParentCount);
		this.validateTestMulti2(TEST_PRODUCT_ID_2, prod2ChildCount, prod2ParentCount);
		this.validateTestMulti2(TEST_PRODUCT_ID_3, prod3ChildCount, prod3ParentCount);
		this.validateTestMulti2(TEST_PRODUCT_ID_4, prod4ChildCount, prod4ParentCount);
	}

	private void validateTestMulti2(String productId, int childProductCount, int parentProductCount) throws DataStoreException {
		DataItem product = DataStore.getInstance().readItemImmutable(productId, DATA_TYPE_PRODUCT);
		if (product != null || childProductCount != 0 || parentProductCount != 0) {
			assertNotNull(productId + " should not have a null child related products property", product.getList(PROP_CHILD_RELATED_PRODUCTS));
			assertEquals(productId + " should have " + childProductCount + " child related products", childProductCount, product.getList(PROP_CHILD_RELATED_PRODUCTS).size());
			assertNotNull(productId + " should not have a null parent related products property", product.getList(PROP_PARENT_RELATED_PRODUCTS));
			assertEquals(productId + " should have " + parentProductCount + " parent related products", parentProductCount, product.getList(PROP_PARENT_RELATED_PRODUCTS).size());
		}
	}

	@Test
	public void testCascadeDelete() {
		try {
			// create test data
			MutableDataItem mutableProduct1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			MutableDataItem mutableProduct2 = this.createTestProduct(TEST_PRODUCT_ID_2);
			MutableDataItem mutableSku1 = this.createTestSku(TEST_SKU_ID_1);
			MutableDataItem mutableSku2 = this.createTestSku(TEST_SKU_ID_2);
			MutableDataItem mutableSku3 = this.createTestSku(TEST_SKU_ID_3);
			// switch sku 3 to point to required product 2
			mutableSku3.setProperty(PROP_REQUIRED_PRODUCT, mutableProduct2);
			DataStore.getInstance().updateItem(mutableSku3);
			// verify required products set properly
			assertNotNull(TEST_SKU_ID_1 + " should not have a null require product property", mutableSku1.getItem(PROP_REQUIRED_PRODUCT));
			assertEquals(TEST_SKU_ID_1 + " should have " + TEST_PRODUCT_ID_1 + " as its required product", TEST_PRODUCT_ID_1, mutableSku1.getItem(PROP_REQUIRED_PRODUCT).getId());
			assertNotNull(TEST_SKU_ID_2 + " should not have a null require product property", mutableSku2.getItem(PROP_REQUIRED_PRODUCT));
			assertEquals(TEST_SKU_ID_2 + " should have " + TEST_PRODUCT_ID_1 + " as its required product", TEST_PRODUCT_ID_1, mutableSku2.getItem(PROP_REQUIRED_PRODUCT).getId());
			assertNotNull(TEST_SKU_ID_3 + " should not have a null require product property", mutableSku3.getItem(PROP_REQUIRED_PRODUCT));
			assertEquals(TEST_SKU_ID_3 + " should have " + TEST_PRODUCT_ID_2 + " as its required product", TEST_PRODUCT_ID_2, mutableSku3.getItem(PROP_REQUIRED_PRODUCT).getId());
			// delete product 2 & verify it cascades
			mutableProduct2 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_2, DATA_TYPE_PRODUCT);
			DataStore.getInstance().deleteItem(mutableProduct2);
			DataItem product1 = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			DataItem product2 = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_2, DATA_TYPE_PRODUCT);
			DataItem sku1 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_1, DATA_TYPE_SKU);
			DataItem sku2 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_2, DATA_TYPE_SKU);
			DataItem sku3 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_3, DATA_TYPE_SKU);
			assertNotNull(TEST_PRODUCT_ID_1 + " should not have been deleted", product1);
			assertNull(TEST_PRODUCT_ID_2 + " should have been deleted", product2);
			assertNotNull(TEST_SKU_ID_1 + " should not have been deleted", sku1);
			assertNotNull(TEST_SKU_ID_2 + " should not have been deleted", sku2);
			assertNull(TEST_SKU_ID_3 + " should have been deleted", sku3);
			// delete product 1 & verify it cascades
			mutableProduct1 = DataStore.getInstance().readItemForUpdate(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			DataStore.getInstance().deleteItem(mutableProduct1);
			product1 = DataStore.getInstance().readItemImmutable(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			sku1 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_1, DATA_TYPE_SKU);
			sku2 = DataStore.getInstance().readItemImmutable(TEST_SKU_ID_2, DATA_TYPE_SKU);
			assertNull(TEST_PRODUCT_ID_1 + " should have been deleted", product1);
			assertNull(TEST_SKU_ID_1 + " should have been deleted", sku1);
			assertNull(TEST_SKU_ID_2 + " should have been deleted", sku2);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_PRODUCT_ID_2, DATA_TYPE_PRODUCT);
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
			this.purgeTestItem(TEST_SKU_ID_3, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_SKU_ID_2, DATA_TYPE_SKU);
			this.purgeTestItem(TEST_SKU_ID_1, DATA_TYPE_SKU);
		}
	}
	
	@Test
	public void testPropertyAttributes() {
		try {
			// create test data
			MutableDataItem product1 = this.createTestProduct(TEST_PRODUCT_ID_1);
			String value = product1.getDataType().getPropertyByName("title").getAttributeByName("test");
			assertEquals("The test attribute of the title property was expected to be 42", "42", value);
			Map<String,String> attributes = product1.getDataType().getPropertyByName("title").getAttributes();
			assertNotNull("Attributes should not be empty", attributes);
			assertEquals("Size of attributes list was expected to be 2", 2, attributes.size());
		} catch (DataStoreException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			this.purgeTestItem(TEST_PRODUCT_ID_1, DATA_TYPE_PRODUCT);
		}
	}
}
