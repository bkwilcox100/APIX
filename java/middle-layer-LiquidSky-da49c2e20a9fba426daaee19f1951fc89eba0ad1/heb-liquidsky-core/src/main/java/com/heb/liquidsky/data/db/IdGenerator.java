package com.heb.liquidsky.data.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class provides the capability for generating complex primary key
 * IDs.  In general it will combine a configured "prefix" value with a
 * consecutive integer to generate the unique ID.
 */
public final class IdGenerator {

	private static final String SQL_NEXT_ID_SELECT = "select LAST_INSERT_ID();";
	private static final String SQL_NEXT_ID_INSERT = "insert into heb_id_generator (next_id, data_type) values (?, ?)";
	private static final String SQL_NEXT_ID_UPDATE = "update heb_id_generator set next_id = LAST_INSERT_ID(next_id + 1) where data_type = ?;";

	private static final IdGenerator INSTANCE = new IdGenerator();
	private static final Logger logger = Logger.getLogger(IdGenerator.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(IdGenerator.class);

	private IdGenerator() {
		// only allow access to this class via the singleton instance
	}

	public static IdGenerator getInstance() {
		return INSTANCE;
	}

	/**
	 * Return the next available ID for the data type by grabbing the
	 * id generator prefix and the next consecutive id generator ID.
	 */
	protected String getNextId(Connection conn, DatabaseDataType dataType) throws SQLException {
		if (!dataType.isUseIdGenerator()) {
			throw new IllegalStateException(dataType.getName() + " is configured with use-id-generator=false");
		}
		HebTraceContext context = TRACER.startSpan("getNextId");
		try {
			// this code is a bit tricky - it updates the id generator
			// table, then returns the last inserted id for the current
			// connection using the last_insert_id() function.  no transaction
			// should be necessary since last_insert_id is bound to the
			// current connection.
			int nextId = -1;
			int updateCount = 0;
			try (PreparedStatement pstmt = conn.prepareStatement(SQL_NEXT_ID_UPDATE)) {
				pstmt.setString(1, dataType.getName());
				updateCount = pstmt.executeUpdate();
			}
			// see MLS-421 - last_insert_id gives the last insert ID
			// for any table, so check updateCount to make sure that
			// a row existed previously
			if (updateCount > 0) {
				try (PreparedStatement pstmt = conn.prepareStatement(SQL_NEXT_ID_SELECT)) {
					try (ResultSet rs = pstmt.executeQuery()) {
						if (rs.next()) {
							nextId = rs.getInt(1);
						}
					}
				}
			}
			if (nextId == -1) {
				// if no row exists in the heb_id_generator table for this data
				// type then insert one.  note that it is possible the insert
				// could conflict with another thread or instance, but since
				// that will only happen after a new data type is first
				// initialized it may be an acceptable risk
				try (PreparedStatement pstmt = conn.prepareStatement(SQL_NEXT_ID_INSERT)) {
					nextId = 1;
					pstmt.setInt(1, nextId);
					pstmt.setString(2, dataType.getName());
					pstmt.executeUpdate();
				}
			}
			String nextIdString = Integer.toString(nextId);
			String idPrefix = dataType.getIdPrefix();
			nextIdString = (StringUtils.isBlank(idPrefix) ? nextIdString : idPrefix + nextIdString);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returning next_id " + nextIdString + " for data type " + dataType.getName());
			}
			return nextIdString;
		} finally {
			TRACER.annotateSpan(context, "data-type", dataType.getName());
			TRACER.endSpan(context);
		}
	}
}
