package com.heb.liquidsky.data.db;

import com.heb.liquidsky.data.DataItemPropertyDescriptor;
import com.heb.liquidsky.data.DataStoreProperty;

public class DatabaseDataItemPropertyDescriptor implements DataItemPropertyDescriptor {

	private final DataStoreProperty property;

	protected DatabaseDataItemPropertyDescriptor(DataStoreProperty property) {
		this.property = property;
	}

	@Override
	public boolean isDataItem() {
		return this.getProperty().isDataItem();
	}

	private DataStoreProperty getProperty() {
		return this.property;
	}

	@Override
	public String getItemType() {
		return this.getProperty().getItemType();
	}

	@Override
	public String getListItemType() {
		return this.getProperty().getListItemType();
	}

	@Override
	public String getPropertyName() {
		return this.getProperty().getProperty();
	}
}
