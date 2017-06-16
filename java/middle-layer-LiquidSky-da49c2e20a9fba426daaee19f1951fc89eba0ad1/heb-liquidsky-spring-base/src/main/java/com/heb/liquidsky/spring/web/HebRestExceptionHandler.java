package com.heb.liquidsky.spring.web;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.response.ServiceExceptionErrorItem;

/**
 * This class provides global handling for ServiceException errors
 * thrown by REST services.
 */
@Configuration
@ControllerAdvice
public class HebRestExceptionHandler {

	private static final Logger logger = Logger.getLogger(HebRestExceptionHandler.class.getName());

	@ExceptionHandler({ServiceException.class})
	public void handleServiceException(HttpServletRequest request, HttpServletResponse response, ServiceException e) throws IOException {
		if (logger.isLoggable(Level.SEVERE) && e.getErrors() != null) {
			for (ServiceExceptionErrorItem item : e.getErrors()) {
				if (item.getException() != null) {
					logger.log(Level.SEVERE, ((item.getObject() != null) ? item.getObject().toString() : null), item.getException());
				}
			}
			
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.log(Level.WARNING, "Failure processing " + request.getMethod() + " request " + CloudUtil.secureLogMessage(request.getServletPath()), e);
		}
		response.sendError(e.getStatusCode(), e.getMessage());
	}

	/**
	 * Override the Spring default error response to allow additional fields
	 * to be included with custom exception types.
	 */
	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@Override
			public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
				Throwable error = getError(requestAttributes);
				if (error instanceof ServiceException) {
					ServiceException se = (ServiceException) error;
					errorAttributes.putAll(se.toSpringAttributeMap());
				}
				return errorAttributes;
			}
		};
	}
}
