package com.heb.liquidsky.productfeed.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.Key;
import com.heb.liquidsky.apihub.ApiHubProductsService;
import com.heb.liquidsky.productfeed.dao.ProductDataStoreDao;
import com.heb.liquidsky.productfeed.model.Product;
import com.heb.liquidsky.productfeed.model.ProductCollection;
import com.heb.liquidsky.productfeed.util.Properties;

/**
 * This class implements the service for importing HEB product information from the HEB API hub to the Liquid Sky DataStore
 * @author bhewett
 *
 */
public class ProductFeedService {
	private static final Logger logger = Logger.getLogger(ProductFeedService.class.getName());

	private final static ProductDataStoreDao productDataStoreDao = new ProductDataStoreDao();
	/**
	 * Import all products from API Hub
	 * @return
	 */
	public Map<Key<Product>, Product> importAllProducts () {
		logger.info("Starting Full Product import...");
		Map<Key<Product>, Product> productMap = null;
		ApiHubProductsService apiProductService = new ApiHubProductsService();
		String nextPage = null;
		int currentPage = 1;
		try {
			do {
				logger.info(String.format("Reading Page %d", currentPage));
				ProductCollection productCollection = apiProductService.getAllProducts(nextPage);
				if (Properties.i().isTestPhase()) {
					mangleSenstiveData (productCollection);
				}
				if (productCollection != null) {
					nextPage = productCollection.getNextPage();
					productMap = addProductsToDataStore (productCollection.getProducts());
					logger.info(String.format("Saved %d objects to data store", productCollection.getTotalProductsRecords()));
				}
			} while (Properties.i().getMaxPagesToImport() > 0 && currentPage++ < Properties.i().getMaxPagesToImport());	
	
		} catch (NotFoundException nfe) {
			logger.warning("Error while importing all products: " + nfe.getMessage());
		}
		logger.info("...End of Full Product import Job!");
		return productMap;
	}

	/** 
	 * IN the test infrastructure we don't want to use real price and SKU's
	 * @param productCollection
	 */
	private void mangleSenstiveData(ProductCollection productCollection) {
		List<Product> products = productCollection.getProducts();
		if (products != null) {
			for (Product product : products) {
				product.setUpc(Properties.TEST_SKU);
				product.setDefaultSku(Properties.TEST_SKU);

				List<Long> newSkuList = new ArrayList<>();
				for (int i = 0; i < product.getChildSku().size(); i++) {
					newSkuList.add (Properties.TEST_SKU);
				}
				product.setChildSku(newSkuList);
			}
		}
		
	}

	/**
	 * Import the changes 
	 * @param deltaDays TODO
	 * @return
	 */
	public Map<Key<Product>, Product> importDeltaProducts (Integer deltaDays) {
		Map<Key<Product>, Product> productMap = null;
		ApiHubProductsService apiProductService = new ApiHubProductsService();
		String nextPage = null;
		int currentPage = 1;
		if (deltaDays == null) {
			deltaDays = Properties.i().getDefaultDeltaDays();
		}
		logger.info(String.format("Starting Product updates feed for past %d days...", deltaDays));

		try {
			do {
				logger.info(String.format("Reading Page %d", currentPage));
				
				ProductCollection productCollection = apiProductService.getDeltaProducts(nextPage, Properties.i().getDefaultDeltaDays());
				if (productCollection != null) {
					nextPage = productCollection.getNextPage();
					productMap = addProductsToDataStore (productCollection.getProducts());
					logger.info(String.format("Saved %d objects to data store", productCollection.getTotalProductsRecords()));
				}
			} while (Properties.i().getMaxPagesToImport() > 0 && currentPage++ < Properties.i().getMaxPagesToImport());	
	
		} catch (NotFoundException nfe) {
			logger.warning("Error while importing all products: " + nfe.getMessage());
		}
		return productMap;
	}
	
	
	/**
	 * Add Products to the datastore
	 * @param products
	 * @return
	 */
	private Map<Key<Product>, Product> addProductsToDataStore(List<Product> products) {
		if (products.size() <= 200) {
			return productDataStoreDao.batchUpsertProducts(products);
		} else { 
			logger.severe("Batch size to store in the data store must be less or equal to 200. Save not performed");
		}
		return null;
	}

}
