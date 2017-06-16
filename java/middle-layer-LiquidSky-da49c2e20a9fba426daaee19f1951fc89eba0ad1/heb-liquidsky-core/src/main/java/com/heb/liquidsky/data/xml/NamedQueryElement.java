package com.heb.liquidsky.data.xml;

import org.w3c.dom.Element;

/**
 * This class is responsible for processing the
 * &lt;named-query> element in a data-store.xml file, allowing the
 * element to be converted into a Java object.
 */
public class NamedQueryElement extends AbstractDataStoreElement {

	private final String name;
	private final String sql;

	public NamedQueryElement(Element element) {
		this.name = this.getStringValue(element, "name");
		this.sql = element.getTextContent().trim();
	}

	public String getName() {
		return this.name;
	}

	public String getSql() {
		return this.sql;
	}
}
