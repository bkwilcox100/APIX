package com.heb.liquidsky.cache;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.common.ConfigurationConstants;
import com.heb.liquidsky.common.HebEnvironmentProperties;
import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

import net.spy.memcached.MemcachedClient;

public final class HebMemoryStoreCache {

	private static final String PROPERTY_MEMORY_STORE_HOST = "MEMORY_STORE_HOST";
	private static final String PROPERTY_MEMORY_STORE_PORT = "MEMORY_STORE_PORT";
	private static final int DEFAULT_EXPIRATION_SECONDS = 60 * 60 * 24;
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 500;
	private static final int DEFAULT_WRITE_TIMEOUT_MILLISECONDS = 2000;

	private static final Logger logger = Logger.getLogger(HebMemoryStoreCache.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(HebMemoryStoreCache.class);
	private static final HebMemoryStoreCache INSTANCE = new HebMemoryStoreCache();

	private MemcachedClient client;
	private boolean healthy = false;
	private long lastHealthCheck = 0l;
	private long healthCheckInterval = 1000l;

	private HebMemoryStoreCache() {
		// private constructor to enforce singleton pattern
		if (logger.isLoggable(Level.WARNING)) {
			if (!ConfigurationConstants.ENABLE_CACHE) {
				logger.warning("Application is running with cache disabled - the MEMCACHE_ENABLED property is false !!!!");
			} else if (HebEnvironmentProperties.getInstance().isLocalInstance()) {
				logger.warning("Application is running with cache disabled - the application is not currently running in a Google Cloud project !!!!");
			} else if (!this.isCacheConfigured()) {
				logger.warning("Application is running with cache disabled - the PROPERTY_MEMORY_STORE_HOST and/or PROPERTY_MEMORY_STORE_PORT properties are not specified !!!!");
			}
		}
	}

	public static HebMemoryStoreCache getInstance() {
		return INSTANCE;
	}

	/**
	 * Delete an item from the cache.
	 * 
	 * @param key A unique key identifying the entry in the cache.
	 */
	public void delete(String key) {
		if (!this.isInitialized()) {
			return;
		}
		Future<Boolean> f = client.delete(key);
		Boolean result = null;
		try {
			result = this.processFuture(f, DEFAULT_WRITE_TIMEOUT_MILLISECONDS);
			if (result != null && logger.isLoggable(Level.FINE)) {
				if (result.booleanValue()) {
					logger.fine("Successfully deleted entry " + key + " from the cache");
				} else {
					logger.fine("Failed to delete entry " + key + " from the cache (item may not have previously been cached)");
				}
			}
		} catch (HebMemcacheException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while deleting entry " + key + " from the cache", e);
			}
		}
	}

