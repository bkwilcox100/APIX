package com.heb.liquidsky.productfeed.dao;

import java.util.Map;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.heb.liquidsky.productfeed.model.Product;

/**
 * Data Access layer for saving products into the Google App Engine Data Store
 * @author bhewett
 *
 */
public class ProductDataStoreDao {

	/**
	 * Batch Update multiple products 
	 * @param products a list of products to update or create in the google data store
	 * @return
	 */
	public Map<Key<Product>, Product> batchUpsertProducts (Iterable<Product> products) {
		return ObjectifyService.ofy().save().entities(products).now();
	}
	
	/**
	 * Upsert a product from it's json String
	 * @param jsonString
	 * @return
	 */
	public Key<Product> upsertProduct(String jsonString) {
		Gson gson = new Gson();

		Product product = gson.fromJson(jsonString, Product.class);
		return upsertProduct(product);
	}

	/**
	 * Upsert a single product
	 * @param product
	 * @return
	 */
	public Key<Product> upsertProduct(Product product) {
		return ObjectifyService.ofy().save().entity(product).now();
	}

	/**
	 * Get product by product id
	 * @param prodId
	 * @return
	 */
	public Product getProduct(Long prodId) {
		Key<Product> prodKey = Key.create(Product.class, prodId);
		Product bookEntity = ObjectifyService.ofy().load().key(prodKey).now();
		
		return bookEntity;
	}

	/**
	 * delete products from the data store
	 * @param prodId
	 */
	public void deleteProduct(Long prodId) {
		Key<Product> prodKey = Key.create(Product.class, prodId);
		ObjectifyService.ofy().delete().key(prodKey).now();
	}

	/**
	 * Delete multiple entities from the data store
	 * @param prodId
	 */
	public void deleteProducts(Iterable<Key<Product>> prodIds) {
		
		ObjectifyService.ofy().delete().keys(prodIds).now();
	}

}
