package com.heb.liquidsky.productfeed.service;

import org.junit.Test;

import com.heb.liquidsky.productfeed.ProductFeedTestBase;
import com.heb.liquidsky.productfeed.dao.ProductDataStoreDao;
import com.heb.liquidsky.productfeed.util.Properties;

public class ProductFeedServiceTest extends ProductFeedTestBase {
	
	/**
	 * Test kicking off the feed  
	 */
	@Test
	public void testProductFeed () {
		ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();
		ProductFeedService productFeedService = new ProductFeedService();
		
		productFeedService.importAllProducts();
		Long sku = 86170L;
		if (Properties.i().isTestPhase()) {
			sku = Properties.TEST_SKU;
		}
		productInDatabaseTest(7843L, "Ground Mace", sku, productDataStoreDao);
	}

	@Test
	public void testProductDeltaFeed () {
		ProductFeedService productFeedService = new ProductFeedService();
		
		productFeedService.importDeltaProducts(null);
	}

}
