package com.heb.liquidsky.data.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * This class is responsible for processing the &lt;table>
 * element in a data-store.xml file, allowing the element to be converted
 * into a Java object.
 */
public class TableElement extends AbstractDataStoreElement {

	private final List<ColumnElement> columns;
	private final String idColumn;
	private final boolean lazyLoad;
	private final String name;
	private final String type;

	public TableElement(Element element) {
		List<Element> columnElements = this.parseNodeList(element, "column");
		this.columns = columnElements.isEmpty() ? Collections.<ColumnElement> emptyList() : new ArrayList<ColumnElement>();
		for (Element childElement : columnElements) {
			this.columns.add(new ColumnElement(childElement));
		}
		this.idColumn = this.getStringValue(element, "id-column");
		this.lazyLoad = this.getBooleanValue(element, "lazy-load", false);
		this.name = this.getStringValue(element, "name");
		this.type = this.getStringValue(element, "type");
	}

	public List<ColumnElement> getColumns() {
		return this.columns;
	}

	public String getIdColumn() {
		return this.idColumn;
	}

	public boolean isLazyLoad() {
		return this.lazyLoad;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}
}
