package com.heb.liquidsky.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a single item of a specific type in the data store.
 */
public interface DataItem {

	/**
	 * Copy a DataItem, used mainly for caching purposes so that a
	 * read-only item can be created from a read-write item.
	 */
	public DataItem copyItem();

	/**
	 * Return a timestamp indicating when this data item was first
	 * created in the data store.
	 */
	public Timestamp getCreationDate() throws DataStoreException;

	/**
	 * Return the item data type.
	 */
	public DataType getDataType();

	/**
	 * Return a descriptor that provides metadata information about
	 * the item including property names & types.
	 */
	public DataItemDescriptor dataItemDescriptor();

	/**
	 * Return a unique identifier for this data item.
	 */
	public String getId();

	/**
	 * Return the specified property as an Integer (may return <code>null</code>).
	 * If the property does not represent an Integer then a ClassCastException
	 * may be thrown.
	 */
	public Integer getInt(String property) throws DataStoreException;

	/**
	 * Return the specified property as a DataItem (may return <code>null</code>).
	 * If the property does not represent an DataItem then a ClassCastException
	 * may be thrown.
	 */
	public DataItem getItem(String property) throws DataStoreException;

	/**
	 * Return a timestamp indicating the last time that this data item was
	 * modified in the data store.
	 */
	public Timestamp getLastModifiedDate() throws DataStoreException;

	/**
	 * Return the specified property as a List (may return <code>null</code>).
	 * If the property does not represent a List then a ClassCastException
	 * may be thrown.
	 */
	public List<DataItem> getList(String property) throws DataStoreException;

	/**
	 * Return the specified property as a Long (may return <code>null</code>).
	 * If the property does not represent a Long then a ClassCastException
	 * may be thrown.
	 */
	public Long getLong(String property) throws DataStoreException;

	/**
	 * Return the specified property as a generic Object
	 * (may return <code>null</code>).
	 */
	public Object getObject(String property) throws DataStoreException;

	/**
	 * Return the specified property as a String (may return <code>null</code>).
	 * If the property does not represent a String then a ClassCastException
	 * may be thrown.
	 */
	public String getString(String property) throws DataStoreException;

	/**
	 * Return the specified property as a Timestamp (may return <code>null</code>).
	 * If the property does not represent a Timestamp then a ClassCastException
	 * may be thrown.
	 */
	public Timestamp getTimestamp(String property) throws DataStoreException;

	/**
	 * Convert the data item to a map so that it can be easily accessed
	 * in formats like JSTL via "${item.property}".
	 */
	public Map<String, Object> toMap(int maxDepth) throws DataStoreException;
}
