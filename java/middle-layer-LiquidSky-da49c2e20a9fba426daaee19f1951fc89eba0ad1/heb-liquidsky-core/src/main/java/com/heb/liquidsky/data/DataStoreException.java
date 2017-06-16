package com.heb.liquidsky.data;

/**
 * Standard exception thrown during data store operations when there
 * is an error.
 */
public class DataStoreException extends Exception {

	public static final long serialVersionUID = 1l;

	public DataStoreException(String msg) {
		super(msg);
	}

	public DataStoreException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DataStoreException(Throwable cause) {
		super(cause);
	}
}
