package com.heb.liquidsky.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.common.ConfigurationConstants;
import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.data.db.DatabaseDataType;
import com.heb.liquidsky.data.xml.DataStoreElement;
import com.heb.liquidsky.data.xml.DataTypeElement;
import com.heb.liquidsky.messaging.CloudMessaging;
import com.heb.liquidsky.pubsub.HEBPubSub;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;
import com.heb.liquidsky.trace.Label;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class is the primary entry point for interacting with data
 * stored in persistent storage.  It is configured via the
 * data-store.xml file, which defines the types and locations of
 * stored data.
 */
public final class DataStore {

	private static final Logger logger = Logger.getLogger(DataStore.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(DataStore.class);

	private static final String DEFAULT_DATA_STORE_XML = "/com/heb/liquidsky/data/data-store.xml";
	private static final String PROP_AUXILIARY_DATA_STORE_CONFIGS = "DATASTORE_AUXILLIARY_CONFIGS";
	private static final DataStore INSTANCE = new DataStore();

	private static Map<String, DataType> DATA_TYPES;

	private DataStore() {
		// only allow access to this class via the singleton instance
	}

	/**
	 * This method allows for an initialization listener or other warmup method to initialize the DataStore.
	 * 
	 * @throws DataStoreException
	 */
	public void warmup() throws IllegalStateException {
		try {
			this.initializeDataStoreFromXml(false);
		} catch (DataStoreException e) {
			throw new IllegalStateException("Unable to initialize data store XML", e);
		}
	}

	/**
	 * Create an empty item that is not (yet) persisted.  This method is
	 * useful for initializing a new item that will later be inserted
	 * permanently into the data store.
	 */
	public MutableDataItem createItem(String dataTypeName) throws DataStoreException {
		return this.createItem(null, dataTypeName);
	}

	/**
	 * Create an empty item that is not (yet) persisted.  This method is
	 * useful for initializing a new item that will later be inserted
	 * permanently into the data store.
	 */
	public MutableDataItem createItem(String id, String dataTypeName) throws DataStoreException {
		DataType dataType = this.getDataType(dataTypeName);
		return dataType.createItem(id);
	}

	/**
	 * Permanently delete the current data item from persistent storage.
	 */
	public void deleteItem(MutableDataItem dataItem) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("deleteItem");
		try {
			DataItem unmodifiedItem = this.readItemForUpdate(dataItem.getId(), dataItem.getDataType().getName());
			List<String> cacheKeys = DataStoreCache.getInstance().calculateDependencies(unmodifiedItem);
			// delete the item from the datastore
			((AbstractDataItem) dataItem).delete();
			// update the cache
			DataStoreCache.getInstance().deleteAll(cacheKeys);
			DataStoreCache.getInstance().update(dataItem.getId(), dataItem.getDataType().getName(), null);
			// notify listeners
			this.publish(dataItem, PUBSUB_ACTION.DELETE);
		} finally {
			TRACER.annotateSpan(context, "item", dataItem.toString());
			TRACER.endSpan(context);
		}
	}

