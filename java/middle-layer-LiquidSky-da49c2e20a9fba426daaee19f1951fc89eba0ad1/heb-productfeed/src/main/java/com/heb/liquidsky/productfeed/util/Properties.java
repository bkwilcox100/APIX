package com.heb.liquidsky.productfeed.util;

public class Properties {
	public static final Long TEST_SKU = 12345678900L;
	public static final int API_HUB_TIMEOUT = 60;
	public static final String API_HUB_KEY_HEADER_NAME = "apikey";

	// Product Feed Constants
	private int MAX_PAGES_TO_IMPORT;
	private int DEFAULT_DELTA_DAYS;
	private boolean TEST_PHASE;

	// API Hub Constants
	private String API_HUB_BASE_URL;
	private String API_HUB_KEY; 
	
	private static Properties properties = null;

	private Properties() {
		MAX_PAGES_TO_IMPORT = 2;
		DEFAULT_DELTA_DAYS = 1;
		TEST_PHASE = true;
	
		// API Hub Constants
		API_HUB_BASE_URL = "https://openapi.heb.com/";
		API_HUB_KEY = "l7xxa9145772ae6244b785890bb60ce55ba1"; 
		
	}
	
	public static Properties i() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	public int getMaxPagesToImport() {
		return MAX_PAGES_TO_IMPORT;
	}

	public int getDefaultDeltaDays() {
		return DEFAULT_DELTA_DAYS;
	}

	public boolean isTestPhase() {
		return TEST_PHASE;
	}

	public String getApiHubBaseUrl() {
		return API_HUB_BASE_URL;
	}

	public String getApiHubKey() {
		return API_HUB_KEY;
	}
	
	
}
