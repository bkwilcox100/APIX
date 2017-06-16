package com.heb.liquidsky.spring.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.endpoints.response.ApiCollection;
import com.heb.liquidsky.endpoints.response.InternalServerErrorException;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Defines v1 of the Api Discovery service
 * This service returns objects that locate and describe microservices within HEB
 * https://confluence.heb.com:8443/display/ESELLING/API+Discovery+Microservice
 * 
 * @author Scott McArthur
 *
 */
@RestController
@RequestMapping(value="/apidiscovery/v1")
public class ApiDiscoveryInterfaceV1 {

	private static final Logger logger = Logger.getLogger(ApiDiscoveryInterfaceV1.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ApiDiscoveryInterfaceV1.class);

	/**
	 * Top Level GET of an API Collection Object
	 * 
	 * @param request - contents of the request
	 * @param collectionId - Id of the collection to return
	 * @param filter - Intended for a query filter parameter (This is not implemented yet as design for this Liquid Sky standard is not complete)
	 *  
	 * @return ApiCollection object
	 * @throws InternalServerErrorException
	 */
	@GetMapping(value="/apicollection/{collectionId}")
	public ApiCollection getApiCollection(
			HttpServletRequest request,
			@PathVariable String collectionId) 
					throws InternalServerErrorException, 
					ServiceException {
		
		HebTraceContext context = TRACER.startSpan("getApiCollection");
		try {
			if (logger.isLoggable(Level.FINEST)) logger.finest(CloudUtil.getMethodName() + ": triggered.");
			return new ApiCollection(collectionId);
		} finally {
			TRACER.endSpan(context);
		}
	}

}
