package com.heb.liquidsky.common;

import org.apache.commons.lang3.BooleanUtils;

/**
 *  Configuration Constants for various components
 */
public class ConfigurationConstants {
	public static final boolean ENABLE_CACHE = BooleanUtils.toBoolean(CloudUtil.getProperty("MEMCACHE_ENABLED"));
	public static final boolean ENABLE_TRACING = BooleanUtils.toBoolean(CloudUtil.getProperty("TRACING_ENABLED"));
	public static final boolean ALLOW_HEADER_OWNER_ID = true;
	public static final String CORS_ALLOWED_ORIGINS = CloudUtil.getProperty("CORS_ALLOWED_ORIGINS");
	public static final String CORS_ALLOWED_HEADERS = CloudUtil.getProperty("CORS_ALLOWED_HEADERS");
	public static final String CORS_ALLOWED_METHODS = CloudUtil.getProperty("CORS_ALLOWED_METHODS");
	public static final boolean ENABLE_DATASTORE_MESSAGE_PUBLISHING = BooleanUtils.toBoolean(CloudUtil.getProperty("DATASTORE_ENABLE_MESSAGING"));
}
