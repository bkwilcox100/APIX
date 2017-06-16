package com.heb.liquidsky.cache;

/**
 * Internal-use exception used to handle memcache failures.
 * 
 * @author ryan
 */
@SuppressWarnings("serial")
public class HebMemcacheException extends Exception {

	public HebMemcacheException(Throwable t) {
		super(t);
	}
}
