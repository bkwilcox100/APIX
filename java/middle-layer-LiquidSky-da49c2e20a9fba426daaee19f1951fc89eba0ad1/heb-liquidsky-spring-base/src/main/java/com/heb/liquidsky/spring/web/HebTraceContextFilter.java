// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.heb.liquidsky.spring.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;
import com.heb.liquidsky.trace.Label;

/**
 * Utilities for generating span data related to HTTP servlets.
 *
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see Labels
 * @see Labels.Builder
 */
@WebFilter(filterName = "HebTraceContextFilter", urlPatterns = {"/*"})
public class HebTraceContextFilter implements Filter {

	private static final Logger logger = Logger.getLogger(HebTraceContextFilter.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(HebTraceContextFilter.class);
	/** Header key for requests already being traced - see https://cloud.google.com/trace/docs/faq */
	private static final String TRACE_HEADER_KEY = "X-Cloud-Trace-Context";
	private static final String HEALTH_CHECK_URI = "/_ah/health";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!this.generateTrace(request, response)) {
			chain.doFilter(request, response);
		} else {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Creating new trace for " + CloudUtil.secureLogMessage(httpRequest.getRequestURI()));
			}
			HebTraceContext traceContext = TRACER.startSpan(httpRequest.getRequestURI(), false);
			try {
				List<Label> labels = new ArrayList<>();
				this.addRequestLabels(httpRequest, labels);
				chain.doFilter(request, response);
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				this.addResponseLabels(httpResponse, labels);
				TRACER.annotateSpan(traceContext, labels.toArray(new Label[labels.size()]));
			} finally {
				TRACER.endSpan(traceContext);
			}
		}
	}

	private boolean generateTrace(ServletRequest request, ServletResponse response) {
		if (!(request instanceof HttpServletRequest)) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Not generating a trace because no HTTP request found");
			}
			return false;
		}
		if (!(response instanceof HttpServletResponse)) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Not generating a trace because no HTTP response found");
			}
			return false;
		}
		if (((HttpServletRequest) request).getRequestURI().equals(HEALTH_CHECK_URI)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Not generating a trace for health check URI " + CloudUtil.secureLogMessage(((HttpServletRequest) request).getRequestURI()));
			}
			return false;
		}
		if (((HttpServletRequest) request).getHeader(TRACE_HEADER_KEY) != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(CloudUtil.secureLogMessage("Found existing trace header " + ((HttpServletRequest) request).getHeader(TRACE_HEADER_KEY) + " for path " + ((HttpServletRequest) request).getRequestURI()));
			}
			// TODO - how to handle this scenario?
		}
		return true;
	}

	/**
	 * Adds span label annotations based on the given HTTP servlet request to the given labels
	 * builder.
	 *
	 * @param request       the http servlet request used to generate the span label annotations.
	 * @param labelsBuilder the labels builder to add span label annotations to.
	 */
	private void addRequestLabels(HttpServletRequest request, List<Label> labels) {
		labels.add(new Label("/http/method", request.getMethod()));
		labels.add(new Label("/http/url", request.getRequestURL().toString()));
		if (request.getContentLength() != -1) {
			labels.add(new Label("/http/request/size", Integer.toString(request.getContentLength())));
		}
		labels.add(new Label("/http/host", request.getServerName()));
		if (request.getHeader("user-agent") != null) {
			labels.add(new Label("/http/user_agent", request.getHeader("user-agent")));
		}
	}


	/**
	 * Generates a new span context based on the value of a span context header.
	 *
	 * @param header a string that is the value of a span context header.
	 * @return the new span context.
	 */
	/*
	public SpanContext fromHeader(String header) {
		int index = header.indexOf('/');
		if (index == -1) {
			TraceId traceId = parseTraceId(header);
			return new SpanContext(traceId, SpanId.invalid(), traceOptionsFactory.create());
		}
		TraceId traceId = parseTraceId(header.substring(0, index));
		if (!traceId.isValid()) {
			return new SpanContext(traceId, SpanId.invalid(), traceOptionsFactory.create());
		}
		String[] afterTraceId = header.substring(index + 1).split(";");
		SpanId spanId = parseSpanId(afterTraceId[0]);
		TraceOptions traceOptions = null;
		for (int i = 1; i < afterTraceId.length; i++) {
			if (afterTraceId[i].startsWith("o=")) {
				traceOptions = parseTraceOptions(afterTraceId[i].substring(2));
			}
		}
		// Invoke the factory here only after we have determined that there is no options argument in
		// the header, in order to avoid making an extra sampling decision.
		if (traceOptions == null) {
			traceOptions = traceOptionsFactory.create();
		}
		return new SpanContext(traceId, spanId, traceOptions);
	}
	*/

	/**
	 * Adds span label annotations based on the given HTTP servlet response to the given labels
	 * builder.
	 *
	 * @param response      the http servlet response used to generate the span label annotations.
	 * @param labelsBuilder the labels builder to add span label annotations to.
	 */
	private void addResponseLabels(HttpServletResponse response, List<Label> labels) {
		labels.add(new Label("/http/status_code", Integer.toString(response.getStatus())));
		if (response.getBufferSize() > 0) {
			labels.add(new Label("/http/response/size", Integer.toString(response.getBufferSize())));
		}
	}
}
