package com.heb.liquidsky.trace;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudtrace.v1.CloudTrace;
import com.google.api.services.cloudtrace.v1.model.Trace;
import com.google.api.services.cloudtrace.v1.model.TraceSpan;
import com.google.api.services.cloudtrace.v1.model.Traces;
import com.heb.liquidsky.common.ConfigurationConstants;
import com.heb.liquidsky.common.HebEnvironmentProperties;
import com.heb.liquidsky.taskqueue.HebFlexTaskQueue;

public final class HebFlexTracer {

	private static final Logger logger = Logger.getLogger(HebFlexTracer.class.getName());
	private static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static {
		JSON_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	/** Minimum time to wait (in ms) between sending traces */
	private static final int TRACE_RATE_LIMIT_MS = 500;
	private static final Object THREAD_LOCAL_LOCK = new Object();
	private static final ThreadLocal<Trace> environmentThreadLocal = new ThreadLocal<>();
	private static final RandomSpanIdFactory SPAN_ID_FACTORY = new RandomSpanIdFactory();
	private static final RandomTraceIdFactory TRACE_ID_FACTORY = new RandomTraceIdFactory();
	private static CloudTrace TRACE_SERVICE;
	private static long LAST_TRACE_SEND_TIME = 0l;

	private final Class<?> clazz;

	private HebFlexTracer(Class<?> clazz) {
		this.clazz = checkNotNull(clazz);
	}

	public static HebFlexTracer getTracer(Class<?> clazz) {
		return new HebFlexTracer(clazz);
	}

	/**
	 * Add a single key-value pair to provide additional information in the
	 * trace, such as the name of a query that is being traced.
	 */
	public void annotateSpan(HebTraceContext traceContext, String key, String value) {
		this.annotateSpan(traceContext, new Label(key, value));
	}

	/**
	 * Add key-value pairs to provide additional information in the
	 * trace, such as the name of a query that is being traced.
	 */
	public void annotateSpan(HebTraceContext traceContext, Label... labels) {
		if (!this.isTracingEnabled()) {
			return;
		}
		checkNotNull(traceContext);
		checkNotNull(labels);
		Trace trace = this.getCurrentTrace();
		if (trace == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("annotateSpan() invoked with null trace");
			}
			return;
		}
		int currentIndex = this.getSpanIndex(trace, traceContext);
		TraceSpan currentSpan = trace.getSpans().get(currentIndex);
		if (currentSpan.getLabels() == null) {
			currentSpan.setLabels(new HashMap<String, String>());
		}
		for (Label label : labels) {
			currentSpan.getLabels().put(label.getKey(), label.getValue());
		}
	}

	public HebTraceContext startSpan(String name) {
		return this.startSpan(name, true);
	}

	public HebTraceContext startSpan(String name, boolean prependClazz) {
		if (!this.isTracingEnabled()) {
			return null;
		}
		Trace trace = this.getCurrentTrace();
		if (trace == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("startSpan() invoked with null trace");
			}
			return null;
		}
		checkNotNull(name);
		if (prependClazz) {
			name = this.clazz.getSimpleName() + "/" + name;
		}
		TraceSpan traceSpan = new TraceSpan();
		traceSpan.setSpanId(SPAN_ID_FACTORY.nextId());
		traceSpan.setStartTime(this.currentTimeAsJsonDate());
		traceSpan.setName(name);
		if (trace.getSpans() == null) {
			trace.setSpans(new ArrayList<TraceSpan>());
		}
		trace.getSpans().add(traceSpan);
		return new HebFlexTraceContext(traceSpan);
	}

	public void endSpan(HebTraceContext traceContext) {
		if (!this.isTracingEnabled()) {
			return;
		}
		Trace trace = this.getCurrentTrace();
		if (trace == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("endSpan() invoked with null trace");
			}
			return;
		}
		int currentIndex = this.getSpanIndex(trace, traceContext);
		trace.getSpans().get(currentIndex).setEndTime(this.currentTimeAsJsonDate());
		if (currentIndex == 0) {
			// last span in the trace, send it to stack driver
			this.sendTraceAsync(trace);
		}
	}

	private int getSpanIndex(Trace trace, HebTraceContext traceContext) {
		checkNotNull(traceContext);
		HebFlexTraceContext flexTraceContext = (HebFlexTraceContext) traceContext;
		checkNotNull(flexTraceContext.getTraceSpan());
		BigInteger currentSpanId = flexTraceContext.getTraceSpan().getSpanId();
		checkNotNull(currentSpanId);
		int currentIndex = -1;
		if (trace.getSpans() != null) {
			for (int i = 0; i < trace.getSpans().size(); i++) {
				TraceSpan traceSpan = trace.getSpans().get(i);
				if (traceSpan.getSpanId().equals(currentSpanId)) {
					currentIndex = i;
					break;
				}
			}
		}
		if (currentIndex == -1) {
			throw new IllegalArgumentException("Mis-matched span context: span not found in the current trace: " + currentSpanId);
		}
		return currentIndex;
	}

	private void sendTraceAsync(Trace trace) {
		long currentTimeMillis = System.currentTimeMillis();
		if ((currentTimeMillis - LAST_TRACE_SEND_TIME) < TRACE_RATE_LIMIT_MS) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Trace not recorded due to rate limiting " + trace);
			}
			return;
		}
		LAST_TRACE_SEND_TIME = currentTimeMillis;
		Traces traces = new Traces();
		traces.setTraces(new ArrayList<Trace>());
		traces.getTraces().add(trace);
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Sending trace: " + trace);
		}
		HebFlexTaskQueue.getInstance().addTraceTask(traces);
		synchronized (THREAD_LOCAL_LOCK) {
			environmentThreadLocal.set(null);
		}
	}

	public HttpResponse sendTraces(Traces traces) throws IOException, GeneralSecurityException {
		String projectId = HebEnvironmentProperties.getInstance().getAppEngineId();
		CloudTrace.Projects.PatchTraces request = this.getTraceService().projects().patchTraces(projectId, traces);
		return request.executeUnparsed();
	}

	private String currentTimeAsJsonDate() {
		return JSON_DATE_FORMAT.format(new Date());
	}

	// from https://cloud.google.com/trace/docs/reference/v1/rest/v1/projects/patchTraces
	private CloudTrace getTraceService() throws IOException, GeneralSecurityException {
		if (TRACE_SERVICE == null) {
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			GoogleCredential credential = GoogleCredential.getApplicationDefault();
			if (credential.createScopedRequired()) {
				credential = credential.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
			}
			TRACE_SERVICE = new CloudTrace.Builder(httpTransport, jsonFactory, credential).setApplicationName("Google-CloudTraceSample/0.1").build();
		}
		return TRACE_SERVICE;
	}

	private Trace getCurrentTrace() {
		synchronized (THREAD_LOCAL_LOCK) {
			Trace threadLocalTrace = environmentThreadLocal.get();
			if (threadLocalTrace == null) {
				String projectId = HebEnvironmentProperties.getInstance().getAppEngineId();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Initializing trace for app: " + projectId);
				}
				threadLocalTrace = new Trace();
				threadLocalTrace.setTraceId(TRACE_ID_FACTORY.nextId());
				threadLocalTrace.setProjectId(projectId);
				environmentThreadLocal.set(threadLocalTrace);
			}
			return threadLocalTrace;
		}
	}

	private boolean isTracingEnabled() {
		return ConfigurationConstants.ENABLE_TRACING;
	}
}
