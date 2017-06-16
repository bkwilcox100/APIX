package com.heb.liquidsky.productfeed.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.googlecode.objectify.Key;
import com.heb.liquidsky.productfeed.ProductFeedTestBase;
import com.heb.liquidsky.productfeed.model.Product;

public class ProductDatastoreDaoTest extends ProductFeedTestBase {
	final static Long ADD_PRODUCT_KEY = new Long(12345678);
	final static Long DELETE_PRODUCT_KEY = new Long(86868686);
	final static Long BATCH_KEY_1 = new Long(11111111);
	final static Long BATCH_KEY_2 = new Long(22222222);
	final static Long BATCH_KEY_3 = new Long(33333333);
	final static Long BATCH_KEY_4 = new Long(44444444);

	
	private Product createTestProduct (Long prodId, String description, Long sku) {
		Product product = new Product();
		product.setProdId(prodId);
		product.setDescription(description);
		product.setDefaultSku(sku);
		return product;
	}
	
	@Test
	/** 
	 * test creating a new product and updating that product
	 */
	public void testProductUpsert() {
		String description = "Bananas";
		ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();
		try {
			Long sku = 8109L;
			// Test CewE
			Product product = createTestProduct (ADD_PRODUCT_KEY, description, sku);
			productDataStoreDao.upsertProduct(product);
			productInDatabaseTest(ADD_PRODUCT_KEY, description, sku, productDataStoreDao);
			// Test Update
			String description2 = "Little Bananas";
			product.setDescription(description2);
			productDataStoreDao.upsertProduct(product);
			productInDatabaseTest(ADD_PRODUCT_KEY, description2, sku, productDataStoreDao);

		} finally {
			productDataStoreDao.deleteProduct(ADD_PRODUCT_KEY);
		}
	}

	@Test
	/**
	 * Test upserting a Product JSON object
	 */
	public void testUpsertProductJson() {
		String json = "{'prodId': 319426,'upc': 4593,'bonusPack': false,'plu': false,'type': 'GOODS','showOnSite': true,'averageWeight': 0,'privateLabel': false,'weightTolerance': false,'minOrderValue': 1,'maxOrderValue': 999999999,'orderIncrement': 1,'isTaxable': false,'brand': 'Fresh','classCode': 42,'departmentCode': '09',  'displayName': 'Fresh Seedless Cucumbers,EACH','description': 'Seedless Cucumbers','longDescription': 'Fresh Seedless Cucumbers','startDate': '01/06/2008','endDate': '12/31/9999','childSku': [4593],'pssDepartmentId': '20','pssDepartmentName': 'BARGAIN BASEMENT','salesChannel': '03','alcoholPct': 0,'primoPick': false,'ownBrand': false,'defaultparentCategory': true,'defaultSku': 4593,'template': 'Other','fsa': false,'wic': false,'leb': false,'foodStamp': true,'heartHealthyClaim': false,'lowSodiumClaim': false,  'fiberSourceClaim': false,'lowSaturatedFatClaim': false,'madeInTexas': false,'isAddOnProduct': false,'taxCategoryCD': '000088','ageRestriction': false,'subdepartmentCode': 'A','isLargeSurcharge': false,'customerFriendlySize': 'EACH','fulfillmentChnl': 'Curb-Side Pick Up','retlUntWd': 1,'retlUntLn': 1,'retlUntHt': 1,'retlUntWt': 0,'stdUomCd': 'EACH','uomQty': '1','imgUri': 319426,'minAgeRestriction': 0,'isSupplement': false,  'productFulfillmentChnl': [ {'isCurbsidePickUp': true,'isCurbsideDelivery': true}],'categories': [{'categoryId': 418899,'isDefaultParent': true}],'prodExtAttrib': [{'attributeId': 1668,'attributeName': 'Height','attributeValtxt': '2017-03-02T20:30:04.884-06:00'},{'attributeId': 1669,'attributeName': 'Width','attributeValtxt': '2017-03-02T20:30:04.884-06:00'},{'attributeId': 1670,'attributeName': 'Depth','attributeValtxt': '2017-03-02T20:30:04.885-06:00'}]}";

		ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();

		productDataStoreDao.upsertProduct(json);
		productInDatabaseTest(319426L, "Seedless Cucumbers", 4593L, productDataStoreDao);
	}

	@Test
	/**
	 * Test deleting objects from the datastore
	 */
	public void testProductDelete() {
		ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();
		Product product = createTestProduct(DELETE_PRODUCT_KEY, "Oranges", new Long(8101));
		productDataStoreDao.upsertProduct(product);
		product = productDataStoreDao.getProduct(DELETE_PRODUCT_KEY);
		if (product == null) {
			fail("product was not found after insert");
		}

		productDataStoreDao.deleteProduct(DELETE_PRODUCT_KEY);
		product = productDataStoreDao.getProduct(DELETE_PRODUCT_KEY);
		if (product != null) {
			fail("Product was found after delete");

		}
	}
	
	@Test 
	public void testBatchProductUpsert () {
		ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();
		Map <Key<Product>, Product> delMap = null;
		try {
	
			List<Product> entityList = new ArrayList<>();
			
			entityList.add(createTestProduct(BATCH_KEY_1, "apples", 1111L));
			entityList.add(createTestProduct(BATCH_KEY_2, "bananas", 2222L));
			entityList.add(createTestProduct(BATCH_KEY_3, "oranges", 33333L));
			entityList.add(createTestProduct(BATCH_KEY_4, "strawberries", 4444L));
			
			delMap = productDataStoreDao.batchUpsertProducts(entityList);
			
			productInDatabaseTest(BATCH_KEY_1, "apples", 1111L, productDataStoreDao);
			productInDatabaseTest(BATCH_KEY_2, "bananas", 2222L, productDataStoreDao);
			productInDatabaseTest(BATCH_KEY_3, "oranges", 33333L, productDataStoreDao);
			productInDatabaseTest(BATCH_KEY_4, "strawberries", 4444L, productDataStoreDao);

		} finally {
			productDataStoreDao.deleteProducts(delMap.keySet());
			if (productDataStoreDao.getProduct(BATCH_KEY_4) != null){
				fail ("Unable to delete product in batch delete");
			}
		}
	}
	
}