	/**
	 * Executes a named query that is defined in the data-store.xml
	 * 
	 * @param dataTypeName
	 * @param queryName
	 * @param args
	 * 
	 * @return List of DataItem objects
	 * @throws DataStoreException
	 */
	public List<DataItem> executeNamedQuery(String dataTypeName, String queryName, Object... args) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("executeNamedQuery");
		try {
			DataType dataType = this.getDataType(dataTypeName);
			return dataType.executeNamedQuery(queryName, args);
		} finally {
			TRACER.annotateSpan(context, "query", queryName);
			TRACER.endSpan(context);
		}
	}

	/**
	 * Gets a DataType object for the given data type name
	 * 
	 * @param dataTypeName
	 * 
	 * @return DataType representing the type passed in the parameter
	 * @throws DataStoreException
	 */
	public DataType getDataType(String dataTypeName) throws DataStoreException {
		this.initializeDataStoreFromXml(false);
		DataType dataType = DATA_TYPES.get(dataTypeName);
		if (dataType == null) {
			throw new DataStoreException("No configuration exists for data type " + dataTypeName);
		}
		return dataType;
	}

	/**
	 * Gets all data types that the DataStore knows about
	 * 
	 * @return Collection of DataType
	 * @throws DataStoreException
	 */
	public Collection<DataType> getDataTypes() throws DataStoreException {
		this.initializeDataStoreFromXml(false);
		return DATA_TYPES.values();
	}

	/**
	 * Gets the singleton instance of the DataStore
	 * 
	 * @return
	 */
	public static DataStore getInstance() {
		return INSTANCE;
	}

	/**
	 * Internal method that gets the all configuration file names for the datastore.  
	 * Files can be configured in the default location defined with the DEFAULT_DATA_STORE_XML constant
	 * or may be included in a comma separated list of file names in a system property defined by the name in PROP_AUXILIARY_DATA_STORE_CONFIGS
	 * 
	 * DataStore configuration is loaded in order and subsequent files may override settings in previous files.
	 * 
	 * @return List of file names containing DataStore configuration
	 */
	private List<String> getDataStoreConfigFilenames() {
		List<String> configFileNames = new ArrayList<>();
		configFileNames.add(DEFAULT_DATA_STORE_XML);
		String auxiliaryConfigFileNames = CloudUtil.getProperty(PROP_AUXILIARY_DATA_STORE_CONFIGS);
		if (!StringUtils.isBlank(auxiliaryConfigFileNames)) {
			for (String configFileName : auxiliaryConfigFileNames.split(",")) {
				configFileNames.add(configFileName);
			}
		}
		return configFileNames;
	}

	/**
	 * This internal method allows the map update to be synchronized
	 * to ensure that there is no way that one thread could reset the map
	 * values while another was updating them.
	 */
	private synchronized void initializeDataStoreFromXml(boolean reinitialize) throws DataStoreException {
		if (DATA_TYPES != null && !reinitialize) {
			return;
		}
		HebTraceContext context = TRACER.startSpan("initializeDataStoreFromXml");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder builder = null;
			try {
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new DataStoreException("Failure initializing XML parser", e);
			}
			DATA_TYPES = new ConcurrentHashMap<>();
			for (String configFileName : this.getDataStoreConfigFilenames()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Loading data store configuration from file " + configFileName);
				}
				try (InputStream stream = CloudUtil.loadFileFromClasspath(configFileName)) {
					if (stream == null) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Configuration file " + configFileName + " does not exist");
						}
						continue;
					}
					Document doc = builder.parse(stream);
					doc.getDocumentElement().normalize();
					DataStoreElement dataStoreMarshaller = new DataStoreElement(doc.getDocumentElement());
					if (dataStoreMarshaller.getDataTypes() != null) {
						for (DataTypeElement dataTypeMarshaller : dataStoreMarshaller.getDataTypes()) {
							String name = dataTypeMarshaller.getName();
							if (DATA_TYPES.containsKey(name)) {
								throw new DataStoreException("Data type " + name + " is defined twice in the config files");
							}
							DATA_TYPES.put(name, this.initializeDataType(dataTypeMarshaller));
						}
					}
				} catch (SAXException | IOException e) {
					DATA_TYPES = null;
					throw new DataStoreException("Failure processing data store file " + configFileName, e);
				}
			}
		} finally {
			TRACER.endSpan(context);
		}
	}

	private DataType initializeDataType(DataTypeElement dataTypeMarshaller) throws DataStoreException {
		// TODO - support multiple types
		return new DatabaseDataType(dataTypeMarshaller);
	}

	/**
	 * Save the current data item to persistent storage.
	 */
	public void insertItem(MutableDataItem dataItem) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("insertItem");
		try {
			// insert the item into the datastore
			((AbstractDataItem) dataItem).insert();
			// update the cache
			List<String> cacheKeys = DataStoreCache.getInstance().calculateDependencies(dataItem);
			DataStoreCache.getInstance().deleteAll(cacheKeys);
			DataStoreCache.getInstance().update(dataItem.getId(), dataItem.getDataType().getName(), dataItem.copyItem());
			// notify listeners
			this.publish(dataItem, PUBSUB_ACTION.UPDATE);
		} finally {
			TRACER.annotateSpan(context, "item", dataItem.toString());
			TRACER.endSpan(context);
		}
	}

	private void publish(DataItem dataItem, PUBSUB_ACTION action) {
		if (ConfigurationConstants.ENABLE_DATASTORE_MESSAGE_PUBLISHING) {
			HEBPubSub.getInstance().publishDeferred(dataItem, action);
			CloudMessaging.getInstance().publishDeferred(dataItem, action);
		}
	}

	/**
	 * Read an existing item from the data store.  This method should be
	 * used for read-only operations to take advantage of caching and other
	 * optimizations.
	 * 
	 * @param id The ID of the item being read.
	 * @param dataTypeName The type of data being read, matching the
	 *  &lt;data-type> "name" attribute in the data-type.xml file.
	 */
	public DataItem readItemImmutable(String id, String dataTypeName) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("readItemImmutable");
		try {
			// try to retrieve the item from the cache
			ValueContainer<Serializable> dataItemContainer = DataStoreCache.getInstance().read(id, dataTypeName);
			if (dataItemContainer != null) {
				return (DataItem) dataItemContainer.getValue();
			}
			// if the item isn't cached then retrieve it from the data store
			DataType dataType = this.getDataType(dataTypeName);
			DataItem dataItem = dataType.readItemImmutable(id);
			if (dataItem == null) {
				// if there is no matching item then cache the null value
				// to speedup future lookups. if the value isn't null then
				// it will have been cached by the lookup.
				DataStoreCache.getInstance().update(id, dataTypeName, dataItem);
			}
			return dataItem;
		} finally {
			TRACER.annotateSpan(context, new Label("id", id), new Label("data-type", dataTypeName));
			TRACER.endSpan(context);
		}
	}

	/**
	 * Read an existing item from the data store.  The item that is
	 * returned will be mutable and may be used for updates.
	 * 
	 * @param id The ID of the item being read.
	 * @param dataTypeName The type of data being read, matching the
	 *  &lt;data-type> "name" attribute in the data-type.xml file.
	 */
	public MutableDataItem readItemForUpdate(String id, String dataTypeName) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("readItemImmutable");
		try {
			DataType dataType = this.getDataType(dataTypeName);
			return dataType.readItemForUpdate(id);
		} finally {
			TRACER.annotateSpan(context, new Label("id", id), new Label("data-type", dataTypeName));
			TRACER.endSpan(context);
		}
	}

	/**
	 * Commit any changes made to the item via the setProperty() method
	 * to persistent storage.
	 */
	public void updateItem(MutableDataItem dataItem) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("updateItem");
		try {
			DataItem unmodifiedItem = this.readItemForUpdate(dataItem.getId(), dataItem.getDataType().getName());
			// get the item's current dependencies
			List<String> cacheKeys = DataStoreCache.getInstance().calculateDependencies(unmodifiedItem);
			// update the item in the datastore
			((AbstractDataItem) dataItem).update();
			// get the item's new dependencies and then flush the cache
			// for all old & new dependencies
			cacheKeys.addAll(DataStoreCache.getInstance().calculateDependencies(dataItem));
			DataStoreCache.getInstance().deleteAll(cacheKeys);
			DataStoreCache.getInstance().update(dataItem.getId(), dataItem.getDataType().getName(), dataItem.copyItem());
			// notify listeners
			this.publish(dataItem, PUBSUB_ACTION.UPDATE);
		} finally {
			TRACER.annotateSpan(context, "item", dataItem.toString());
			TRACER.endSpan(context);
		}
	}
}
