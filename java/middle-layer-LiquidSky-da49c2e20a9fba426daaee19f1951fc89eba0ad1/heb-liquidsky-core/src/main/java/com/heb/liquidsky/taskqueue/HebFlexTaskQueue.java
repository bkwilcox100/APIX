package com.heb.liquidsky.taskqueue;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.cloudtrace.v1.model.Traces;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.messaging.FcmMessage;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;
import com.heb.liquidsky.taskqueue.tasks.FcmFutureTask;
import com.heb.liquidsky.taskqueue.tasks.HebFutureTask;
import com.heb.liquidsky.taskqueue.tasks.PubSubFutureTask;
import com.heb.liquidsky.trace.tasks.TraceFutureTask;

public final class HebFlexTaskQueue {

	private static final Logger logger = Logger.getLogger(HebFlexTaskQueue.class.getName());
	public static final String FCM_QUEUE_NAME = "fcm-queue";

	private static HebTaskProcessor TASK_PROCESSOR;
	private static Thread TASK_PROCESSOR_THREAD;
	private static final HebFlexTaskQueue INSTANCE = new HebFlexTaskQueue();

	private HebFlexTaskQueue() {
		// private constructor to enforce singleton pattern
	}

	public static HebFlexTaskQueue getInstance() {
		return INSTANCE;
	}

	private void addTask(HebFutureTask task) {
		this.getTaskProcessor().addTask(task);
	}

	public void addPubSubTask(DataItem dataItem, PUBSUB_ACTION action) {
		PubSubFutureTask task = new PubSubFutureTask(dataItem, action);
		this.addTask(task);
	}

	public void addFcmTask(FcmMessage fcmMessage) {
		FcmFutureTask task = new FcmFutureTask(fcmMessage);
		this.addTask(task);
	}

	public void addTraceTask(Traces traces) {
		TraceFutureTask task = new TraceFutureTask(traces);
		this.addTask(task);
	}

	private synchronized HebTaskProcessor getTaskProcessor() {
		if (TASK_PROCESSOR == null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting new Task processor instance");
			}
			TASK_PROCESSOR = new HebTaskProcessor(1000);
		}
		if (TASK_PROCESSOR_THREAD == null || !TASK_PROCESSOR_THREAD.isAlive()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting new Task processor thread");
			}
			TASK_PROCESSOR_THREAD = new Thread(TASK_PROCESSOR);
			TASK_PROCESSOR_THREAD.start();
		}
		return TASK_PROCESSOR;
	}
}
