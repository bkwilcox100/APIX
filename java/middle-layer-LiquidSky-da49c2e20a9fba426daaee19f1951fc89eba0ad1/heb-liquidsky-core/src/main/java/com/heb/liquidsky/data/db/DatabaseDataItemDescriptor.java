package com.heb.liquidsky.data.db;

import java.util.List;

import com.heb.liquidsky.data.DataItemDescriptor;
import com.heb.liquidsky.data.DataItemPropertyDescriptor;

public class DatabaseDataItemDescriptor implements DataItemDescriptor {

	private final DatabaseDataType dataType;
	private DataItemPropertyDescriptor idPropertyDescriptor;

	protected DatabaseDataItemDescriptor(DatabaseDataType dataType) {
		this.dataType = dataType;
	}

	private DatabaseDataType getDataType() {
		return this.dataType;
	}

	@Override
	public DataItemPropertyDescriptor getIdPropertyDescriptor() {
		if (this.idPropertyDescriptor == null) {
			DataColumn idColumn = this.getDataType().getPrimaryTable().getColumnForProperty(this.getDataType().getIdProperty());
			this.idPropertyDescriptor = new DatabaseDataItemPropertyDescriptor(idColumn);
		}
		return this.idPropertyDescriptor;
	}

	@Override
	public List<DataItemPropertyDescriptor> getPropertyDescriptors() {
		return this.getDataType().getPropertyDescriptors();
	}
}
