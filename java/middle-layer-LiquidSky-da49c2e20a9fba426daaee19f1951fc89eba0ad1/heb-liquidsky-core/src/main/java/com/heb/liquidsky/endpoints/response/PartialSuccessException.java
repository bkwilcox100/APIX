package com.heb.liquidsky.endpoints.response;

import java.util.List;
import java.util.Map;

/**
 * Implement an exception indicating that the operation was only
 * partially successful.
 */
public class PartialSuccessException extends ServiceException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = 207;
	private static final String ATTRIBUTE_SUCCESS = "success";

	private final List<String> successes;

	public PartialSuccessException(String message, List<String> successes, List<ServiceExceptionErrorItem> errors) {
		super(message);
		this.setErrors(errors);
		this.successes = successes;
	}

	public PartialSuccessException(String message, String messageCode, List<String> successes, List<ServiceExceptionErrorItem> errors) {
		super(message, messageCode);
		this.setErrors(errors);
		this.successes = successes;
	}

	public int getStatusCode() {
		return CODE;
	}

	public List<String> getSuccesses() {
		return this.successes;
	}

	@Override
	public Map<String, Object> toSpringAttributeMap() {
		Map<String, Object> springAttributeMap = super.toSpringAttributeMap();
		if (this.getSuccesses() != null && !this.getSuccesses().isEmpty()) {
			springAttributeMap.put(ATTRIBUTE_SUCCESS, this.getSuccesses());
		}
		return springAttributeMap;
	}
}
