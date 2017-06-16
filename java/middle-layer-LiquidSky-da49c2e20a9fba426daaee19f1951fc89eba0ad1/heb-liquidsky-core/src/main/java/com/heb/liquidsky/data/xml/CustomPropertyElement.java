package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class is responsible for processing the &lt;custom-property>
 * element in a data-store.xml file, allowing the element to be converted
 * into a Java object.
 */
public class CustomPropertyElement extends AbstractDataStoreElement {

	private final List<PropertyAttributeElement> attributes;
	private final String itemType;
	private final String listItemType;
	private final String propertyName;
	private final String propertyType;
	private final String source;

	public CustomPropertyElement(Element element) {
		List<Element> attributeElements = this.parseNodeList(element, "attribute");
		this.attributes = attributeElements.isEmpty() ? Collections.<PropertyAttributeElement> emptyList() : new ArrayList<PropertyAttributeElement>();
		for (Element childElement : attributeElements) {
			this.attributes.add(new PropertyAttributeElement(childElement));
		}
		this.itemType = this.getStringValue(element, "item-type");
		this.listItemType = this.getStringValue(element, "list-item-type");
		this.propertyName = this.getStringValue(element, "name");
		this.propertyType = this.getStringValue(element, "property-type");
		this.source = this.getStringValue(element, "source");
	}

	public List<PropertyAttributeElement> getAttributes() {
		return this.attributes;
	}

	public String getItemType() {
		return this.itemType;
	}

	public String getListItemType() {
		return this.listItemType;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public String getPropertyType() {
		return this.propertyType;
	}

	public String getSource() {
		return this.source;
	}
}
