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

import com.heb.liquidsky.endpoints.AdminRestProjectPropertyService;
import com.heb.liquidsky.endpoints.response.ServiceException;

/**
 * Defines the endpoint servlet for managing application properties
 * in Cloud Storage.
 * 
 * @author Ryan Holliday
 */
@RestController
@RequestMapping(value="/adminrest/v1")
public class AdminRestProjectPropertyServlet {

	private static final AdminRestProjectPropertyService SERVICE_OBJECT = new AdminRestProjectPropertyService();

	@GetMapping(value="/projectProperties/{folder}")
	public Map<String, Object> readApplicationPropertiesForFolder(@PathVariable String folder) throws ServiceException {
		return SERVICE_OBJECT.readApplicationPropertiesForFolder(folder);
	}

	@PostMapping(value="/projectProperties/{folder}")
	public Map<String, Object> createApplicationProperties(@PathVariable String folder, @RequestBody List<Map<String, Object>> jsonBody) throws ServiceException {
		return SERVICE_OBJECT.createApplicationProperties(folder, jsonBody);
	}

	@PutMapping(value="/projectProperties/{folder}")
	public Map<String, Object> updateApplicationProperties(@PathVariable String folder, @RequestBody List<Map<String, Object>> jsonBody) throws ServiceException {
		return SERVICE_OBJECT.updateApplicationProperties(folder, jsonBody);
	}

	@DeleteMapping(value="/projectProperties/{folder}/{propertyName}")
	public List<String> deleteApplicationProperty(@PathVariable String folder, @PathVariable String propertyName) throws ServiceException {
		return SERVICE_OBJECT.deleteApplicationProperty(folder, propertyName);
	}
}
