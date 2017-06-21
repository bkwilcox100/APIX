package com.heb.liquidsky.spring.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.endpoints.AdminRestAppPropertiesInterface;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.EndpointUtils;

/**
 * Defines the endpoint servlet that handles App Properties requests for the Admin Rest service.
 * App Properties contains App Version, but is designed to be able to be extended to add additional app properties as needed.
 * 
 * @author Scott McArthur
 *
 */
@RestController
@RequestMapping(value="/adminrest/v1")
public class AdminRestAppVersionServlet {

	private static final AdminRestAppPropertiesInterface AP_APP_VERSION_INTERFACE = new AdminRestAppPropertiesInterface();

	@PostMapping(value="/appproperties")
	public Map<String, Object> createBatchApiCollection(@RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.createBatchAppProperties(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@PostMapping(value="/appproperties/{appId}/appversion")
	public Map<String, Object> createBatchAppVersion(@PathVariable String appId, @RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.createBatchAppVersion(EndpointUtils.getRequestBodyAsJsonElement(body), appId);
	}

	@GetMapping(value="/appproperties/{appId}/appversion/{appVersionId}")
	public Map<String, Object> readAppVersionResource(@PathVariable String appId, @PathVariable String appVersionId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.readAppVersionResource(appId, appVersionId);
	}

	@GetMapping(value="/appproperties/{appId}")
	public Map<String, Object> readAppPropertiesResource(@PathVariable String appId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.readAppPropertiesResource(appId);
	}

	@GetMapping(value="/appproperties")
	public List<Map<String, Object>> readAppPropertiesCollection() throws ServiceException {
		return AP_APP_VERSION_INTERFACE.readAppPropertiesCollection();
	}

	@GetMapping(value="/appproperties/{appId}/appversion")
	public List<Map<String, Object>> readAppVersionCollection(@PathVariable String appId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.readAppVersionCollection(appId);
	}

	@PutMapping(value="/appproperties/{appId}")
	public Map<String, Object> updateApiCollectionResource(@PathVariable String appId, @RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.updateAppPropertiesResource(EndpointUtils.getRequestBodyAsJsonElement(body), appId);
	}

	@PutMapping(value="/appproperties/{appId}/appversion/{appVersionId}")
	public Map<String, Object> updateAppVersionResource(@PathVariable String appId, @PathVariable String appVersionId, @RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.updateAppVersionResource(EndpointUtils.getRequestBodyAsJsonElement(body), appId, appVersionId);
	}

	@DeleteMapping(value="/appproperties/{appId}")
	public Map<String, Object> deleteAppPropertiesResource(@PathVariable String appId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.deleteAppPropertiesResource(appId);
	}

	@DeleteMapping(value="/appproperties")
	public Map<String, Object> deleteBatchAppPropertiesResource(@RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.deleteBatchAppPropertiesResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@DeleteMapping(value="/appproperties/{appId}/appversion/{appVersionId}")
	public Map<String, Object> deleteAppVersionResource(@PathVariable String appId, @PathVariable String appVersionId) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.deleteAppVersionResource(appId, appVersionId);
	}

	@DeleteMapping(value="/appproperties/{appId}/appversion")
	public Map<String, Object> deleteBatchAppVersionResource(@RequestBody String body) throws ServiceException {
		return AP_APP_VERSION_INTERFACE.deleteBatchAppVersionResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}
}
