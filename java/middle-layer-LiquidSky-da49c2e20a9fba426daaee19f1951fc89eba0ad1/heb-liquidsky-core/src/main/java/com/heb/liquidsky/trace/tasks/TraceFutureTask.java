package com.heb.liquidsky.trace.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.HttpResponse;
import com.google.api.services.cloudtrace.v1.model.Traces;
import com.heb.liquidsky.taskqueue.HebTaskProcessor;
import com.heb.liquidsky.taskqueue.tasks.HebFutureTask;
import com.heb.liquidsky.trace.HebFlexTracer;

public class TraceFutureTask implements HebFutureTask {

	private static final Logger logger = Logger.getLogger(TraceFutureTask.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(TraceFutureTask.class);
	private static final int MAX_TRACES_PER_REQUEST = 20;

	private Future<HttpResponse> future = null;
	private final Traces traces;

	public TraceFutureTask(Traces traces) {
		this.traces = traces;
	}

	@Override
	public void endTask() throws IOException {
		try {
			HttpResponse response = this.future.get();
			if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Failure sending HTTP request: " + response.getStatusCode());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Received HTTP response: " + response.parseAsString());
			}
		} catch (IOException | ExecutionException | InterruptedException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while retrieving HTTP response", e);
			}
		}
	}

	@Override
	public boolean isFinished() throws IOException {
		return (this.future != null && this.future.isDone());
	}

	@Override
	public MERGE_STATUS isMergeAllowed() {
		return (this.future == null && traces.getTraces().size() < MAX_TRACES_PER_REQUEST) ? MERGE_STATUS.MERGE_ALLOWED : MERGE_STATUS.MERGE_REQUIRES_NEW_TASK;
	}

	@Override
	public void merge(HebFutureTask task) {
		if (this.future != null) {
			throw new IllegalStateException("Cannot merge tasks that have already started");
		}
		if (traces.getTraces().size() >= MAX_TRACES_PER_REQUEST) {
			throw new IllegalStateException("Cannot merge traces when size exceeds max allowed: " + MAX_TRACES_PER_REQUEST);
		}
		this.traces.getTraces().addAll(((TraceFutureTask) task).traces.getTraces());
	}

	@Override
	public void startTask() throws IOException {
		this.future = this.sendTracesDeferred(this.traces);
	}

	private Future<HttpResponse> sendTracesDeferred(final Traces traces) {
		return HebTaskProcessor.getExecutorService().submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return TRACER.sendTraces(traces);
			}
		});
	}
}
