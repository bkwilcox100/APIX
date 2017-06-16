package com.heb.liquidsky.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.BatchOperationResult;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.NotFoundException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.response.ServiceExceptionErrorItem;

/**
 * Implementation class for performing management tasks for
 * application properties in Cloud Storage.
 * 
 * @author Ryan Holliday
 */
public class AdminRestProjectPropertyService {

	private static final String JSON_FOLDER_NAME = "name";
	private static final String JSON_FOLDER_PROPERTIES = "properties";
	private static final String JSON_PROPERTY_NAME = "propertyName";
	private static final String JSON_PROPERTY_VALUE = "propertyValue";
	enum WRITE_MODE {CREATE, UPDATE};

	public Map<String, Object> readApplicationPropertiesForFolder(String folder) throws InternalServerErrorException {
		List<String> propertyNames = null;
		try {
			propertyNames = CloudUtil.getPropertyNamesInFolder(folder);
		} catch (IOException e) {
			throw new InternalServerErrorException("Failure while reading properties from Cloud Storage for folder " + folder, e);
		}
		return this.generateSuccessObject(folder, propertyNames);
	}

	public Map<String, Object> createApplicationProperties(String folder, List<Map<String, Object>> jsonBody) throws ServiceException {
		return this.writeProperties(folder, jsonBody, WRITE_MODE.CREATE);
	}

	public List<String> deleteApplicationProperty(String folder, String propertyName) throws ServiceException {
		if (!this.propertyExists(folder, propertyName)) {
			throw new NotFoundException("Property " + propertyName + " does not exist in folder " + folder);
		}
		try {
			CloudUtil.deletePropertyFileFromStorage(folder, propertyName);
			List<String> result = new ArrayList<>();
			result.add(folder + "/" + propertyName);
			return result;
		} catch (IOException e) {
			throw new InternalServerErrorException("Failure while deleting property " + propertyName + " in folder " + folder, e);
		}
	}

	public Map<String, Object> updateApplicationProperties(String folder, List<Map<String, Object>> jsonBody) throws ServiceException {
		return this.writeProperties(folder, jsonBody, WRITE_MODE.UPDATE);
	}

	private boolean propertyExists(String folder, String propertyName) throws InternalServerErrorException {
		try {
			return (CloudUtil.propertyExistsInStorage(folder, propertyName));
		} catch (IOException e) {
			throw new InternalServerErrorException("Failure while looking up property " + propertyName + " in folder " + folder, e);
		}
	}

	private Map<String, Object> writeProperties(String folder, List<Map<String, Object>> jsonBody, WRITE_MODE writeMode) throws ServiceException {
		BatchOperationResult batchResult = new BatchOperationResult();
		List<String> successes = new ArrayList<>();
		for (Map<String, Object> propertyValueMap : jsonBody) {
			try {
				String result = this.writeProperty(folder, propertyValueMap, writeMode);
				successes.add(result);
				batchResult.addSuccess(result);
			} catch (ServiceException e) {
				batchResult.addError(new ServiceExceptionErrorItem(e, propertyValueMap));
			}
		}
		return batchResult.generateResponse(this.generateSuccessObject(folder, successes));
	}

	private String writeProperty(String folder, Map<String, Object> jsonBody, WRITE_MODE writeMode) throws ServiceException {
		if (!jsonBody.containsKey(JSON_PROPERTY_NAME)) {
			throw new BadRequestException("Request does not contain " + JSON_PROPERTY_NAME);
		}
		String propertyName = jsonBody.get(JSON_PROPERTY_NAME).toString();
		if (!jsonBody.containsKey(JSON_PROPERTY_VALUE)) {
			throw new BadRequestException("Request does not contain " + JSON_PROPERTY_VALUE);
		}
		String propertyValue = jsonBody.get(JSON_PROPERTY_VALUE).toString();
		try {
			if (writeMode == WRITE_MODE.CREATE) {
				if (this.propertyExists(folder, propertyName)) {
					throw new NotFoundException("Property " + propertyName + " already exists in folder " + folder);
				}
				CloudUtil.createPropertyFileInStorage(folder, propertyName, propertyValue);
			} else if (writeMode == WRITE_MODE.UPDATE) {
				if (!this.propertyExists(folder, propertyName)) {
					throw new NotFoundException("Property " + propertyName + " does not exist in folder " + folder);
				}
				CloudUtil.updatePropertyFileInStorage(folder, propertyName, propertyValue);
			}
		} catch (IOException e) {
			throw new InternalServerErrorException("Failure while writing property " + propertyName + " in folder " + folder + " to Cloud Storage", e);
		}
		return folder + "/" + propertyName;
	}

	private Map<String, Object> generateSuccessObject(String folder, List<String> propertyNames) {
		Map<String, Object> results = new HashMap<>();
		results.put(JSON_FOLDER_NAME, folder);
		results.put(JSON_FOLDER_PROPERTIES, propertyNames);
		return results;
	}
}
