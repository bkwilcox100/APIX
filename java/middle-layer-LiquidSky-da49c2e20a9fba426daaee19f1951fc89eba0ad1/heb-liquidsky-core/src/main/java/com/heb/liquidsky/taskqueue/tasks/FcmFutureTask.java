package com.heb.liquidsky.taskqueue.tasks;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.HttpResponse;
import com.heb.liquidsky.messaging.CloudMessaging;
import com.heb.liquidsky.messaging.FcmMessage;

public class FcmFutureTask implements HebFutureTask {

	private static final Logger logger = Logger.getLogger(FcmFutureTask.class.getName());

	private Future<HttpResponse> future;
	private final FcmMessage fcmMessage;

	public FcmFutureTask(FcmMessage fcmMessage) {
		this.fcmMessage = fcmMessage;
	}

	@Override
	public void endTask() throws IOException {
		try {
			CloudMessaging.getInstance().processResponse(this.future.get());
		} catch (IOException | ExecutionException | InterruptedException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure while releasing HTTP connection", e);
			}
		}
	}

	@Override
	public boolean isFinished() throws IOException {
		return (this.future != null && this.future.isDone());
	}

	@Override
	public MERGE_STATUS isMergeAllowed() {
		return MERGE_STATUS.MERGE_NOT_ALLOWED;
	}

	@Override
	public void merge(HebFutureTask task) {
		throw new UnsupportedOperationException("FcmFutureTask does not support task merging");
	}

	@Override
	public void startTask() throws IOException {
		this.future = CloudMessaging.getInstance().publishAsync(this.fcmMessage);
	}
}
