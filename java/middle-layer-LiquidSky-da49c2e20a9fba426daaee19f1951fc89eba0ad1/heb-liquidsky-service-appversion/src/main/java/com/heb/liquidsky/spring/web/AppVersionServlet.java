package com.heb.liquidsky.spring.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.endpoints.AppPropertiesInterface;
import com.heb.liquidsky.endpoints.response.ServiceException;

/**
 * Defines the endpoint servlet that handles App Properties requests for the App Version service
 * App Properties contains App Version, but is designed to be able to be extended to add additional app properties as needed.
 * 
 * @author Scott McArthur
 */
@RestController
@RequestMapping(value="/appversion/v1")
public class AppVersionServlet {

	private static final AppPropertiesInterface AP_APP_VERSION_INTERFACE = new AppPropertiesInterface();

	/**
	 * Default customer facing call.  This just returns the App Properties for the specified appId
	 * 
	 * @param appId
	 * @return
	 * @throws ServiceException
	 */
	@GetMapping(value="/appproperties/{appId}")
	public Map<String, Object> readAppPropertiesResource(@PathVariable String appId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.readAppPropertiesResource(appId);
	}

	/**
	 * DEPRECATED
	 * For Legacy Support Only!
	 * This returns the default App Version in the same format as the legacy version.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/appversion")
	public Map<String, Object> readLegacyAppVersion() throws ServiceException {
		Map<String, Object> appProperties = AP_APP_VERSION_INTERFACE.readAppPropertiesResource("default");
		Map<String, Object> returnValue = new HashMap<String, Object>();
		Map<String, String> legacyVersionInformation = null;
		List<Map<String, String>> legacyAppVersionList = new ArrayList<Map<String, String>>();
		
		if (appProperties.containsKey("appVersions")){
			List<Map<String, Object>> appVersionList = (List<Map<String, Object>>) appProperties.get("appVersions");
			for (Map<String, Object> currentItem : appVersionList) {
				legacyVersionInformation = new HashMap<String, String>();
				if (currentItem.containsKey("osName") && currentItem.containsKey("osVersion")){
					legacyVersionInformation.put((String) currentItem.get("osName"), (String) currentItem.get("osVersion"));	
				} else {
					legacyVersionInformation.put("ERROR", "CANNOT FIND APP VERSION INFORMATION");
				}
				legacyAppVersionList.add(legacyVersionInformation);
			}
		}
		returnValue.put("AppLatestVersion", legacyAppVersionList);
		return returnValue;
	}
}
