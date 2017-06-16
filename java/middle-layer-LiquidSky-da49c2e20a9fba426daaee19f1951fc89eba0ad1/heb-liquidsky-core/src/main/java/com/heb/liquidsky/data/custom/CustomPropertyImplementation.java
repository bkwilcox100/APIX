package com.heb.liquidsky.data.custom;

import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;

public interface CustomPropertyImplementation {

	/**
	 * Return the value of the custom property.
	 */
	public Object getPropertyValue(DataItem dataItem) throws DataStoreException;

	/**
	 * Return the name of an attribute configured for the custom property.
	 */
	public String getAttributeValue(String attributeName);
}
