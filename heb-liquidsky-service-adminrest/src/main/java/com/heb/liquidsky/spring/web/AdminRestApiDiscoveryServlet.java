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

import com.heb.liquidsky.endpoints.AdminRestApiDiscoveryInterface;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.EndpointUtils;

/**
 * Defines the endpoint servlet for Admin Rest API Discovery resources.
 * 
 * @author Scott McArthur
 */
@RestController
@RequestMapping(value="/adminrest/v1")
public class AdminRestApiDiscoveryServlet {

	private static final AdminRestApiDiscoveryInterface INTERFACE_OBJECT = new AdminRestApiDiscoveryInterface();

	@PostMapping(value="/apicollection")
	public Map<String, Object> createBatchApiCollection(@RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.createBatchApiCollection(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@PostMapping(value="/apicollection/{collectionId}/serviceDescriptions")
	public Map<String, Object> createBatchServiceDescription(@PathVariable String collectionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.createBatchServiceDescription(EndpointUtils.getRequestBodyAsJsonElement(body), collectionId);
	}

	@PostMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions")
	public Map<String, Object> createBatchServiceVersion(@PathVariable String serviceDescriptionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.createBatchServiceVersion(EndpointUtils.getRequestBodyAsJsonElement(body), serviceDescriptionId);
	}

	@PostMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths")
	public Map<String, Object> createBatchResourcePath(@PathVariable String serviceVersionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.createBatchResourcePath(EndpointUtils.getRequestBodyAsJsonElement(body), serviceVersionId);
	}

	@GetMapping(value="/apicollection")
	public List<Map<String, Object>> readApiCollectionCollection() throws ServiceException {
		return INTERFACE_OBJECT.readApiCollectionCollection();
	}

	@GetMapping(value="/apicollection/{collectionId}")
	public Map<String, Object> readApiCollectionResource(@PathVariable String collectionId) throws ServiceException {
		return INTERFACE_OBJECT.readApiCollectionResource(collectionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions")
	public List<Map<String, Object>> readServiceDescriptionCollection(@PathVariable String collectionId) throws ServiceException {
		return INTERFACE_OBJECT.readServiceDescriptionCollection(collectionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}")
	public Map<String, Object> readServiceDescriptionResource(@PathVariable String collectionId, @PathVariable String serviceDescriptionId) throws ServiceException {
		return INTERFACE_OBJECT.readServiceDescriptionResource(collectionId, serviceDescriptionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions")
	public List<Map<String, Object>> readServiceVersionCollection(@PathVariable String serviceDescriptionId) throws ServiceException {
		return INTERFACE_OBJECT.readServiceVersionCollection(serviceDescriptionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}")
	public Map<String, Object> readServiceVersionResource(@PathVariable String serviceDescriptionId, @PathVariable String serviceVersionId) throws ServiceException {
		return INTERFACE_OBJECT.readServiceVersionResource(serviceDescriptionId, serviceVersionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths")
	public List<Map<String, Object>> readResourcePathCollection(@PathVariable String serviceVersionId) throws ServiceException {
		return INTERFACE_OBJECT.readResourcePathCollection(serviceVersionId);
	}

	@GetMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths/{resourcePathId}")
	public Map<String, Object> readResourcePathResource(@PathVariable String serviceVersionId, @PathVariable String resourcePathId) throws ServiceException {
		return INTERFACE_OBJECT.readResourcePathResource(serviceVersionId, resourcePathId);
	}

	@PutMapping(value="/apicollection/{collectionId}")
	public Map<String, Object> updateApiCollectionResource(@PathVariable String collectionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.updateApiCollectionResource(EndpointUtils.getRequestBodyAsJsonElement(body), collectionId);
	}

	@PutMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}")
	public Map<String, Object> updateServiceDescriptionResource(@PathVariable String collectionId, @PathVariable String serviceDescriptionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.updateServiceDescriptionResource(EndpointUtils.getRequestBodyAsJsonElement(body), collectionId, serviceDescriptionId);
	}

	@PutMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}")
	public Map<String, Object> updateServiceVersionResource(@PathVariable String serviceDescriptionId, @PathVariable String serviceVersionId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.updateServiceVersionResource(EndpointUtils.getRequestBodyAsJsonElement(body), serviceDescriptionId, serviceVersionId);
	}

	@PutMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths/{resourcePathId}")
	public Map<String, Object> updateResourcePathResource(@PathVariable String serviceVersionId, @PathVariable String resourcePathId, @RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.updateResourcePathResource(EndpointUtils.getRequestBodyAsJsonElement(body), serviceVersionId, resourcePathId);
	}

	@DeleteMapping(value="/apicollection")
	public Map<String, Object> deleteBatchApiCollectionResource(@RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.deleteBatchApiCollectionResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@DeleteMapping(value="/apicollection/{collectionId}")
	public Map<String, Object> deleteApiCollectionResource(@PathVariable String collectionId) throws ServiceException {
		return INTERFACE_OBJECT.deleteApiCollectionResource(collectionId);
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions")
	public Map<String, Object> deleteBatchServiceDescriptionResource(@RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.deleteBatchServiceDescriptionResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}")
	public Map<String, Object> deleteServiceDescriptionResource(@PathVariable String serviceDescriptionId) throws ServiceException {
		return INTERFACE_OBJECT.deleteServiceDescriptionResource(serviceDescriptionId);
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions")
	public Map<String, Object> deleteBatchServiceVersionResource(@RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.deleteBatchServiceVersionResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}")
	public Map<String, Object> deleteServiceVersionResource(@PathVariable String serviceVersionId) throws ServiceException {
		return INTERFACE_OBJECT.deleteServiceVersionResource(serviceVersionId);
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths")
	public Map<String, Object> deleteBatchResourcePathResource(@RequestBody String body) throws ServiceException {
		return INTERFACE_OBJECT.deleteBatchResourcePathResource(EndpointUtils.getRequestBodyAsJsonElement(body));
	}

	@DeleteMapping(value="/apicollection/{collectionId}/serviceDescriptions/{serviceDescriptionId}/serviceVersions/{serviceVersionId}/resourcePaths/{resourcePathId}")
	public Map<String, Object> deleteResourcePathResource(@PathVariable String resourcePathId) throws ServiceException {
		return INTERFACE_OBJECT.deleteResourcePathResource(resourcePathId);
	}
}
