package com.heb.liquidsky.data.db;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.common.HebEnvironmentProperties;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class is responsible for managing connections to the database.
 */
public final class ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ConnectionManager.class);
	private static final String DATA_SOURCE_CLASS_NON_SPRING = "com.heb.liquidsky.data.db.NonPoolingDataSource";
	private static final String DATA_SOURCE_CLASS_SPRING = "com.heb.liquidsky.spring.jdbc.HebSpringDataSource";
	private static final String DEV_JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private static final String DEV_JDBC_URL = CloudUtil.getProperty("CLOUD_SQL_DATABASE_LOCAL_URL");
	private static final String DEV_JDBC_USER = CloudUtil.getProperty("CLOUD_SQL_DATABASE_LOCAL_USER");
	private static final String DEV_JDBC_PASSWORD = CloudUtil.getProperty("CLOUD_SQL_DATABASE_LOCAL_PASSWORD");
	private static final String JDBC_DRIVER_CLASS_NON_SPRING = "com.mysql.jdbc.GoogleDriver";
	private static final String JDBC_DRIVER_CLASS_SPRING = "com.mysql.cj.jdbc.Driver";
	private static final String PROD_JDBC_DRIVER_CLASS = CloudUtil.isSpringEnvironment() ? JDBC_DRIVER_CLASS_SPRING : JDBC_DRIVER_CLASS_NON_SPRING;
	private static final String PROD_JDBC_URL = CloudUtil.getProperty("CLOUD_SQL_DATABASE_URL");
	private static final String PROD_JDBC_USER = CloudUtil.getProperty("CLOUD_SQL_DATABASE_USER");
	private static final String PROD_JDBC_PASSWORD = CloudUtil.getProperty("CLOUD_SQL_DATABASE_PASSWORD");
	public static final String JDBC_DRIVER_CLASS = (HebEnvironmentProperties.getInstance().isLocalInstance()) ? DEV_JDBC_DRIVER_CLASS : PROD_JDBC_DRIVER_CLASS;
	public static final String JDBC_URL = buildJDBCUrl();
	private static final Object DATA_SOURCE_LOCK = new Object();
	private static final ConnectionManager INSTANCE = new ConnectionManager();

	private HebDataSource dataSource;

	private ConnectionManager() {
		// only allow access to this class via the singleton instance
	}

	public Connection getConnection() throws SQLException {
		HebTraceContext context = TRACER.startSpan("getConnection");
		try {
			return getDataSource().getConnection();
		} finally {
			TRACER.endSpan(context);
		}
	}

	private static String buildJDBCUrl() {
		String url = null;
		if (HebEnvironmentProperties.getInstance().isLocalInstance()) {
			url = DEV_JDBC_URL;
			url = CloudUtil.appendQueryParam(url, "user", DEV_JDBC_USER);
			url = CloudUtil.appendQueryParam(url, "password", DEV_JDBC_PASSWORD);
		} else {
			url = PROD_JDBC_URL;
			url = CloudUtil.appendQueryParam(url, "user", PROD_JDBC_USER);
			url = CloudUtil.appendQueryParam(url, "password", PROD_JDBC_PASSWORD);
		}
		return url;
	}

	private HebDataSource getDataSource() {
		synchronized (DATA_SOURCE_LOCK) {
			if (this.dataSource == null) {
				if (CloudUtil.isSpringEnvironment()) {
					this.dataSource = initializeDataSource(DATA_SOURCE_CLASS_SPRING);
				} else {
					this.dataSource = initializeDataSource(DATA_SOURCE_CLASS_NON_SPRING);
				}
			}
			return this.dataSource;
		}
	}

	private HebDataSource initializeDataSource(String dataSourceClassName) throws IllegalArgumentException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Initializing data source of type " + dataSourceClassName);
		}
		try {
			@SuppressWarnings("unchecked")
			Class<HebDataSource> dataSourceClass = (Class<HebDataSource>) Class.forName(dataSourceClassName);
			Constructor<HebDataSource> constructor = dataSourceClass.getConstructor();
			return constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			String msg = "Cannot create data source with class name " + dataSourceClassName;
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, msg, e);
			}
			throw new IllegalArgumentException(msg, e);
		}
	}

	public static ConnectionManager getInstance() {
		return INSTANCE;
	}
}
