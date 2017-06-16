package com.heb.liquidsky.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class contains generic utilities that are generically useful throughout
 * the Liquid Sky application.
 */
public final class CloudUtil {

	private static final Properties PROPERTIES = new Properties();
	private static final String KEYRING_ID = "application-shared";
	private static final String PROPERTY_FILE_SUFFIX = ".property";
	private static final String BASE_PATH = "properties/";
	public static final String CLOUD_STORAGE_DEFAULT_APP_PROPERTY_DIRECTORY = "app-settings";
	private static final List<String> CLOUD_STORAGE_PROPERTY_DIRECTORIES = Arrays.asList(CLOUD_STORAGE_DEFAULT_APP_PROPERTY_DIRECTORY, "gcp-creds");
	private static final Logger logger = Logger.getLogger(CloudUtil.class.getName());

	private CloudUtil() {
		// singleton class
	}

	/**
	 * Utility method to allow properties to be retrieved as either system
	 * properties or environment variables, since the standard environment
	 * supports only system properties while the flexible environment supports
	 * only environment variables.
	 */
	public static String getProperty(String key) {
		if (PROPERTIES.isEmpty()) {
			loadProperties();
		}
		if (!PROPERTIES.containsKey(key) && logger.isLoggable(Level.WARNING)) {
			String msg = "No property exists for key " + key + ".  Please verify that the property has been set in ";
			msg += (HebEnvironmentProperties.getInstance().isLocalInstance()) ? "your local properties." : "Cloud Storage for this project.";
			logger.warning(msg);
		}
		return PROPERTIES.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		String value = CloudUtil.getProperty(key);
		return (value != null) ? value : defaultValue;
	}

	private static void loadProperties() {
		if (PROPERTIES.isEmpty()) {
			PROPERTIES.putAll(HebEnvironmentProperties.getInstance().loadProperties());
		}
		if (PROPERTIES.isEmpty()) {
			throw new IllegalStateException("No properties file could be found in the classpath");
		}
	}