	/**
	 * Delete multiple items from the cache.
	 * 
	 * @param keys A list of unique keys identifying entries in the cache.
	 */
	public void deleteAll(List<String> keys) {
		if (!this.isInitialized()) {
			return;
		}
		if (keys == null || keys.isEmpty()) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Attempt to invoke CloudCache.deleteAll with an empty list of keys to delete");
			}
			return;
		}
		Map<String, Future<Boolean>> futureMap = new HashMap<>();
		for (String key : keys) {
			if (!futureMap.containsKey(key)) {
				futureMap.put(key, client.delete(key));
			}
		}
		try {
			for (Map.Entry<String, Future<Boolean>> entry : futureMap.entrySet()) {
				Boolean result = this.processFuture(entry.getValue(), DEFAULT_WRITE_TIMEOUT_MILLISECONDS);
				if (result != null && logger.isLoggable(Level.FINE)) {
					if (result.booleanValue()) {
						logger.fine("Successfully deleted entry " + entry.getKey() + " from the cache");
					} else {
						logger.warning("Failed to delete entry " + entry.getKey() + " from the cache (item may not have previously been cached)");
					}
				}
			}
		} catch (HebMemcacheException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while deleting entries from the cache", e);
			}
		}
	}

	/**
	 * Retrieve a value from the cache.
	 * 
	 * @param key A unique key identifying the entry in the cache.
	 * @return The entry from the cache, or <code>null</code> if there is no
	 *  entry for the given key.
	 */
	public ValueContainer<Serializable> get(String key) {
		if (!this.isInitialized() || !this.isHealthy()) {
			return null;
		}
		try {
			return this.getWithoutHealthCheck(key);
		} catch (HebMemcacheException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while retrieving " + key + " from the cache", e);
			}
			return null;
		}
	}

	private ValueContainer<Serializable> getWithoutHealthCheck(String key) throws HebMemcacheException {
		Future<Object> f = client.asyncGet(key);
		Object cacheObj = this.processFuture(f, DEFAULT_READ_TIMEOUT_MILLISECONDS);
		if (logger.isLoggable(Level.FINE)) {
			if (cacheObj != null) {
				logger.fine("Successfully retrieved entry " + key + " from the cache");
			} else {
				logger.fine("Entry " + key + " is not in the cache");
			}
		}
		@SuppressWarnings("unchecked")
		ValueContainer<Serializable> result = (ValueContainer<Serializable>) cacheObj;
		return result;
	}

	/**
	 * Retrieve multiple values from the cache.
	 * 
	 * @param keys A list of unique keys identifying the entries in the cache.
	 * @return A map of key-value pairs for all entries found in the cache.  If
	 *  no entry is found then the map will not contain a matching key.
	 */
	public Map<String, Object> getAll(List<String> keys) {
		if (!this.isInitialized() || !this.isHealthy()) {
			return null;
		}
		Future<Map<String, Object>> f = client.asyncGetBulk(keys);
		Map<String, Object> result = null;
		try {
			result = this.processFuture(f, DEFAULT_READ_TIMEOUT_MILLISECONDS);
			if (logger.isLoggable(Level.FINE)) {
				if (result != null) {
					logger.fine("Successfully retrieved " + result.size() + " entries from the cache");
				} else {
					logger.fine("No matching entries found in the cache");
				}
			}
		} catch (HebMemcacheException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while retrieving restults from the cache", e);
			}
		}
		return result;
	}

	/**
	 * "Healthy" indicates that the cache is responding within a
	 * reasonable amount of time.
	 */
	private boolean isHealthy() {
		if (!this.healthy) {
			this.performHealthCheck();
		}
		return this.healthy;
	}

	private boolean setHealthy(boolean healthy) {
		return this.healthy = healthy;
	}

	private boolean isInitialized() {
		if (!ConfigurationConstants.ENABLE_CACHE || !this.isCacheConfigured() || HebEnvironmentProperties.getInstance().isLocalInstance()) {
			return false;
		}
		if (this.client == null) {
			this.initialize();
		}
		return true;
	}

	private synchronized void initialize() {
		if (!ConfigurationConstants.ENABLE_CACHE || !this.isCacheConfigured() || HebEnvironmentProperties.getInstance().isLocalInstance()) {
			return;
		}
		HebTraceContext context = TRACER.startSpan("initialize");
		String host = CloudUtil.getProperty(PROPERTY_MEMORY_STORE_HOST);
		String port = CloudUtil.getProperty(PROPERTY_MEMORY_STORE_PORT);
		try {
			this.client = new MemcachedClient(new InetSocketAddress(host, Integer.parseInt(port)));
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Successfully initialized memcache for host " + host + ":" + port);
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure initializing memcache for host " + host + ":" + port, e);
			}
		} finally {
			TRACER.endSpan(context);
		}
	}

	private boolean isCacheConfigured() {
		String host = CloudUtil.getProperty(PROPERTY_MEMORY_STORE_HOST);
		String port = CloudUtil.getProperty(PROPERTY_MEMORY_STORE_PORT);
		return (!StringUtils.isBlank(host) && !StringUtils.isBlank(port));
	}

	private synchronized void performHealthCheck() {
		if ((System.currentTimeMillis() - this.lastHealthCheck) < this.healthCheckInterval) {
			return;
		}
		HebTraceContext context = TRACER.startSpan("performHealthCheck");
		try {
			this.lastHealthCheck = System.currentTimeMillis();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Performing memcache health check");
			}
			this.getWithoutHealthCheck("initialization_test");
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Memcache health check PASSED");
			}
			this.setHealthy(true);
			this.healthCheckInterval = 1000l;
		} catch (Exception e) {
			// set the interval for the next health check, using a doubling algorithm
			// so that the code backs off after repeated failures
			if (this.healthCheckInterval < (60 * 1000)) {
				this.healthCheckInterval *= 2;
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Memcache health check FAILED, will try again after " + (this.healthCheckInterval / 1000) + " seconds", e);
			}
		} finally {
			TRACER.endSpan(context);
		}
	}

	/**
	 * Internal utility method for handling a Future response from a
	 * cache operation consistently.
	 */
	private <T> T processFuture(Future<T> f, int timeoutInMilliseconds) throws HebMemcacheException {
		try {
			return f.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			// timeout
			this.setHealthy(false);
			f.cancel(true);
			throw new HebMemcacheException(e);
		}
	}

	/**
	 * Add an item to the cache, replacing an existing item if one is
	 * already present.
	 * 
	 * @param key A unique key identifying the entry in the cache.
	 * @param newValue A ValueContainer for the item being added.  This value
	 *  may not be <code>null</code>.
	 */
	public void put(String key, ValueContainer<Serializable> newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Cannot add null values to the cache");
		}
		if (!this.isInitialized()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Ignored adding entry " + key + " to the cache because ConfigurationConstants.ENABLE_CACHE is false");
			}
			return;
		}
		Future<Boolean> f = client.set(key, DEFAULT_EXPIRATION_SECONDS, newValue);
		Boolean result = null;
		try {
			result = this.processFuture(f, DEFAULT_WRITE_TIMEOUT_MILLISECONDS);
			if (result != null) {
				if (logger.isLoggable(Level.FINE) && result.booleanValue()) {
					logger.fine("Successfully added entry " + key + " to the cache");
				} else if (logger.isLoggable(Level.WARNING) && !result.booleanValue()) {
					logger.warning("Failed to add entry " + key + " to the cache");
				}
			}
		} catch (HebMemcacheException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while adding entry with key " + key + " to the cache", e);
			}
		}
	}

	public void warmup() throws IllegalStateException {
		this.initialize();
	}
}
