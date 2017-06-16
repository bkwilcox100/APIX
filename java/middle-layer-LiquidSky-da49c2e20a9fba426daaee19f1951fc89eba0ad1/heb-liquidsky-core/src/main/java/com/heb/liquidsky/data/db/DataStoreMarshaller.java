package com.heb.liquidsky.data.db;

import com.heb.liquidsky.data.DataStoreException;

/**
 * This interface is used by all objects that are initialized from the
 * data-store.xml file.
 */
public interface DataStoreMarshaller {

	/**
	 * Once the item is initialized, valid that the configuration does
	 * not have any errors.
	 */
	public void validateConfig() throws DataStoreException;
}
