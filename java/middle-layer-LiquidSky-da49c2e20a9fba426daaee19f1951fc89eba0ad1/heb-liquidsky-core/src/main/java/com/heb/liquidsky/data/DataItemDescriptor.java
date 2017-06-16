package com.heb.liquidsky.data;

import java.util.List;

/**
 * Return meta data about a particular data item.
 */
public interface DataItemDescriptor {

	public DataItemPropertyDescriptor getIdPropertyDescriptor();

	/**
	 * Return a list of all property names for the data item.
	 */
	public List<DataItemPropertyDescriptor> getPropertyDescriptors();
}