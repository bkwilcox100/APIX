package com.heb.liquidsky.data.xml;

import org.w3c.dom.Element;

public class PropertyAttributeElement extends AbstractDataStoreElement {

	private final String name;
	private final String value;

	public PropertyAttributeElement(Element element) {
		this.name = this.getStringValue(element, "name");
		this.value = this.getStringValue(element, "value");
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}
}
