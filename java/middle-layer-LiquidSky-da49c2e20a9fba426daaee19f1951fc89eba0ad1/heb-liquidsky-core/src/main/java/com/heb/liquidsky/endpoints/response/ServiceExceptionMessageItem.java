package com.heb.liquidsky.endpoints.response;

import java.util.HashMap;
import java.util.Map;

public class ServiceExceptionMessageItem {

	protected static final String ATTRIBUTE_CODE = "code";
	protected static final String ATTRIBUTE_TEXT = "text";
	protected static final String PLACEHOLDER_MESSAGE_CODE = null;

	private final String code;
	private final String text;

	public ServiceExceptionMessageItem(String text) {
		this.text = text;
		this.code = PLACEHOLDER_MESSAGE_CODE;
	}

	public ServiceExceptionMessageItem(String text, String code) {
		this.text = text;
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public String getText() {
		return this.text;
	}

	public Map<String, Object> toSpringAttributeMap() {
		Map<String, Object> springAttributeMap = new HashMap<>();
		springAttributeMap.put(ATTRIBUTE_CODE, this.getCode());
		springAttributeMap.put(ATTRIBUTE_TEXT, this.getText());
		return springAttributeMap;
	}

}
