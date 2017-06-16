package com.heb.liquidsky.data;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract implementation of the DataItem interface, used primarily to force
 * delete, insert and update operations to go through the DataStore (where
 * caching and other functionality can be layered on).
 */
public abstract class AbstractDataItem implements DataItem {

	/**
	 * Permanently delete the current data item from persistent storage.
	 * 
	 * @throws UnsupportedOperationException Thrown if this data item
	 *  type does not allow inserts.
	 */
	protected abstract void delete() throws DataStoreException;

	/**
	 * Save the current data item to persistent storage.
	 * 
	 * @throws UnsupportedOperationException Thrown if this data item
	 *  type does not allow inserts.
	 */
	protected abstract void insert() throws DataStoreException;

	/**
	 * Commit any changes made to the item via the setProperty() method to
	 * persistent storage.
	 * 
	 * @throws UnsupportedOperationException Thrown if this data item
	 *  type does not allow inserts.
	 */
	protected abstract void update() throws DataStoreException;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataItem)) {
			return false;
		}
		DataItem dataItem = (DataItem) obj;
		if (this.getDataType() == null && dataItem.getDataType() != null) {
			return false;
		}
		return (StringUtils.equals(this.getId(), dataItem.getId()) && this.getDataType().equals(dataItem.getDataType()));
	}

	@Override
	public String toString() {
		return this.getDataType().getName() + "__" + this.getId();
	}
}