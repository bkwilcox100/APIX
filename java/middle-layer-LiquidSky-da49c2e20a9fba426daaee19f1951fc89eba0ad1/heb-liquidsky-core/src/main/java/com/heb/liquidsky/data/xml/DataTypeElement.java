package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class is responsible for processing the &lt;data-type>
 * element in a data-store.xml file, allowing the element to be converted
 * into a Java object.
 */
public class DataTypeElement extends AbstractDataStoreElement {

	private final List<CustomPropertyElement> customProperties;
	private final boolean fcmEnabled;
	private final String idPrefix;
	private final String idProperty;
	private final String name;
	private final List<NamedQueryElement> namedQueries;
	private final boolean pubSubEnabled;
	private final List<TableElement> tables;
	private final boolean useIdGenerator;

	public DataTypeElement(Element element) {
		List<Element> customPropertyElements = this.parseNodeList(element, "custom-property");
		this.customProperties = customPropertyElements.isEmpty() ? Collections.<CustomPropertyElement> emptyList() : new ArrayList<CustomPropertyElement>();
		for (Element childElement : customPropertyElements) {
			this.customProperties.add(new CustomPropertyElement(childElement));
		}
		this.fcmEnabled = this.getBooleanValue(element, "fcm-enabled", false);
		this.idPrefix = this.getStringValue(element, "id-generator-prefix");
		this.idProperty = this.getStringValue(element, "id-property");
		this.name = this.getStringValue(element, "name");
		List<Element> namedQueryElements = this.parseNodeList(element, "named-query");
		this.namedQueries = namedQueryElements.isEmpty() ? Collections.<NamedQueryElement> emptyList() : new ArrayList<NamedQueryElement>();
		for (Element childElement : namedQueryElements) {
			this.namedQueries.add(new NamedQueryElement(childElement));
		}
		this.pubSubEnabled = this.getBooleanValue(element, "pub-sub-enabled", false);
		List<Element> tableElements = this.parseNodeList(element, "table");
		this.tables = tableElements.isEmpty() ? Collections.<TableElement> emptyList() : new ArrayList<TableElement>();
		for (Element childElement : tableElements) {
			this.tables.add(new TableElement(childElement));
		}
		this.useIdGenerator = this.getBooleanValue(element, "use-id-generator", true);
	}

	public List<CustomPropertyElement> getCustomProperties() {
		return this.customProperties;
	}

	public boolean isFcmEnabled() {
		return this.fcmEnabled;
	}

	public String getIdPrefix() {
		return this.idPrefix;
	}

	public String getIdProperty() {
		return this.idProperty;
	}

	public String getName() {
		return this.name;
	}

	public List<NamedQueryElement> getNamedQueries() {
		return this.namedQueries;
	}

	public boolean isPubSubEnabled() {
		return this.pubSubEnabled;
	}

	public List<TableElement> getTables() {
		return this.tables;
	}

	public boolean isUseIdGenerator() {
		return this.useIdGenerator;
	}
}
