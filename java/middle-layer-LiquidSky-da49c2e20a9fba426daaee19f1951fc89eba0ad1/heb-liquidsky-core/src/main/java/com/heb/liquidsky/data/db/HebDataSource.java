package com.heb.liquidsky.data.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface HebDataSource {

	public Connection getConnection() throws SQLException;
}
