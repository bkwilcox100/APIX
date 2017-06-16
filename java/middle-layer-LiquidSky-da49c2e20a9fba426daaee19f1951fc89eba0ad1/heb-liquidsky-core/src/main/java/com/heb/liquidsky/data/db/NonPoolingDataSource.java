package com.heb.liquidsky.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TEMPORARY class that implements a non-pooling data source.
 * This data source will be replaced with Spring OOTB
 * capabilities.
 */
@Deprecated
public class NonPoolingDataSource implements HebDataSource {

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(ConnectionManager.JDBC_URL);
	}
}
