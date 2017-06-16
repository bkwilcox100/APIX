package com.heb.liquidsky.data;

/**
 * Interface representing a data item that supports update operations.
 */
public interface MutableDataItem extends DataItem {

	/**
	 * Return a flag indicating whether or not to publish updates for this
	 * data item through pub/sub.
	 */
	public boolean isPubSubEnabled();

	/**
	 * Set a flag indicating whether or not to publish updates for this
	 * data item through pub/sub.
	 */
	public void setPubSubEnabled(boolean enabled);

	/**
	 * Update a property on the data item.  While this method will update
	 * the item in memory, it still must be committed to persistent storage.
	 */
	public void setProperty(String property, Object value);
}
