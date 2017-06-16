package com.heb.liquidsky.spring.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.heb.liquidsky.data.db.ConnectionManager;
import com.heb.liquidsky.data.db.HebDataSource;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

@Configuration
@PropertySource("classpath:application-datasource.properties")
public class HebSpringDataSource implements HebDataSource {

	private static final Logger logger = Logger.getLogger(HebSpringDataSource.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(HebSpringDataSource.class);
	private static final Object DATA_SOURCE_LOCK = new Object();
	private static DataSource DATA_SOURCE;

	@Autowired
	private Environment env;

	@Override
	public Connection getConnection() throws SQLException {
		return this.dataSource().getConnection();
	}

	/**
	 * Since database credentials are encrypted in Cloud Storage
	 * we cannot use Spring's OOTB data source configuration, so
	 * this method provides a custom method for building the
	 * data source.
	 */
	@Bean
	@Primary
	public DataSource dataSource() {
		synchronized (DATA_SOURCE_LOCK) {
			if (DATA_SOURCE == null) {
				this.initializeDataSource();
			}
			return DATA_SOURCE;
		}
	}

	private void initializeDataSource() {
		HebTraceContext context = TRACER.startSpan("initializeDataSource");
		try {
			DATA_SOURCE = (DataSource) DataSourceBuilder.create()
					.url(ConnectionManager.JDBC_URL)
					.driverClassName(ConnectionManager.JDBC_DRIVER_CLASS)
					.build();
			if (env == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Environment is null, connection pool will not be fully configured");
				}
			} else {
				DATA_SOURCE.setTestOnBorrow(Boolean.valueOf(env.getProperty("spring.datasource.tomcat.test-on-borrow")));
				DATA_SOURCE.setValidationQuery(env.getProperty("spring.datasource.tomcat.validation-query"));
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("DataSource initialized of type : " + DATA_SOURCE.getClass().getName());
				logger.info("DataSource configuration: " + DATA_SOURCE.toString());
			}
		} finally {
			TRACER.endSpan(context);
		}
	}
}
