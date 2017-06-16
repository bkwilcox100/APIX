package com.heb.liquidsky.data;

import java.util.List;

/**
 * Interface representing a &lt;data-type> element in the data-store.xml
 * file, corresponding to the collection of properties that represent an
 * item of a defined type.
 */
public interface DataType {

	public static final String PROP_CREATION_DATE = "creationDate";
	public static final String PROP_LAST_MODIFIED_DATE = "lastModifiedDate";

	/**
	 * Create an empty item that is not (yet) persisted.  This method is
	 * useful for initializing a new item that will later be inserted
	 * permanently into the data store.
	 * 
	 * @param id A unique ID for the empty item that is being created.
	 */
	public MutableDataItem createItem(String id);

	/**
	 * Flag indicating whether Firebase Cloud Messaging notifications
	 * should be enabled for this data type.
	 */
	public boolean isFcmEnabled();

	/**
	 * Execute a query that returns a list of DataItem objects, or an
	 * empty list if no matches are found.
	 * 
	 * @param queryName The name of the query to execute.
	 * @param args An array of arguments to pass to the query.
	 */
	public List<DataItem> executeNamedQuery(String queryName, Object... args) throws DataStoreException;

	/**
	 * Return the name of this data type as specified in the data store config.
	 */
	public String getName();

	/**
	 * Return the property for the given name, or <code>null</code> if there is no
	 * such property.
	 */
	public DataStoreProperty getPropertyByName(String propertyName);

	/**
	 * Flag indicating whether pub/sub notifications should be enabled for
	 * this data type.
	 */
	public boolean isPubSubEnabled();

	/**
	 * Read an existing item from the data store.  This method should be
	 * used for read-only operations to take advantage of caching and other
	 * optimizations.
	 * 
	 * @param id The ID of the item being read.
	 */
	public DataItem readItemImmutable(String id) throws DataStoreException;

	/**
	 * Read an existing item from the data store.  The item that is
	 * returned will be mutable and may be used for updates.
	 * 
	 * @param id The ID of the item being read.
	 */
	public MutableDataItem readItemForUpdate(String id) throws DataStoreException;
}
