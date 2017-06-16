package com.heb.liquidsky.trace;

import com.google.api.services.cloudtrace.v1.model.TraceSpan;

public class HebFlexTraceContext implements HebTraceContext {

	private final TraceSpan traceSpan;

	public HebFlexTraceContext(TraceSpan traceSpan) {
		this.traceSpan = traceSpan;
	}

	public TraceSpan getTraceSpan() {
		return this.traceSpan;
	}
}
