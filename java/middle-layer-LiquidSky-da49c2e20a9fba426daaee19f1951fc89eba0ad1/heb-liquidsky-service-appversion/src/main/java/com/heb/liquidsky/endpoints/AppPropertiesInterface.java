package com.heb.liquidsky.endpoints;

import java.util.Map;

import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.ResourceUtils;

/**
 * Defines v1 of the App Properties services for the App Version service
 * AppProperties define various attributes of an app. 
 * This is mainly used to replace what has been known as the App Version Service or Forced upgrade service 
 * by using the AppVersion data contained within the AppProperties object.
 * 
 * @author Scott McArthur
 *
 */
public class AppPropertiesInterface {

	private static final String CONTEXT_FILTER = "default";
	
	private static final String DATA_ITEM_NAME_APP_PROPERTIES = "appProperties";
	
	public Map<String, Object> readAppPropertiesResource(String appId) throws ServiceException {
		return ResourceUtils.readResource(appId, DATA_ITEM_NAME_APP_PROPERTIES, CONTEXT_FILTER);
	}

}