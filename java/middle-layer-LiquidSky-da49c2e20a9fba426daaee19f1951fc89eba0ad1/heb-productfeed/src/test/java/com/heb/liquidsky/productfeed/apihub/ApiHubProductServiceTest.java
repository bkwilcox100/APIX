package com.heb.liquidsky.productfeed.apihub;

import org.junit.Test;

import com.google.api.server.spi.response.NotFoundException;
import com.heb.liquidsky.apihub.ApiHubProductsService;
import com.heb.liquidsky.productfeed.ProductFeedTestBase;
import com.heb.liquidsky.productfeed.model.Product;
import com.heb.liquidsky.productfeed.model.ProductCollection;

public class ApiHubProductServiceTest extends ProductFeedTestBase {
	public static final Long PRODUCT_ID = new Long(319426);
	
	@Test
	/**
	 * Test getting one page of data
	 */
	public void testGetOnePageProducts () {
		ApiHubProductsService apiHubProductsService = new ApiHubProductsService();
		try { 
			ProductCollection prodCollection = apiHubProductsService.getAllProducts(null, null, null);
			
			assertEquals(prodCollection.getTotalProductsRecords(), new Long (200));
			assertNotNull(prodCollection.getProducts());
			assertEquals (prodCollection.getProducts().size(), 200);
		} catch (NotFoundException nfe) {
			fail ("Unable to get product collection");
		}
		
	}
	
	@Test
	/**
	 * Test getting two pages of product from API hub using the default return size (200 products)
	 */
	public void testGetTwoPageProducts () {
		ApiHubProductsService apiHubProductsService = new ApiHubProductsService();
		try {
			// Get Page 1
			ProductCollection prodCollection = apiHubProductsService.getAllProducts(null, null, null);
			
			assertEquals(prodCollection.getTotalProductsRecords(), new Long (200));
			assertNotNull(prodCollection.getProducts());
			assertEquals (prodCollection.getProducts().size(), 200);
			assertEquals(prodCollection.getCurrentProductsPage(), new Long (1));
			
			// Get Page 2
			prodCollection = apiHubProductsService.getAllProducts(null, null, prodCollection.getNextPage());
			assertEquals(new Long (200), prodCollection.getTotalProductsRecords() );
			assertNotNull(prodCollection.getProducts());
			assertEquals (200, prodCollection.getProducts().size());
			assertEquals(new Long (2), prodCollection.getCurrentProductsPage());
		} catch (NotFoundException nfe) {
			fail ("Unable to get product collection");
		}
		
	}
	
	@Test
	/**
	 * Test getting one product
	 */
	public void testGetProduct () {
		ApiHubProductsService apiHubProductsService = new ApiHubProductsService();
		try {
			Product product = apiHubProductsService.getProduct(PRODUCT_ID);
			assertNotNull(product);
			assertEquals(PRODUCT_ID, product.getProdId());
			assertEquals(new Long(4593), product.getUpc());
	
		} catch (NotFoundException nfe) {
			fail ("Unable to get product ID, " + PRODUCT_ID + " from ApiHub: " + nfe.getMessage());
		}
		
	}

	@Test
	/**
	 * Test that we can get 2 pages of size 10
	 */
	public void testGet10ProductsPerPage () {
		ApiHubProductsService apiHubProductsService = new ApiHubProductsService();
		Integer pageSize = 10 ;
		try {
			// Get Page 1
			ProductCollection prodCollection = apiHubProductsService.getAllProducts(null, pageSize, null);
			
			assertEquals(new Long(pageSize), prodCollection.getTotalProductsRecords());
			assertNotNull(prodCollection.getProducts());
			assertEquals (pageSize.intValue(), prodCollection.getProducts().size());
			assertEquals(new Long (1), prodCollection.getCurrentProductsPage());
			
			// Get Page 2
			prodCollection = apiHubProductsService.getAllProducts(null, pageSize, prodCollection.getNextPage());
			assertEquals(new Long(pageSize), prodCollection.getTotalProductsRecords() );
			assertNotNull(prodCollection.getProducts());
			assertEquals (pageSize.intValue(), prodCollection.getProducts().size());
			assertEquals(new Long (2), prodCollection.getCurrentProductsPage());
		} catch (NotFoundException nfe) {
			fail ("Unable to get product collection");
		}
		
	}

	@Test
	/**
	 * Test that we can get 2 delta pages of size 10
	 */
	public void testGetDeltaProducts () {
		ApiHubProductsService apiHubProductsService = new ApiHubProductsService();
		int deltaDays = 5;
		try {
			// Get Page 1
			ProductCollection prodCollection = apiHubProductsService.getDeltaProducts(null, null, null, deltaDays);
			
			assertNotNull(prodCollection.getProducts());
			assertEquals(new Long (1), prodCollection.getCurrentProductsPage());
			
			// Get Page 2
			String nextPage = prodCollection.getNextPage();
			if (nextPage != null) {
				prodCollection = apiHubProductsService.getDeltaProducts(null, null, nextPage, deltaDays);
				assertNotNull(prodCollection.getProducts());
				assertEquals(new Long (2), prodCollection.getCurrentProductsPage());
			}
		} catch (NotFoundException nfe) {
			fail ("Unable to get product collection");
		}
		
	}
}
