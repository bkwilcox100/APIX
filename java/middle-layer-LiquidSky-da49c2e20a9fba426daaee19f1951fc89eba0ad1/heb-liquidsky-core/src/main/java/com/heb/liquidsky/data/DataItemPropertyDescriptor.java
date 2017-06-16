package com.heb.liquidsky.data;

/**
 * Return meta data about a particular property of a data item.
 */
public interface DataItemPropertyDescriptor {

	public boolean isDataItem();

	public String getItemType();

	public String getListItemType();

	public String getPropertyName();
}
