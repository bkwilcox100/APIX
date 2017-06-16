package com.heb.liquidsky.data.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.data.AbstractDataItem;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataItemDescriptor;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreCache;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.DataStoreProperty;
import com.heb.liquidsky.data.DataType;
import com.heb.liquidsky.data.custom.CustomProperty;
import com.heb.liquidsky.trace.Label;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class represents a read-only data store item that is
 * persisted in a database.
 */
public class DatabaseDataItem extends AbstractDataItem implements Serializable {

	private static final long serialVersionUID = 3l;
	private static final Logger logger = Logger.getLogger(DatabaseDataItem.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(DatabaseDataItem.class);

	private final Map<String, Serializable> localDataMap = new HashMap<>();
	private transient DataItemDescriptor dataItemDescriptor;
	private transient DatabaseDataType dataTypeObj;
	private final String dataType;
	private String id;

	protected DatabaseDataItem(String id, String dataType) {
		this.dataType = dataType;
		this.setId(id);
	}

	protected DatabaseDataItem(String id, DatabaseDataType dataTypeObj) {
		this.dataTypeObj = dataTypeObj;
		this.dataType = dataTypeObj.getName();
		this.setId(id);
	}

	@Override
	public DatabaseDataItem copyItem() {
		DatabaseDataItem newItem = new DatabaseDataItem(this.getId(), this.getDataType());
		newItem.localDataMap.putAll(this.localDataMap);
		return newItem;
	}

	/**
	 * Return an existing data item from the database.  If no data item
	 * exists with the specified ID then this method returns <code>null</code>.
	 */
	protected static DatabaseDataItem loadExistingItem(String id, DatabaseDataType dataTypeObj) throws DataStoreException {
		DatabaseDataItem dataItem = new DatabaseDataItem(id, dataTypeObj);
		return (dataItem.readAll()) ? dataItem : null;
	}

	protected <T extends Object> void addData(String key, T value) {
		if (value instanceof List) {
			// convert list to array to make the cache faster via simpler serialization
			@SuppressWarnings("unchecked")
			String[] listAsArray = ((List<String>) value).toArray(new String[0]);
			this.localDataMap.put(key, listAsArray);
		} else {
			this.localDataMap.put(key, (Serializable) value);
		}
	}

	@Override
	protected void delete() throws DataStoreException {
		throw new UnsupportedOperationException("Delete operations are not allowed for immutable data store items");
	}

	@Override
	public Timestamp getCreationDate() throws DataStoreException {
		return (Timestamp) this.getObject(DataType.PROP_CREATION_DATE);
	}

	@Override
	public DataItemDescriptor dataItemDescriptor() {
		if (this.dataItemDescriptor == null) {
			this.dataItemDescriptor = new DatabaseDataItemDescriptor(this.getDataType());
		}
		return this.dataItemDescriptor;
	}

	@Override
	public DatabaseDataType getDataType() {
		if (this.dataTypeObj == null) {
			try {
				this.dataTypeObj = (DatabaseDataType) DataStore.getInstance().getDataType(this.dataType);
			} catch (DataStoreException e) {
				throw new IllegalArgumentException("Invalid data type " + this.dataType, e);
			}
		}
		return this.dataTypeObj;
	}

	@Override
	public String getId() {
		return this.id;
	}

	protected void setId(String id) {
		this.id = id;
		this.addData(this.getDataType().getIdProperty(), id);
	}

	@Override
	public Integer getInt(String property) throws DataStoreException {
		return (Integer) this.getObject(property);
	}

	@Override
	public DataItem getItem(String property) throws DataStoreException {
		DataStoreProperty dataProperty = this.getDataType().getPropertyByName(property);
		if (dataProperty == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to retrieve non-existent property '" + property + "' for " + this.getDataType().getName() + ":" + this.getId());
			}
			return null;
		}
		String itemId = this.getString(property);
		DatabaseDataType dataType = (DatabaseDataType) DataStore.getInstance().getDataType(dataProperty.getItemType());
		return (itemId != null) ? dataType.createItemFromCache(itemId) : null;
	}

	@Override
	public Timestamp getLastModifiedDate() throws DataStoreException {
		return (Timestamp) this.getObject(DataType.PROP_LAST_MODIFIED_DATE);
	}

