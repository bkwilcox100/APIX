package com.heb.liquidsky.endpoints;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.ResourceUtils;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Defines v1 of the Admin Portal Audit Log interface
 * Audit logs can only be read
 * 
 * @author Scott McArthur
 *
 */
public class AdminRestAuditLogInterface {

	private static final Logger logger = Logger.getLogger(AdminRestAuditLogInterface.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(AdminRestAuditLogInterface.class);

	private static final String DATA_ITEM_NAME_AUDIT_LOG = "liquidSkyAdminAuditLog";
	private static final String AUDIT_LOG_COLLECTION_QUERY = "getLastNAuditLogs";
	private static final String CONTEXT_FILTER = "default";


	/*
	 * =============================================================================
	 * Read Operations
	 * (No audit logging for read operations)
	 * =============================================================================
	 */
	
	public List<Map<String, Object>> readAuditLogCollection() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("readAppPropertiesCollection");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			return ResourceUtils.readCollectionFromQuery(AUDIT_LOG_COLLECTION_QUERY, DATA_ITEM_NAME_AUDIT_LOG, CONTEXT_FILTER, 100);
		} finally {
			TRACER.endSpan(context);
		}
	}
}