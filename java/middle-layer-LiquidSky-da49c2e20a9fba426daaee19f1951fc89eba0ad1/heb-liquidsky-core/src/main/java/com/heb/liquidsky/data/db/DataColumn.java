package com.heb.liquidsky.data.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.DataStoreProperty;
import com.heb.liquidsky.data.xml.ColumnElement;
import com.heb.liquidsky.data.xml.PropertyAttributeElement;

/**
 * This class is an object representation of a &lt;column>
 * configuration in the data-store.xml file.
 */
public class DataColumn implements DataStoreMarshaller, DataStoreProperty {

	private final boolean cascadeDelete;
	private final String columnName;
	private final String itemType;
	private final String listItemType;
	private final String property;
	private final boolean readOnly;
	private final DataTable table;
	private final Map<String,String> attributes;

	protected DataColumn(DataTable table, ColumnElement marshaller) throws DataStoreException {
		this.attributes = marshaller.getAttributes().isEmpty() ? Collections.<String,String>emptyMap() : new HashMap<String,String>();
		if(!marshaller.getAttributes().isEmpty()) {
			for (PropertyAttributeElement attribute : marshaller.getAttributes()) {
				this.attributes.put(attribute.getName(), attribute.getValue());
			}
		}
		this.cascadeDelete = marshaller.isCascadeDelete();
		this.columnName = marshaller.getColumnName();
		this.itemType = marshaller.getItemType();
		this.listItemType = marshaller.getListItemType();
		this.property = marshaller.getProperty();
		this.readOnly = marshaller.isReadOnly();
		this.table = table;
		this.validateConfig();
	}

	protected boolean isCascadeDelete() {
		return this.cascadeDelete;
	}

	protected String getColumnName() {
		return this.columnName;
	}

	@Override
	public boolean isDataItem() {
		return (this.getItemType() != null || this.getListItemType() != null);
	}

	@Override
	public String getItemType() {
		return this.itemType;
	}

	@Override
	public String getListItemType() {
		return this.listItemType;
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	@Override
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	@Override
	public Map<String,String> getAttributes() {
		return this.attributes;
	}
	
	@Override
	public String getAttributeByName(String name) {
		if(!getAttributes().isEmpty()) {
			return getAttributes().get(name);
		}
		return null;
	}

	protected DataTable getTable() {
		return this.table;
	}

	protected boolean isIdColumn() {
		return this.getProperty().equals(this.getTable().getDataType().getIdProperty());
	}

	/**
	 * Utility method used for determining whether to include this column
	 * in insert SQL statements.
	 */
	protected boolean isInsertable(boolean autoGenerateId) {
		if (this.isReadOnly()) {
			// do not include read-only columns in inserts
			return false;
		}
		if (autoGenerateId && this.getTable().isPrimary() && this.isIdColumn()) {
			// if the primary key is auto-generated do not insert it
			return false;
		}
		return true;
	}

	/**
	 * Utility method used for determining whether to include this column
	 * in update SQL statements.
	 */
	protected boolean isUpdatable() {
		return (!this.isIdColumn() && !this.isReadOnly());
	}

	@Override
	public void validateConfig() throws DataStoreException {
		if (StringUtils.isBlank(getListItemType()) && StringUtils.isBlank(getItemType())) {
			if (this.getTable().isReference()) {
				throw new DataStoreException("Invalid config for " + this.getColumnName() + ": reference columns must specify either a list-item-type or an item-type property");
			}
			if (this.isCascadeDelete()) {
				throw new DataStoreException("Invalid config for " + this.getColumnName() + ": cascade deletion is only valid for config for a list-item-type or item-type property");
			}
		}
		if (!StringUtils.isBlank(getListItemType()) && !StringUtils.isBlank(getItemType())) {
			throw new DataStoreException("Invalid config for " + this.getColumnName() + ": a property cannot specify both a list-item-type and an item-type value");
		}
	}
}
