package com.heb.liquidsky.data.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataItemPropertyDescriptor;
import com.heb.liquidsky.data.DataStoreCache;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.DataStoreProperty;
import com.heb.liquidsky.data.DataType;
import com.heb.liquidsky.data.MutableDataItem;
import com.heb.liquidsky.data.custom.CustomProperty;
import com.heb.liquidsky.data.xml.CustomPropertyElement;
import com.heb.liquidsky.data.xml.DataTypeElement;
import com.heb.liquidsky.data.xml.NamedQueryElement;
import com.heb.liquidsky.data.xml.TableElement;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class is an object representation of a &lt;data-type>
 * configuration in the data-store.xml file.
 */
public class DatabaseDataType implements DataType, DataStoreMarshaller {

	private List<DataTable> allTables;
	private final List<DataTable> auxiliaryTables;
	private final List<CustomProperty> customProperties;
	private final boolean fcmEnabled;
	private final String idPrefix;
	private final String idProperty;
	private final String name;
	private final List<NamedQuery> namedQueries;
	private final DataTable primaryTable;
	private List<DataItemPropertyDescriptor> propertyDescriptors;
	private final boolean pubSubEnabled;
	private final List<DataTable> referenceTables;
	private final boolean useIdGenerator;

	public DatabaseDataType(DataTypeElement marshaller) throws DataStoreException {
		this.idPrefix = marshaller.getIdPrefix();
		this.idProperty = marshaller.getIdProperty();
		this.name = marshaller.getName();
		this.fcmEnabled = marshaller.isFcmEnabled();
		this.pubSubEnabled = marshaller.isPubSubEnabled();
		this.useIdGenerator = marshaller.isUseIdGenerator();
		this.auxiliaryTables = new ArrayList<>();
		this.referenceTables = new ArrayList<>();
		List<DataTable> primaryTables = new ArrayList<>();
		if (marshaller.getTables() != null) {
			for (TableElement tableMarshaller : marshaller.getTables()) {
				DataTable dataTable = new DataTable(this, tableMarshaller);
				if (dataTable.isAuxiliary()) {
					this.auxiliaryTables.add(dataTable);
				} else if (dataTable.isReference()) {
					this.referenceTables.add(dataTable);
				} else {
					primaryTables.add(dataTable);
				}
			}
		}
		if (primaryTables.isEmpty()) {
			throw new DataStoreException("Invalid config for " + this.getName() + ": no primary tables configured");
		} else if (primaryTables.size() > 1) {
			throw new DataStoreException("Invalid config for " + this.getName() + ": only one primary table is allowed per data type, but multiple tables are configured as primary");
		}
		this.primaryTable = primaryTables.get(0);
		this.namedQueries = new ArrayList<>();
		if (marshaller.getNamedQueries() != null) {
			for (NamedQueryElement namedQueryMarshaller : marshaller.getNamedQueries()) {
				this.namedQueries.add(new NamedQuery(namedQueryMarshaller));
			}
		}
		this.customProperties = new ArrayList<>();
		if (marshaller.getCustomProperties() != null) {
			for (CustomPropertyElement customPropertyMarshaller : marshaller.getCustomProperties()) {
				this.customProperties.add(new CustomProperty(customPropertyMarshaller));
			}
		}
		this.validateConfig();
	}

	@Override
	public MutableDataItem createItem(String id) {
		return MutableDatabaseDataItem.createItem(id, this);
	}

	/**
	 * This method exists for performance reasons and is used to retrieve
	 * an item that is known to exist.  If the item is cached then it is
	 * retrieved from the cache, otherwise a new, empty item is returned.
	 */
	protected DatabaseDataItem createItemFromCache(String id) {
		// try to retrieve the item from the cache
		ValueContainer<Serializable> dataItemContainer = DataStoreCache.getInstance().read(id, this.getName());
		if (dataItemContainer != null) {
			return (DatabaseDataItem) dataItemContainer.getValue();
		}
		// if there's no item in the cache then return an empty item
		return new DatabaseDataItem(id, this);
	}

