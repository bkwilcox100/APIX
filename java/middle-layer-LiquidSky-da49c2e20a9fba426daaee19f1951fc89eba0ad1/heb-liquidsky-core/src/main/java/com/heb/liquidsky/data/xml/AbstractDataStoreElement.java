package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractDataStoreElement {

	protected static final Logger logger = Logger.getLogger(AbstractDataStoreElement.class.getName());

	protected boolean getBooleanValue(Element element, String attributeName, boolean defaultValue) {
		String value = this.getStringValue(element, attributeName);
		if (StringUtils.equalsIgnoreCase(value, "true")) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(value, "false")) {
			return false;
		}
		return defaultValue;
	}

	protected String getStringValue(Element element, String attributeName) {
		return (element.getAttributeNode(attributeName) != null) ? element.getAttribute(attributeName) : null;
	}

	protected List<Element> parseNodeList(Element parent, String childName) {
		NodeList childNodes = parent.getElementsByTagName(childName);
		int childNodeCount = childNodes.getLength();
		List<Element> childElements = (childNodeCount == 0) ? Collections.<Element> emptyList() : new ArrayList<Element>();
		for (int i=0; i < childNodeCount; i++) {
			childElements.add((Element) childNodes.item(i));
		}
		return childElements;
	}
}
