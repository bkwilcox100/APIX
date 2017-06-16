package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class is the top-level class for processing
 * the &lt;data-store> element in a data-store.xml file, allowing the
 * file to be converted into a Java object.
 */
public class DataStoreElement extends AbstractDataStoreElement {

	private final List<DataTypeElement> dataTypes;

	public DataStoreElement(Element element) {
		List<Element> dataTypeElements = this.parseNodeList(element, "data-type");
		this.dataTypes = dataTypeElements.isEmpty() ? Collections.<DataTypeElement> emptyList() : new ArrayList<DataTypeElement>();
		for (Element dataTypeElement : dataTypeElements) {
			this.dataTypes.add(new DataTypeElement(dataTypeElement));
		}
	}

	public List<DataTypeElement> getDataTypes() {
		return this.dataTypes;
	}
}
