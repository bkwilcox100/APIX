package com.heb.liquidsky.data.custom;

import java.util.Map;

public abstract class AbstractCustomPropertyDescriptor implements CustomPropertyImplementation {

	private final Map<String, String> attributes;

	protected AbstractCustomPropertyDescriptor(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	private Map<String, String> getAttributes() {
		return this.attributes;
	}

	/**
	 * Return the name of an attribute configured for the custom property.
	 */
	@Override
	public String getAttributeValue(String attributeName) {
		return this.getAttributes().get(attributeName);
	}

	public boolean isReadOnly() {
		return true;
	}
}