	public static Properties loadPropertiesFile(String propertyFile) {
		Properties properties = new Properties();
		try (InputStream input = CloudUtil.loadFileFromClasspath(propertyFile)) {
			if (input != null) {
				properties.load(input);
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Loaded properties from classpath: " + propertyFile);
				}
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while loading properties from classpath: " + propertyFile, e);
			}
		}
		return properties;
	}

	public static Properties loadPropertiesFromFolders(String bucketName) throws IOException {
		Properties properties = new Properties();
		for (String folder : CLOUD_STORAGE_PROPERTY_DIRECTORIES) {
			// get list of blobs in folder
			for (String blobPath : getPropertyBlobPathsInFolder(bucketName, folder)) {
				String propertyName = blobPathToPropertyName(blobPath);
				String propertyValue = CloudStorageUtil.readEncryptedFileFromStorage(bucketName, blobPath, KEYRING_ID, folder);
				if (propertyValue == null) {
					propertyValue = "";
				}
				properties.put(propertyName, propertyValue);
			}
		}
		
		return properties;
	}

	private static List<String> getPropertyBlobPathsInFolder(String bucketName, String folder) throws IOException {
		List<String> results = new ArrayList<>();
		// get list of blobs in folder
		String prefix = BASE_PATH + folder;
		for (String blobPath : CloudStorageUtil.getBlobPathsInFolder(bucketName, prefix)) {
			// check to see if blob is a property file
			if (!blobPath.endsWith(PROPERTY_FILE_SUFFIX)) {
				continue;
			}
			String propertyName = blobPathToPropertyName(blobPath);
			if (StringUtils.isBlank(propertyName)) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Non-property file found in Cloud Storage properties folder " + bucketName + ":" + blobPath);
				}
				continue;
			}
			results.add(blobPath);
		}
		return results;
	}

	public static List<String> getPropertyNamesInFolder(String folder) throws IOException {
		String bucketName = HebEnvironmentProperties.getInstance().getAppEngineId();
		List<String> results = new ArrayList<>();
		for (String blobPath : getPropertyBlobPathsInFolder(bucketName, folder)) {
			String propertyName = blobPathToPropertyName(blobPath);
			results.add(propertyName);
		}
		return results;
	}

	public static boolean propertyExistsInStorage(String folder, String propertyName) throws IOException {
		String bucketName = HebEnvironmentProperties.getInstance().getAppEngineId();
		String blobPath = propertyNameToBlobPath(folder, propertyName);
		return CloudStorageUtil.exists(bucketName, blobPath); 
	}

	/**
	 * The stripped-down name of the blob is the property name.
	 */
	private static String blobPathToPropertyName(String blobPath) {
		int start = blobPath.lastIndexOf(CloudStorageUtil.FILE_SEPARATOR) + 1;
		int end = blobPath.lastIndexOf(PROPERTY_FILE_SUFFIX);
		if (start == -1 || end == -1) {
			return null;
		}
		return blobPath.substring(start, end);
	}

	private static String propertyNameToBlobPath(String folder, String propertyName) {
		return BASE_PATH + folder + CloudStorageUtil.FILE_SEPARATOR + propertyName + PROPERTY_FILE_SUFFIX;
	}

	/**
	 * Method used to create properties in Cloud Storage.
	 */
	public static void createPropertyFileInStorage(String folder, String propertyName, String value) throws IOException {
		String bucketName = HebEnvironmentProperties.getInstance().getAppEngineId();
		String blobPath = propertyNameToBlobPath(folder, propertyName);
		CloudStorageUtil.createEncryptedFileInStorage(bucketName, blobPath, value.getBytes(), KEYRING_ID, folder);
	}

	/**
	 * Method used to delete properties in Cloud Storage.
	 */
	public static void deletePropertyFileFromStorage(String folder, String propertyName) throws IOException {
		String bucketName = HebEnvironmentProperties.getInstance().getAppEngineId();
		String blobPath = propertyNameToBlobPath(folder, propertyName);
		CloudStorageUtil.deleteFileFromStorage(bucketName, blobPath);
	}

	/**
	 * Method used to update properties in Cloud Storage.
	 */
	public static void updatePropertyFileInStorage(String folder, String propertyName, String value) throws IOException {
		String bucketName = HebEnvironmentProperties.getInstance().getAppEngineId();
		String blobPath = propertyNameToBlobPath(folder, propertyName);
		CloudStorageUtil.updateEncryptedFileInStorage(bucketName, blobPath, value.getBytes(), KEYRING_ID, folder);
	}

	/**
	 * Determine if the current app is running on Spring or not.
	 */
	public static boolean isSpringEnvironment() {
		return (isClassAvailable("org.springframework.context.ApplicationContext"));
	}

	/**
	 * Determine if a specific class is available in the classpath; useful for
	 * distinguishing between Standard & Flex environments, Spring & non-Spring,
	 * etc.
	 */
	private static boolean isClassAvailable(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * This returns the calling methods name, useful for logging.
	 */
	public static String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	/**
	 * Load a file from the classpath.
	 * 
	 * @param filename
	 *            Filename relative to the classpath, for example
	 *            "/com/heb/liquidsky/filename.properties"
	 */
	public static InputStream loadFileFromClasspath(String filename) {
		InputStream stream = CloudUtil.class.getResourceAsStream(filename);
		if (stream == null) {
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		}
		return stream;
	}

	public static String appendQueryParam(String url, String key, String value) {
		url += (url.indexOf('?') == -1) ? '?' : '&';
		try {
			url += URLEncoder.encode(key, "UTF-8");
			url += '=';
			url += URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// this exception literally never happens - Sun should have
			// made it a runtime exception
		}
		return url;
	}

	/**
	 * For debugging purposes it is sometimes useful to generate a stack trace
	 * to determine the execution path for a piece of code. This method returns
	 * that information as a string, limiting the stack trace to a specified max
	 * depth.
	 */
	public static String generateDebugStackTrace(int maxDepth) {
		String msg = "Deubgging stack trace:\n";
		try {
			throw new Exception("Debugging stack trace");
		} catch (Exception e) {
			msg = generateDebugStackTrace(e, maxDepth);
		}
		return msg;
	}

	public static String generateDebugStackTrace(Throwable e, int maxDepth) {
		String msg = "Deubgging stack trace:\n";
		int depth = 0;
		for (StackTraceElement element : e.getStackTrace()) {
			if (depth >= maxDepth) {
				break;
			}
			msg += "\t--> " + element.toString() + "\n";
			depth++;
		}
		return msg;
	}

	public static byte[] toByteArray(Object obj) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(obj);
			out.flush();
			return bos.toByteArray();
		}
	}

	public static Object toObject(byte[] bytes) throws ClassNotFoundException, IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		}
	}

	public static String secureLogMessage (String message) {
		// ensure no CRLF injection into logs for forging records
		String clean = message.replace('\n', '_').replace('\r', '_');
		clean = StringEscapeUtils.escapeHtml4(clean);
		if (!message.equals(clean)) {
			clean += " (Encoded)";
		}
		return clean;
	}
}
