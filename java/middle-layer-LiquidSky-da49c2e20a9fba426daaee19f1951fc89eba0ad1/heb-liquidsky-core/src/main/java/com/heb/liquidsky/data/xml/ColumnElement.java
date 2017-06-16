package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class is responsible for processing the &lt;column>
 * element in a data-store.xml file, allowing the element to be converted
 * into a Java object.
 */
public class ColumnElement extends AbstractDataStoreElement {

	private final List<PropertyAttributeElement> attributes;
	private final boolean cascadeDelete;
	private final String columnName;
	private final String itemType;
	private final String listItemType;
	private final String property;
	private boolean readOnly = false;

	public ColumnElement(Element element) {
		this.cascadeDelete = this.getBooleanValue(element, "cascade", false);
		this.columnName = this.getStringValue(element, "column-name");
		this.itemType = this.getStringValue(element, "item-type");
		this.listItemType = this.getStringValue(element, "list-item-type");
		this.property = this.getStringValue(element, "property");
		this.readOnly = this.getBooleanValue(element, "read-only", false);
		
		List<Element> attributeElements = this.parseNodeList(element, "attribute");
		this.attributes = attributeElements.isEmpty() ? Collections.<PropertyAttributeElement> emptyList() : new ArrayList<PropertyAttributeElement>();
		for (Element childElement : attributeElements) {
			this.attributes.add(new PropertyAttributeElement(childElement));
		}
	}

	public ColumnElement(String columnName, String property) {
		this.cascadeDelete = false;
		this.columnName = columnName;
		this.itemType = null;
		this.listItemType = null;
		this.property = property;
		this.readOnly = true;
		this.attributes = Collections.<PropertyAttributeElement> emptyList();
	}

	public boolean isCascadeDelete() {
		return this.cascadeDelete;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public String getItemType() {
		return this.itemType;
	}

	public String getListItemType() {
		return this.listItemType;
	}

	public String getProperty() {
		return this.property;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public List<PropertyAttributeElement> getAttributes() {
		return this.attributes;
	}
	
}
