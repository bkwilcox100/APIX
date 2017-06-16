package com.heb.liquidsky.endpoints.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	protected static final String ATTRIBUTE_ERRORS = "errors";
	protected static final String ATTRIBUTE_MESSAGE = "message";
	protected static final String ATTRIBUTE_STATUS = "status";

	private List<ServiceExceptionErrorItem> errors;
	private final ServiceExceptionMessageItem messageItem;

	public ServiceException(String message) {
		super(message);
		this.messageItem = new ServiceExceptionMessageItem(message);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
		this.messageItem = new ServiceExceptionMessageItem(message);
	}

	public ServiceException(String message, String messageCode) {
		super(message);
		this.messageItem = new ServiceExceptionMessageItem(message, messageCode);
	}

	public ServiceException(String message, String messageCode, Throwable t) {
		super(message, t);
		this.messageItem = new ServiceExceptionMessageItem(message, messageCode);
	}

	public abstract int getStatusCode();

	public ServiceExceptionMessageItem getMessageItem() {
		return this.messageItem;
	}

	public List<ServiceExceptionErrorItem> getErrors() {
		return this.errors;
	}

	public void addError(ServiceExceptionErrorItem error) {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		this.errors.add(error);
	}

	public void setErrors(List<ServiceExceptionErrorItem> errors) {
		this.errors = errors;
	}

	/**
	 * Return a map corresponding to the attributes to be returned
	 * to the end user when this exception handled by Spring.  Note
	 * that these attributes will be appended onto the default Spring
	 * attributes, overwriting any values that use the same key.
	 * 
	 * Child exceptions should override this method to include any
	 * exception-specific attributes.
	 * 
	 * @see HebRestExceptionHandler.errorAttributes
	 */
	public Map<String, Object> toSpringAttributeMap() {
		Map<String, Object> springAttributeMap = new HashMap<>();
		springAttributeMap.put(ATTRIBUTE_STATUS, this.getStatusCode());
		springAttributeMap.put(ATTRIBUTE_MESSAGE, this.getMessageItem().toSpringAttributeMap());
		if (this.getErrors() != null && !this.getErrors().isEmpty()) {
			List<Map<String, Object>> errorList = new ArrayList<>();
			for (ServiceExceptionErrorItem error : this.getErrors()) {
				errorList.add(error.toSpringAttributeMap());
			}
			springAttributeMap.put(ATTRIBUTE_ERRORS, errorList);
		}
		return springAttributeMap;
	}
}
