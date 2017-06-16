package com.heb.liquidsky.apihub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.heb.liquidsky.productfeed.util.Properties;

public class ApiHubHelper {

	// ====---- Helper Methods ----====  //
	
	/*
	 * getApiHubEndpoint
	 * Parameters
	 * 	see API hub documentation here:  https://confluence.heb.com:8443/display/ESELLING/API+ECOMM+HUB?preview=/30442694/37455836/WebserviceAPISpecificationDocTemplate%2009-29-16.docx
	 * 	apiHubEndpoint - The endpoint to hit on the API hub.  /products, priceLocations/stores/, /categories
	 * 	args (optional) - an array of arguments to be sent to the API hub.  
	 * 			this is for specifying variable values as part of the endpoint URL 
	 * 			like for /products/delta/{days} this would hold ["delta", "days"]
	 *	queryParams (optional) - a map of query parameters to append to the endpoint 
	 *				e.g. /products/12345?sale=true but that is not a real example as there are no API hub endpoints that accept query parameters yet.
	 */
	
	static public String getApiHubEndpoint(String apiHubEndpoint) throws MalformedURLException, IOException{
		return getApiHubEndpoint(apiHubEndpoint, new String[] {}, new HashMap<String, String>());
	}
	
	static public String getApiHubEndpoint(String apiHubEndpoint, String[] args) throws MalformedURLException, IOException{
		return getApiHubEndpoint(apiHubEndpoint, args, new HashMap<String, String>());
	}
	
	static public String getApiHubEndpoint(String apiHubEndpoint, String[] args, Map<String, String> queryParams) throws MalformedURLException, IOException{
		StringBuffer apiHubResponse = new StringBuffer();
		String line;
		StringBuilder processedArgs = new StringBuilder();
		StringBuilder processedQueryParams = new StringBuilder();
		
		// Convert the array to be URL type arguments such as product ID
		for (String arg  : args) {
			processedArgs.append("/").append(arg);
		}
		
		// Convert the query params map to a query param string.
		if (queryParams.entrySet().size() > 0) {
			processedQueryParams.append("?");
			for (Map.Entry<String, String> entry : queryParams.entrySet())
			{
			    processedQueryParams.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
		}
		
		BufferedReader reader =  null;

		try{
			//  Make connection to API Hub
			URL url = new URL(Properties.i().getApiHubBaseUrl() + apiHubEndpoint + processedArgs.toString() + processedQueryParams.toString());
			HttpURLConnection apiHubConnection = (HttpURLConnection)url.openConnection();
			apiHubConnection.setReadTimeout(Properties.API_HUB_TIMEOUT * 1000);
			apiHubConnection.setConnectTimeout(Properties.API_HUB_TIMEOUT * 1000);
			apiHubConnection.setRequestProperty (Properties.API_HUB_KEY_HEADER_NAME, Properties.i().getApiHubKey());
			apiHubConnection.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader( apiHubConnection.getInputStream() ));

			//Read the response
			while ((line = reader.readLine()) != null) {
				apiHubResponse.append(line);
			}
		    
		} catch (MalformedURLException e) {
			throw new MalformedURLException("URL Exception in getApiHubEndpoint: " + e.toString());
		} catch (IOException e) {
			throw new IOException("IO Exception in getApiHubEndpoint. error: " + e.toString(), e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return apiHubResponse.toString();
	}
}