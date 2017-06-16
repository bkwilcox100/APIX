package com.heb.liquidsky.common;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HebEnvironmentProperties {

	private static final String PROPERTY_FILE_FLEX = "project-flex.properties";
	private static final String PROPERTY_FILE_JUNIT = "junit.properties";
	private static final HebEnvironmentProperties INSTANCE = new HebEnvironmentProperties();

	private static final Logger logger = Logger.getLogger(HebEnvironmentProperties.class.getName());

	private HebEnvironmentProperties() {
		// private constructor to enforce singleton pattern
	}

	public static HebEnvironmentProperties getInstance() {
		return INSTANCE;
	}

	/**
	 * Get the project ID for the currently running project. 
	 */
	public String getAppEngineId() {
		String appEngineId = System.getenv("GCLOUD_PROJECT");
		if (appEngineId == null) {
			// if running locally get the project ID from local properties
			appEngineId = CloudUtil.getProperty("APP_ENGINE_APP_ID"); 
		}
		return appEngineId;
	}

	/**
	 * Gets the host name that the request is running on.
	 * @return String - Hostname
	 */
	public String getHostName() {
		// TODO - not supported in Flex ?
		return "";
	}

	/**
	 * Gets the Instance ID for the App Engine instance that this particular request is running on
	 * @return String - App Engine Instance ID
	 */
	public String getInstanceId() {
		return System.getenv("GAE_INSTANCE");
	}

	/**
	 * Determine if the current application is running on a production app
	 * engine instance or not.
	 */
	public boolean isLocalInstance() {
		return System.getenv("GCLOUD_PROJECT") == null;
	}

	/**
	 * Load all application property values and return a Properties object.
	 */
	public Properties loadProperties() {
		if (this.isLocalInstance()) {
			// if running unit tests, load the unit test properties
			Properties properties = CloudUtil.loadPropertiesFile(PROPERTY_FILE_JUNIT);
			if (!properties.isEmpty()) {
				return properties;
			}
			// if running locally, load the local properties
			return CloudUtil.loadPropertiesFile(PROPERTY_FILE_FLEX);
		}
		// otherwise load properties from cloud storage
		String bucketName = this.getAppEngineId();
		try {
			Properties cloudProperties = CloudUtil.loadPropertiesFromFolders(bucketName);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loaded " + cloudProperties.size() + " properties from Cloud Storage");
			}
			if (cloudProperties.isEmpty()) {
				throw new IllegalStateException("No property values found in Cloud Storage bucket " + bucketName + ".  Project requires property values to set in order to operate properly.");
			}
			return cloudProperties;
		} catch (IOException e) {
			throw new IllegalStateException("Failure while loading properties from Cloud Storage bucket: " + bucketName, e);
		}
	}
}
