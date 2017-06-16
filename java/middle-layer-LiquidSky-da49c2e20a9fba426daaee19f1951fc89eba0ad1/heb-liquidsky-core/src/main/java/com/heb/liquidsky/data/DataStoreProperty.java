package com.heb.liquidsky.data;

import java.util.Map;

/**
 * Generic representation of a property, which may come from the database or
 * from some other source.
 */
public interface DataStoreProperty {

	/**
	 * Flag indicating whether or not this property is a reference to another
	 * data item, or if it is simply a string, int, or other basic data type.
	 */
	public boolean isDataItem();

	/**
	 * For items that reference other data store items, return the data
	 * type of the referenced item.
	 */
	public String getItemType();

	/**
	 * For lists of items, return the data type of the child items.
	 */
	public String getListItemType();

	/**
	 * Return the name by which the property is referenced.
	 */
	public String getProperty();

	/**
	 * Flag indicating whether this property can be updated or if it is read-only.
	 */
	public boolean isReadOnly();
	
	/**
	 * Return a map of attributes
	 */
	public Map<String, String> getAttributes();
	
	/**
	 * Return the value of the specified attribute. 
	 * Return null if the attribute does not exist.
	 */
	public String getAttributeByName(String name);
}
