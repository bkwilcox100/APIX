package com.heb.liquidsky.spring.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.endpoints.AdminRestAuditLogInterface;
import com.heb.liquidsky.endpoints.response.ServiceException;

/**
 * Defines the endpoint servlet for Audit Log requests.
 * Audit Log is read only.
 * 
 * @author Scott McArthur
 */
@RestController
@RequestMapping(value="/adminrest/v1")
public class AdminRestAuditLogServlet {

	private static final AdminRestAuditLogInterface INTERFACE_OBJECT = new AdminRestAuditLogInterface();

	@GetMapping(value="/auditlog")
	public List<Map<String, Object>> readAuditLogCollection() throws ServiceException {
		return INTERFACE_OBJECT.readAuditLogCollection();
	}
}