	/**
	 * Return a child property that is a list of DataItem objects.  Internally
	 * the list is stored as a list of item IDs that is then converted to a
	 * transient list of objects, since keeping a list of references is more
	 * efficient for caching purposes than keeping an actual list of objects.
	 */
	@Override
	public List<DataItem> getList(String property) throws DataStoreException {
		List<DataItem> result = this.getListInternal(property);
		return (result == null || result.isEmpty()) ? Collections.<DataItem> emptyList() : Collections.<DataItem> unmodifiableList(result);
	}

	/**
	 * Internal method that returns a list property without converting
	 * <code>null</code> values to empty lists or making non-empty lists
	 * immutable.
	 */
	protected List<DataItem> getListInternal(String property) throws DataStoreException {
		DataStoreProperty dataProperty = this.getDataType().getPropertyByName(property);
		if (dataProperty == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to retrieve non-existent list property '" + property + "' for " + this.getDataType().getName() + ":" + this.getId());
			}
			return null;
		}
		String[] itemIds = (String[]) this.getObject(property);
		List<DataItem> result = null;
		if (itemIds != null && itemIds.length > 0) {
			DatabaseDataType dataType = (DatabaseDataType) DataStore.getInstance().getDataType(dataProperty.getListItemType());
			result = dataType.createItemsFromCache(itemIds);
		}
		return result;
	}

	@Override
	public Long getLong(String property) throws DataStoreException {
		return (Long) this.getObject(property);
	}

	@Override
	public Object getObject(String property) throws DataStoreException {
		ValueContainer<? extends Object> value = this.getValueContainer(property);
		if (value == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Property " + property + " not in memory for " + this.getDataType().getName() + ":" + this.getId());
			}
			DataStoreProperty dataProperty = this.getDataType().getPropertyByName(property);
			if (dataProperty != null) {
				if (dataProperty instanceof DataColumn) {
					DataTable dataTable = ((DataColumn) dataProperty).getTable();
					// if the table is a lazy load table then load only that
					// table, otherwise load all non-lazy load table data
					if (dataTable.isLazyLoad()) {
						this.read(dataTable);
					} else {
						this.readAll();
					}
				} else if (dataProperty instanceof CustomProperty) {
					this.addData(dataProperty.getProperty(), ((CustomProperty) dataProperty).getCustomPropertyImplementation().getPropertyValue(this));
				}
				value = this.getValueContainer(property);
			}
		}
		return (value != null) ? value.getValue() : null;
	}

	protected ValueContainer<? extends Object> getValueContainer(String property) {
		Object obj = this.localDataMap.get(property);
		if (obj != null) {
			return new ValueContainer<>(obj);
		} else if (this.localDataMap.containsKey(property)) {
			return ValueContainer.getEmptyValueContainer();
		} else {
			return null;
		}
	}

	@Override
	public String getString(String property) throws DataStoreException {
		return (String) this.getObject(property);
	}

	@Override
	public Timestamp getTimestamp(String property) throws DataStoreException {
		return (Timestamp) this.getObject(property);
	}

	@Override
	protected void insert() throws DataStoreException {
		throw new UnsupportedOperationException("Insert operations are not allowed for immutable data store items");
	}

	private void read(DataTable dataTable) throws DataStoreException {
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			this.readTable(conn, dataTable);
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
		this.updateInCache();
	}

	protected boolean readAll() throws DataStoreException {
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			boolean result = this.readTable(conn, this.getDataType().getPrimaryTable());
			if (!result) {
				return false;
			}
			for (DataTable auxiliaryTable : this.getDataType().getAuxiliaryTables()) {
				if (auxiliaryTable.isLazyLoad()) {
					continue;
				}
				this.readTable(conn, auxiliaryTable);
			}
			for (DataTable referenceTable : this.getDataType().getReferenceTables()) {
				if (referenceTable.isLazyLoad()) {
					continue;
				}
				this.readTable(conn, referenceTable);
			}
			this.updateInCache();
			return result;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private boolean readTable(Connection conn, DataTable dataTable) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("readTable");
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Retrieving record with ID: " + this.getId() + " from table " + dataTable.getName());
			}
			boolean result = false;
			String sql = dataTable.readQuery();
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, this.getId());
				try {
					try (ResultSet rs = pstmt.executeQuery()) {
						if (dataTable.getListItemColumn() != null) {
							result = this.readList(dataTable, rs);
						} else {
							result = this.readSingle(dataTable, rs);
						}
					}
				} catch (SQLException e) {
					this.rethrowWithQueryInfo(pstmt, sql, e);
				}
			} catch (SQLException e) {
				throw new DataStoreException("Failure reading from table " + dataTable.getName() + " for ID " + this.getId(), e);
			}
			return result;
		} finally {
			TRACER.annotateSpan(context, new Label("item", this.toString()), new Label("table", dataTable.getName()));
			TRACER.endSpan(context);
		}
	}

	private boolean readList(DataTable dataTable, ResultSet rs) throws SQLException {
		DataColumn listItemColumn = dataTable.getListItemColumn();
		List<String> elements = Collections.<String> emptyList();
		while (rs.next()) {
			if (elements.isEmpty()) {
				elements = new ArrayList<>();
			}
			String id = rs.getString(listItemColumn.getColumnName());
			elements.add(id);
		}
		this.addData(listItemColumn.getProperty(), elements);
		return true;
	}

	private boolean readSingle(DataTable dataTable, ResultSet rs) throws SQLException, DataStoreException {
		int recordCount = 0;
		while (rs.next()) {
			recordCount++;
			if (recordCount > 1) {
				throw new DataStoreException("Invalid data in the data store - multiple records exist with ID '" + this.getId() + "'");
			}
			for (DataColumn dataColumn : dataTable.getColumns()) {
				Object object = rs.getObject(dataColumn.getColumnName());
				this.addData(dataColumn.getProperty(), object);
			}
		}
		if (recordCount == 0) {
			for (DataColumn dataColumn : dataTable.getColumns()) {
				this.addData(dataColumn.getProperty(), null);
			}
		}
		return (recordCount == 1);
	}

	/**
	 * Convert the data item to a map so that it can be easily accessed
	 * in formats like JSTL via "${item.property}".  Note that this method
	 * does not utilize lazy-loading and thus may have a significant
	 * performance impact; as such it should be used sparingly.
	 */
	@Override
	public Map<String, Object> toMap(int maxDepth) throws DataStoreException {
		return this.buildMap(maxDepth);
	}

	private Map<String, Object> buildMap(int maxDepth) throws DataStoreException {
		Map<String, Object> map = new HashMap<>();
		if (maxDepth > 0) {
			this.buildMap(map, this.getDataType().getPrimaryTable(), maxDepth);
			for (DataTable dataTable : this.getDataType().getAuxiliaryTables()) {
				this.buildMap(map, dataTable, maxDepth);
			}
			for (DataTable dataTable : this.getDataType().getReferenceTables()) {
				this.buildMap(map, dataTable, maxDepth);
			}
		}
		return map;
	}

	private void buildMap(Map<String, Object> map, DataTable dataTable, int maxDepth) throws DataStoreException {
		for (DataColumn dataColumn : dataTable.getColumns()) {
			String property = dataColumn.getProperty();
			Object value = this.getObject(property);
			if (value instanceof List) {
				List<DataItem> dataItems = this.getList(property);
				List<Map<String, Object>> childList = new ArrayList<>();
				for (DataItem dataItem : dataItems) {
					childList.add(dataItem.toMap(maxDepth - 1));
				}
				map.put(property, childList);
			} else if (value instanceof DataItem) {
				map.put(property, ((DataItem) value).toMap(maxDepth - 1));
			} else {
				map.put(property, value);
			}
		}
	}

	/**
	 * This utility method simply wraps a SQL exception with information
	 * about the PreparedStatement that failed.
	 *
	 * @param pstmt The statement that generated the error.
	 * @param query The SQL that generated the error.
	 * @param e The error that was generated.
	 */
	protected void rethrowWithQueryInfo(PreparedStatement pstmt, String query, SQLException e) throws SQLException {
		String msg = "Failure executing: " + query;
		throw new SQLException(msg, e);
	}

	@Override
	protected void update() throws DataStoreException {
		throw new UnsupportedOperationException("Update operations are not allowed for immutable data store items");
	}

	protected void updateInCache() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateInCache() invoked from data item post-read");
		}
		DataStoreCache.getInstance().update(this.getId(), this.getDataType().getName(), this);
	}
}
