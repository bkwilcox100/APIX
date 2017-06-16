package com.heb.liquidsky.endpoints.response;

import java.util.HashMap;
import java.util.Map;

public class ServiceExceptionErrorItem {

	protected static final String ATTRIBUTE_OBJECT = "object";

	private final ServiceException exception;
	private final Map<String, Object> object;

	public ServiceExceptionErrorItem(ServiceException exception, Map<String, Object> object) {
		this.exception = exception;
		this.object = object;
	}

	public ServiceException getException() {
		return this.exception;
	}

	public Map<String, Object> getObject() {
		return this.object;
	}

	public Map<String, Object> toSpringAttributeMap() {
		Map<String, Object> springAttributeMap = new HashMap<>();
		if (this.getException() != null) {
			springAttributeMap.put(ServiceException.ATTRIBUTE_MESSAGE, this.getException().getMessageItem().toSpringAttributeMap());
			springAttributeMap.put(ServiceException.ATTRIBUTE_STATUS, this.getException().getStatusCode());
		}
		if (this.getObject() != null) {
			springAttributeMap.put(ATTRIBUTE_OBJECT, this.getObject());
		}
		return springAttributeMap;
	}
}
