package com.heb.liquidsky.data.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.DataStoreProperty;
import com.heb.liquidsky.data.DataType;
import com.heb.liquidsky.data.MutableDataItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class MutableDatabaseDataItem extends DatabaseDataItem implements MutableDataItem {

	private static final long serialVersionUID = 2l;
	private static final Logger logger = Logger.getLogger(MutableDatabaseDataItem.class.getName());

	private boolean pubSubEnabled = true;

	private MutableDatabaseDataItem(String id, String dataType) {
		super(id, dataType);
	}

	private MutableDatabaseDataItem(String id, DatabaseDataType dataTypeObj) {
		super(id, dataTypeObj);
	}

	/**
	 * Return an empty data item that is not (yet) persisted to the database.
	 * This method should be used when creating a new data item that will
	 * later be inserted into the database.
	 */
	protected static MutableDatabaseDataItem createItem(String id, DatabaseDataType dataTypeObj) {
		return new MutableDatabaseDataItem(id, dataTypeObj);
	}

	/**
	 * Return an existing data item from the database.  If no data item
	 * exists with the specified ID then this method returns <code>null</code>.
	 */
	protected static MutableDatabaseDataItem loadExistingItem(String id, DatabaseDataType dataTypeObj) throws DataStoreException {
		MutableDatabaseDataItem dataItem = new MutableDatabaseDataItem(id, dataTypeObj);
		return (dataItem.readAll()) ? dataItem : null;
	}

	@Override
	public void delete() throws DataStoreException {
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			try {
				conn.setAutoCommit(false);
				this.delete(conn);
				conn.commit();
			} catch (DataStoreException | SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DataStoreException("Failure deleting data item for ID " + this.getId(), e);
		}
	}

	private void delete(Connection conn) throws DataStoreException, SQLException {
		for (DataTable dataTable : this.getDataType().getReferenceTables()) {
			this.deleteCascade(conn, dataTable);
			this.updateReferenceTable(conn, dataTable);
		}
		for (DataTable dataTable : this.getDataType().getAuxiliaryTables()) {
			this.deleteCascade(conn, dataTable);
			this.delete(conn, dataTable);
		}
		this.deleteCascade(conn, this.getDataType().getPrimaryTable());
		this.delete(conn, this.getDataType().getPrimaryTable());
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void delete(Connection conn, DataTable dataTable) throws SQLException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Deleting record with ID: " + this.getId() + " from table " + dataTable.getName());
		}
		String sql = dataTable.deleteQuery();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, this.getId());
			try {
				pstmt.executeUpdate();
			} catch (SQLException e) {
				this.rethrowWithQueryInfo(pstmt, sql, e);
			}
		}
	}

	/**
	 * If dependent items are configured for cascade delete, remove each
	 * of them.
	 */
	private void deleteCascade(Connection conn, DataTable dataTable) throws DataStoreException, SQLException {
		for (DataColumn dataColumn : dataTable.getColumns()) {
			if (!dataColumn.isCascadeDelete()) {
				continue;
			}
			if (dataColumn.getItemType() != null) {
				String itemId = this.getString(dataColumn.getProperty());
				if (itemId != null) {
					MutableDatabaseDataItem dataItem = new MutableDatabaseDataItem(itemId, dataColumn.getItemType());
					dataItem.delete(conn);
				}
			} else if (dataColumn.getListItemType() != null) {
				String[] itemIds = (String[]) this.getObject(dataColumn.getProperty());
				if (itemIds != null) {
					for (String itemId : itemIds) {
						MutableDatabaseDataItem dataItem = new MutableDatabaseDataItem(itemId, dataColumn.getListItemType());
						dataItem.delete(conn);
					}
				}
			}
		}
	}

	private String[] getItemIdsForDataColumn(DataColumn dataColumn) {
		String[] itemIds = null;
		if (dataColumn.getItemType() != null) {
			// single item
			@SuppressWarnings("unchecked")
			ValueContainer<String> valueContainer = (ValueContainer<String>) this.getValueContainer(dataColumn.getProperty());
			if (valueContainer != null && valueContainer.getValue() != null) {
				itemIds = new String[1];
				itemIds[0] = valueContainer.getValue();
			}
		} else if (dataColumn.getListItemType() != null) {
			// list of items
			@SuppressWarnings("unchecked")
			ValueContainer<String[]> valueContainer = (ValueContainer<String[]>) this.getValueContainer(dataColumn.getProperty());
			itemIds = (valueContainer != null) ? valueContainer.getValue() : null;
		}
		return itemIds;
	}

	/**
	 * Override the parent method to return a mutable (not immutable) list
	 * in cases where there are currently no items so that the caller can
	 * perform list modification operations such as "getList(property).add(item)".
	 */
	@Override
	public List<DataItem> getList(String property) throws DataStoreException {
		List<DataItem> result = this.getListInternal(property);
		return (result == null) ? new ArrayList<DataItem>() : result;
	}

	@Override
	public void insert() throws DataStoreException {
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			try {
				conn.setAutoCommit(false);
				this.insertNonListItem(conn, this.getDataType().getPrimaryTable());
				for (DataTable dataTable : this.getDataType().getAuxiliaryTables()) {
					if (dataTable.getListItemColumn() != null) {
						this.insertListItem(conn, dataTable);
					} else {
						this.insertNonListItem(conn, dataTable);
					}
				}
				for (DataTable dataTable : this.getDataType().getReferenceTables()) {
					this.updateReferenceTable(conn, dataTable);
				}
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DataStoreException("Failure inserting data item for ID " + this.getId(), e);
		}
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void insertListItem(Connection conn, DataTable dataTable) throws SQLException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Inserting list record with ID: " + this.getId() + " in table " + dataTable.getName());
		}
		DataColumn listItemColumn = dataTable.getListItemColumn();
		if (listItemColumn.isReadOnly()) {
			return;
		}
		String[] itemIds = this.getItemIdsForDataColumn(listItemColumn);
		if (itemIds == null || itemIds.length == 0) {
			// no items to insert
			return;
		}
		String sql = dataTable.insertQuery(false);
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			for (String itemId : itemIds) {
				int count = 1;
				pstmt.setObject(count++, this.getId());
				pstmt.setObject(count++, itemId);
				pstmt.addBatch();
			}
			try {
				pstmt.executeBatch();
			} catch (SQLException e) {
				this.rethrowWithQueryInfo(pstmt, sql, e);
			}
		}
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void insertNonListItem(Connection conn, DataTable dataTable) throws SQLException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Inserting record with ID: " + this.getId() + " in table " + dataTable.getName());
		}
		int count = 1;
		boolean autoGenerateId = (dataTable.isPrimary() && this.getId() == null && !this.getDataType().isUseIdGenerator());
		String sql = dataTable.insertQuery(autoGenerateId);
		try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			if (dataTable.isAuxiliary()) {
				// id is not a configured property for non-primary tables,
				// so it must be explicitly included in the SQL
				pstmt.setObject(count++, this.getId());
			} else if (dataTable.isPrimary() && this.getId() == null && this.getDataType().isUseIdGenerator()) {
				// if ID needs to be auto-generated then do so
				String nextId = IdGenerator.getInstance().getNextId(conn, this.getDataType());
				this.setId(nextId);
			}
			for (DataColumn dataColumn : dataTable.getColumns()) {
				if (!dataColumn.isInsertable(autoGenerateId)) {
					continue;
				}
				ValueContainer<? extends Object> valueContainer = this.getValueContainer(dataColumn.getProperty());
				Object value = (valueContainer != null) ? valueContainer.getValue() : null;
				pstmt.setObject(count++, value);
			}
			try {
				pstmt.executeUpdate();
			} catch (SQLException e) {
				this.rethrowWithQueryInfo(pstmt, sql, e);
			}
			if (dataTable.isPrimary() && this.getId() == null && !this.getDataType().isUseIdGenerator()) {
				// if ID was auto-generated by the database then retrieve it
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						String nextId = Integer.toString(rs.getInt(1));
						this.setId(nextId);
					}
				}
			}
			this.updateAutoGeneratedFields(conn, dataTable);
		}
	}

	@Override
	public void setProperty(String property, Object value) {
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException("Null property passed to setProperty()");
		}
		DataStoreProperty dataProperty = this.getDataType().getPropertyByName(property);
		if (dataProperty == null) {
			throw new IllegalArgumentException("No property named " + property + " has been configured for data type " + this.getDataType().getName());
		}
		if (StringUtils.equals(property, this.getDataType().getIdProperty())) {
			throw new IllegalArgumentException("The ID property cannot be modified");
		}
		if (dataProperty.isReadOnly()) {
			throw new IllegalArgumentException(property + " is read-only and cannot be modified");
		}
		// do not allow blank values since MySQL will allow an empty
		// string in a NOT NULL column. convert to null if blank.
		if (value instanceof String && StringUtils.isBlank((String) value)) {
			value = null;
		}
		if (value != null) {
			if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<DataItem> dataItems = (List<DataItem>) value;
				String[] itemIds = new String[dataItems.size()];
				for (int i = 0; i < dataItems.size(); i++) {
					itemIds[i] = dataItems.get(i).getId();
				}
				value = itemIds;
			}
			if (value instanceof DataItem) {
				value = ((DataItem) value).getId();
			}
		}
		this.addData(property, value);
	}

	@Override
	public boolean isPubSubEnabled() {
		return this.pubSubEnabled;
	}

	@Override
	public void setPubSubEnabled(boolean enabled) {
		this.pubSubEnabled = enabled;
	}

	@Override
	public void update() throws DataStoreException {
		try (Connection conn = ConnectionManager.getInstance().getConnection()) {
			try {
				conn.setAutoCommit(false);
				this.validateLastModifiedDate(conn);
				this.updateNonListItem(conn, this.getDataType().getPrimaryTable());
				for (DataTable dataTable : this.getDataType().getAuxiliaryTables()) {
					if (dataTable.getListItemColumn() != null) {
						this.updateListItem(conn, dataTable);
					} else {
						this.updateNonListItem(conn, dataTable);
					}
				}
				for (DataTable dataTable : this.getDataType().getReferenceTables()) {
					this.updateReferenceTable(conn, dataTable);
				}
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DataStoreException("Failure updating data item for ID " + this.getId(), e);
		}
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void updateAutoGeneratedFields(Connection conn, DataTable dataTable) throws SQLException {
		if (!dataTable.isPrimary()) {
			return;
		}
		String sql = dataTable.autoGeneratedFieldsQuery();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, this.getId());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					throw new SQLException("Unable to set auto-generated properties for record " + this.getId());
				}
				this.addData(DataType.PROP_CREATION_DATE, rs.getTimestamp(1));
				this.addData(DataType.PROP_LAST_MODIFIED_DATE, rs.getTimestamp(2));
			}
		}
	}

	private void updateListItem(Connection conn, DataTable dataTable) throws SQLException {
		DataColumn listItemColumn = dataTable.getListItemColumn();
		if (listItemColumn.isReadOnly()) {
			return;
		}
		@SuppressWarnings("unchecked")
		ValueContainer<List<String>> valueContainer = (ValueContainer<List<String>>) this.getValueContainer(listItemColumn.getProperty());
		if (valueContainer != null) {
			// an update is only needed if values have been modified,
			// in which case valueContainer won't be null.
			this.delete(conn, dataTable);
			this.insertListItem(conn, dataTable);
		}
	}

	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void updateNonListItem(Connection conn, DataTable dataTable) throws SQLException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Updating record with ID: " + this.getId() + " in table " + dataTable.getName());
		}
		int count = 1;
		String sql = dataTable.updateQuery();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			for (DataColumn dataColumn : dataTable.getColumns()) {
				if (!dataColumn.isUpdatable()) {
					continue;
				}
				ValueContainer<? extends Object> valueContainer = this.getValueContainer(dataColumn.getProperty());
				Object value = (valueContainer != null) ? valueContainer.getValue() : null;
				pstmt.setObject(count++, value);
			}
			pstmt.setString(count++, this.getId());
			try {
				pstmt.executeUpdate();
				this.updateAutoGeneratedFields(conn, dataTable);
			} catch (SQLException e) {
				this.rethrowWithQueryInfo(pstmt, sql, e);
			}
		}
	}

	/**
	 * A reference table shares data with another data type, so when
	 * updating or deleting ONLY the reference to this item should be
	 * changed.  That means (for example) that list types would simply
	 * update the list reference column in the reference tables, so if
	 * a product referenced child SKUs only the parent product field
	 * would change.
	 */
	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void updateReferenceTable(Connection conn, DataTable dataTable) throws SQLException {
		DataColumn referenceColumn = dataTable.getReferenceColumn();
		if (referenceColumn.isReadOnly()) {
			return;
		}
		@SuppressWarnings("unchecked")
		ValueContainer<List<String>> valueContainer = (ValueContainer<List<String>>) this.getValueContainer(referenceColumn.getProperty());
		if (valueContainer == null) {
			// an update is only needed if values have been modified,
			// in which case valueContainer won't be null.
			return;
		}
		// set old references to this item null
		String sql = dataTable.deleteReferenceQuery();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			int count = 1;
			pstmt.setObject(count++, this.getId());
			pstmt.addBatch();
			try {
				pstmt.executeUpdate();
			} catch (SQLException e) {
				this.rethrowWithQueryInfo(pstmt, sql, e);
			}
		}
		// insert new references to this item
		String[] itemIds = this.getItemIdsForDataColumn(referenceColumn);
		if (itemIds != null && itemIds.length > 0) {
			sql = dataTable.insertReferenceQuery();
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				for (String itemId : itemIds) {
					int count = 1;
					pstmt.setObject(count++, this.getId());
					pstmt.setObject(count++, itemId);
					pstmt.addBatch();
				}
				try {
					pstmt.executeBatch();
				} catch (SQLException e) {
					this.rethrowWithQueryInfo(pstmt, sql, e);
				}
			}
		}
	}

	@Override
	protected void updateInCache() {
		// make this a no-op since mutable items should not be cached
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateInCache() disabled for mutable items, skipping cache update");
		}
	}

	/**
	 * Before performing an update, make sure that the last modified date
	 * of the current item matches the last modified date of the item in
	 * the database.  If it does not it means that the database and cache
	 * are out of sync and the operation needs to be retried.
	 */
	@SuppressFBWarnings(
			value="SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
			justification="SQL is obtained from an internal method and not an untrusted source"
		)
	private void validateLastModifiedDate(Connection conn) throws DataStoreException {
		String sql = this.getDataType().getPrimaryTable().lastModifiedQuery();
		Timestamp lastModifiedDate = this.getTimestamp(DataType.PROP_LAST_MODIFIED_DATE);
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, this.getId());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					throw new ConcurrentModificationException(this.getDataType().getName() + " with ID "+ this.getId() + " cannot be updated because no matching record exists.");
				}
				Timestamp dbLastModifiedDate = rs.getTimestamp(1);
				if (dbLastModifiedDate == null || !dbLastModifiedDate.equals(lastModifiedDate)) {
					throw new ConcurrentModificationException(this.getDataType().getName() + " with ID "+ this.getId() + " has last modified date " + lastModifiedDate + " which does not match the database value " + dbLastModifiedDate + ". Concurrent update not allowed to avoid overwriting data.");
				}
			}
		} catch (SQLException e) {
			throw new DataStoreException("Failure executing query " + sql + " for ID " + this.getId(), e);
		}
	}
}
