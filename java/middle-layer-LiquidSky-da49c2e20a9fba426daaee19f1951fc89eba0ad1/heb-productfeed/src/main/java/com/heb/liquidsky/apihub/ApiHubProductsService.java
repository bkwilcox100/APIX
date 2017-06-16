package com.heb.liquidsky.apihub;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.server.spi.response.NotFoundException;
import com.google.gson.Gson;
import com.heb.liquidsky.productfeed.model.Product;
import com.heb.liquidsky.productfeed.model.ProductCollection;

/**
 * This class is responsible for accessing the Product data from the HEB API Hub
 * @author bhewett
 *
 */
public class ApiHubProductsService {
	static final public String PRODUCT_ENDPOINT = "apihub/v1/products";
	static final public String DELTA_ENDPOINT = "/delta";
	static final public String MAX_ID_PARAM = "maxId";
	static final public String SUBSET_SIZE_PARAM = "subsetSize";

	/**
	 * Get first page of products from the API hub. This page will be the API Hub default page size (200).
	 * @return a ProductCollection of Products.
	 * @throws NotFoundException
	 */
	public ProductCollection getAllProducts ()
			throws NotFoundException {
			return getAllProducts(null, null, null);	
	}
			
	/**
	 * Get first page of products from the API hub. This page will be the API Hub default page size (200).
	 * @return a ProductCollection of Products.
	 * @throws NotFoundException
	 */
	public ProductCollection getAllProducts (Long maxId) 
			throws NotFoundException {
			return getAllProducts(maxId, null, null);	
	}

	/**
	 * Get the next page of products from the API hub using the nextPage string from the previous call. 
	 * This page will be the API Hub default page size (200).
	 * @return a ProductCollection of Products.
	 * @throws NotFoundException
	 */
	public ProductCollection getAllProducts (String nextPage) 
			throws NotFoundException {
			return getAllProducts(null, null, nextPage);	
	}

	/**
	 * Get the next page of products from the API hub using the nextPage string from the previous call. 
	 * This page will be the API Hub default page size (200).
	 * @return a ProductCollection of Products.
	 * @throws NotFoundException
	 */
	public ProductCollection getAllProducts (Integer subsetSize, String nextPage) 
			throws NotFoundException {
			return getAllProducts(null, subsetSize, nextPage);	
	}
	/**
	 * Get products from the 
	 * @param maxId the last id that was processed in the previous call.  Used to set the starting product id of this call
	 * @param subsetSize page size to retrieve from API hub.  Default is 200 products
	 * @param nextPage partial URL string returned from the API hub in a previous call.  Used to retrieve the next set of products
	 * @return a collection of Products.
	 * @throws NotFoundException
	 */
	public ProductCollection getAllProducts (Long maxId, Integer subsetSize, String nextPage)
			throws NotFoundException {

		Map<String, String> params = new HashMap<>(2);
		String maxIdParam = null;

		String endpoint = null;
		if (nextPage != null) {
			String[] urlParts = nextPage.split("[?]");
			endpoint = urlParts[0];
			if (urlParts.length > 1) {
				String [] queryString = urlParts[1].split("[=]");
				if (queryString.length > 1){
					maxIdParam = queryString[1];
				}
			}
		} else {
			endpoint = PRODUCT_ENDPOINT;
		}
		if (subsetSize != null) {
			params.put(SUBSET_SIZE_PARAM, subsetSize.toString());
		}

		if (maxId != null && maxIdParam == null) {
			maxIdParam = maxId.toString();
		}

		if (maxIdParam != null) {
			params.put(MAX_ID_PARAM, maxIdParam);
		}
		return this.processProductService(endpoint, new String[]{}, params);
	}
	
	/**
	 * Get all product updates for deltaDays back. 
	 * @param maxId the last prod id of the previous search.  Overrides the next page property 
	 * @param subsetSize number of records to return in each call.  Default is 200.
	 * @param nextPage URL for the next page of URL's.  Leave null for the first page
	 * @param deltaDays number of days back to look for product updates
	 * @return
	 * @throws NotFoundException
	 */
	public ProductCollection getDeltaProducts (String nextPage, Integer deltaDays) throws NotFoundException {
		return getDeltaProducts(null, null, nextPage, deltaDays);
	}

	/**
	 * Get all product updates for deltaDays back. 
	 * @param maxId the last prod id of the previous search.  Overrides the next page property 
	 * @param subsetSize number of records to return in each call.  Default is 200.
	 * @param nextPage URL for the next page of URL's.  Leave null for the first page
	 * @param deltaDays number of days back to look for product updates
	 * @return
	 * @throws NotFoundException
	 */
	public ProductCollection getDeltaProducts (Long maxId, Integer subsetSize, String nextPage, Integer deltaDays) throws NotFoundException {
		String endpoint = null;
		if (nextPage == null) {
			endpoint = String.format("%s%s/%d", PRODUCT_ENDPOINT, DELTA_ENDPOINT, deltaDays);
		} else {
			endpoint = nextPage;
		}

		return this.getAllProducts(maxId, subsetSize, endpoint);
	}

	public Product getProduct(Long productId) throws NotFoundException {
		try {
			String jsonProducts = null;

			try {
				jsonProducts = ApiHubHelper.getApiHubEndpoint(PRODUCT_ENDPOINT, new String[] { productId.toString() });
			} catch (MalformedURLException e) {
				throw new NotFoundException(
						"Malformed URL exception when looking for product " + productId + " e: " + e.toString());
			} catch (IOException e) {
				throw new NotFoundException(
						"Buffer IO exception when looking for product " + productId + " e: " + e.toString());
			}
			Gson gson = new Gson();

			Product product = gson.fromJson(jsonProducts, Product.class);

			return product;
		} catch (IndexOutOfBoundsException e) {
			throw new NotFoundException("Product not found with an ID: " + productId);
		}
	}
	
	private ProductCollection processProductService (String endpoint, String [] args, Map<String, String> params) throws NotFoundException{
		String jsonProducts = null;

		try {
			jsonProducts = ApiHubHelper.getApiHubEndpoint(endpoint, args, params);

		} catch (MalformedURLException e) {
			throw new NotFoundException("Malformed URL exception when looking for all products e: " + e.toString());
		} catch (IOException e) {
			throw new NotFoundException("Buffer IO exception when looking for product " + "e: " + e.toString());
		}
		Gson gson = new Gson();

		ProductCollection products = gson.fromJson(jsonProducts, ProductCollection.class);

		return products;
	}
}