	/**
	 * This method exists for performance reasons and is used to retrieve
	 * a a list of items that are known to exist.  If any of the items are
	 * cached then they are retrieved from the cache, otherwise a new, empty
	 * item is returned.
	 */
	protected List<DataItem> createItemsFromCache(String[] ids) {
		if (ids == null || ids.length == 0) {
			return Collections.<DataItem> emptyList();
		}
		// try to retrieve the item from the cache
		Map<String, Object> cacheMap = DataStoreCache.getInstance().readAll(ids, this.getName());
		List<DataItem> results = new ArrayList<>(ids.length);
		for (String id : ids) {
			if (!cacheMap.containsKey(id)) {
				// if there's no item in the cache then return an empty item
				results.add(new DatabaseDataItem(id, this));
			} else {
				// return the cached item
				@SuppressWarnings("unchecked")
				ValueContainer<DatabaseDataItem> valueContainer = (ValueContainer<DatabaseDataItem>) cacheMap.get(id);
				results.add(valueContainer.getValue());
			}
		}
		return results;
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	@Override
	public List<DataItem> executeNamedQuery(String queryName, Object... args) throws DataStoreException {
		NamedQuery namedQuery = this.getNamedQuery(queryName);
		if (namedQuery == null) {
			throw new DataStoreException("No query named " + queryName + " exists for data type " + this.getName());
		}
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement(namedQuery.getSql());
			} catch (SQLException e) {
				throw new DataStoreException("Named query " + namedQuery.getName() + " contains invalid SQL " + namedQuery.getSql() + " for data type " + this.getName(), e);
			}
			if (args != null) {
				int count = 1;
				for (Object arg : args) {
					pstmt.setObject(count++, arg);
				}
			}
			List<String> itemIds = null;
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					if (itemIds == null) {
						itemIds = new ArrayList<>();
					}
					itemIds.add(rs.getString(1));
				}
			}
			return (itemIds == null) ? Collections.<DataItem> emptyList() : this.createItemsFromCache(itemIds.toArray(new String[itemIds.size()]));
		} catch (SQLException e) {
			throw new DataStoreException("Failure while executing named query " + queryName + " for data type " + this.getName(), e);
		}
	}

	private List<DataTable> getAllTables() {
		if (this.allTables == null) {
			this.allTables = new ArrayList<>();
			this.allTables.add(this.getPrimaryTable());
			this.allTables.addAll(this.getAuxiliaryTables());
			this.allTables.addAll(this.getReferenceTables());
		}
		return this.allTables;
	}

	protected List<DataTable> getAuxiliaryTables() {
		return this.auxiliaryTables;
	}

	private List<CustomProperty> getCustomProperties() {
		return this.customProperties;
	}

	@Override
	public boolean isFcmEnabled() {
		return this.fcmEnabled;
	}

	protected String getIdPrefix() {
		return this.idPrefix;
	}

	protected String getIdProperty() {
		return this.idProperty;
	}

	protected List<DataTable> getReferenceTables() {
		return this.referenceTables;
	}

	@Override
	public String getName() {
		return this.name;
	}

	private List<NamedQuery> getNamedQueries() {
		return this.namedQueries;
	}

	private NamedQuery getNamedQuery(String queryName) {
		for (NamedQuery namedQuery : this.getNamedQueries()) {
			if (namedQuery.getName().equalsIgnoreCase(queryName)) {
				return namedQuery;
			}
		}
		return null;
	}

	protected DataTable getPrimaryTable() {
		return this.primaryTable;
	}

	protected List<DataItemPropertyDescriptor> getPropertyDescriptors() {
		if (this.propertyDescriptors == null) {
			this.propertyDescriptors = new ArrayList<>();
			for (DataTable table : this.getAllTables()) {
				for (DataColumn column : table.getColumns()) {
					this.propertyDescriptors.add(new DatabaseDataItemPropertyDescriptor(column));
				}
			}
			for (CustomProperty customProperty : this.getCustomProperties()) {
				this.propertyDescriptors.add(new DatabaseDataItemPropertyDescriptor(customProperty));
			}
		}
		return this.propertyDescriptors;
	}

	@Override
	public DataStoreProperty getPropertyByName(String property) {
		for (DataTable table : this.getAllTables()) {
			DataColumn column = table.getColumnForProperty(property);
			if (column != null) {
				return column;
			}
		}
		for (CustomProperty customProperty : this.getCustomProperties()) {
			if (customProperty.getProperty().equals(property)) {
				return customProperty;
			}
		}
		return null;
	}

	@Override
	public boolean isPubSubEnabled() {
		return this.pubSubEnabled;
	}

	@Override
	public DataItem readItemImmutable(String id) throws DataStoreException {
		return DatabaseDataItem.loadExistingItem(id, this);
	}

	@Override
	public MutableDataItem readItemForUpdate(String id) throws DataStoreException {
		return MutableDatabaseDataItem.loadExistingItem(id, this);
	}

	protected boolean isUseIdGenerator() {
		return this.useIdGenerator;
	}

	@Override
	public void validateConfig() throws DataStoreException {
		// TODO
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataType)) {
			return false;
		}
		DataType dataType = (DataType) obj;
		return (StringUtils.equals(this.getName(), dataType.getName()));
	}
}
