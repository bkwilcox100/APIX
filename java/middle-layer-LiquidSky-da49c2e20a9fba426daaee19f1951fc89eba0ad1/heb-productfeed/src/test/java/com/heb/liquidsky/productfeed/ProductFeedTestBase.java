package com.heb.liquidsky.productfeed;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.heb.liquidsky.productfeed.dao.ProductDataStoreDao;
import com.heb.liquidsky.productfeed.model.Product;

import junit.framework.TestCase;

public abstract class ProductFeedTestBase extends TestCase {
	protected static final Logger logger = Logger.getLogger(ProductFeedTestBase.class.getName());

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			// Set no eventual consistency, that way queries return all results.
			// https://cloud.google.com/appengine/docs/java/tools/localunittesting#Java_Writing_High_Replication_Datastore_tests
			new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

	private Closeable closeable;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		helper.setUp();

		ObjectifyService.register(Product.class);

		closeable = ObjectifyService.begin();

	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		helper.tearDown();
		closeable.close();
	}

	protected void productInDatabaseTest (Long prodId, String description, Long sku, ProductDataStoreDao productDataStoreDao) {
		Product product = productDataStoreDao.getProduct(prodId);
		if (product == null) {
			fail("product was not found after insert");
		}
		assertEquals(description, product.getDescription());
		assertEquals(sku, product.getDefaultSku());
	}
	

}
