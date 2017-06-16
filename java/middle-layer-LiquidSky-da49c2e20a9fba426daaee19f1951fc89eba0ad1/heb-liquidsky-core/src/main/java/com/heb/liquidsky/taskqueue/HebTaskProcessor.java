package com.heb.liquidsky.taskqueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heb.liquidsky.taskqueue.tasks.HebFutureTask;
import com.heb.liquidsky.taskqueue.tasks.HebFutureTask.MERGE_STATUS;

public class HebTaskProcessor implements Runnable {

	private static final Logger logger = Logger.getLogger(HebTaskProcessor.class.getName());
	private static final long MAX_IDLE_TIME_MS = 5 * 60 * 1000; // 5 minutes
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

	/** Sleep time between getting new tasks and testing to see if old requests have finished */
	private final int processingIntervalMs;

	private final List<HebFutureTask> activeTasks = new ArrayList<>();
	private final Map<String, List<HebFutureTask>> queuedTaskMap = new HashMap<>();
	private final Object TASK_LOCK = new Object();

	public HebTaskProcessor(int processingIntervalMs) {
		this.processingIntervalMs = processingIntervalMs;
	}

	public static ExecutorService getExecutorService() {
		return EXECUTOR_SERVICE;
	}

	public void addTask(HebFutureTask task) {
		String taskType = task.getClass().getName();
		synchronized (TASK_LOCK) {
			// tasks are organized by type in the map for quick access
			List<HebFutureTask> currentTasks = this.queuedTaskMap.get(taskType);
			if (currentTasks == null) {
				currentTasks = new ArrayList<HebFutureTask>();
				this.queuedTaskMap.put(taskType, currentTasks);
			}
			// some tasks can be merged to promote batch processing - for
			// example, multiple traces can be sent to stackdriver in the
			// same message, rather than one at a time
			boolean mergePerformed = false;
			if (!currentTasks.isEmpty() && task.isMergeAllowed() != MERGE_STATUS.MERGE_NOT_ALLOWED) {
				for (HebFutureTask currentTask : currentTasks) {
					if (currentTask.isMergeAllowed() == MERGE_STATUS.MERGE_ALLOWED) {
						currentTask.merge(task);
						mergePerformed = true;
						break;
					}
				}
			}
			// if a merge was not possible, add the single task to the queue
			if (!mergePerformed) {
				currentTasks.add(task);
			}
		}
	}

	private int getProcessingIntervalMs() {
		return this.processingIntervalMs;
	}

	private void finishTasks() {
		if (this.activeTasks.isEmpty()) {
			return;
		}
		Iterator<HebFutureTask> iterator = this.activeTasks.iterator();
		int unfinishedTaskCount = 0;
		int finishedTaskCount = 0;
		while (iterator.hasNext()) {
			HebFutureTask activeTask = iterator.next();
			try {
				if (!activeTask.isFinished()) {
					unfinishedTaskCount++;
					continue;
				}
				activeTask.endTask();
			} catch (IOException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Failure while finishing task " + activeTask, e);
				}
				continue;
			}
			iterator.remove();
			finishedTaskCount++;
		}
		Level level = (finishedTaskCount > 0 || unfinishedTaskCount > 0) ? Level.INFO : Level.FINE;
		if (logger.isLoggable(level)) {
			logger.log(level, finishedTaskCount + " tasks have finished, " + unfinishedTaskCount + " tasks remain");
		}
	}

	private boolean isIdle() {
		return (this.activeTasks.isEmpty() && this.queuedTaskMap.isEmpty());
	}

	private void startTasks() {
		if (this.queuedTaskMap.isEmpty()) {
			return;
		}
		for (Map.Entry<String, List<HebFutureTask>> queuedTaskMapEntry : this.queuedTaskMap.entrySet()) {
			int startedTaskCount = 0;
			Iterator<HebFutureTask> iterator = queuedTaskMapEntry.getValue().iterator();
			while (iterator.hasNext()) {
				HebFutureTask queuedTask = iterator.next();
				try {
					queuedTask.startTask();
				} catch (IOException e) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.log(Level.SEVERE, "Failure while starting task " + queuedTask, e);
					}
					continue;
				}
				this.activeTasks.add(queuedTask);
				iterator.remove();
				startedTaskCount++;
			}
			Level level = (startedTaskCount > 0) ? Level.INFO : Level.FINE;
			if (logger.isLoggable(level)) {
				logger.log(level, startedTaskCount + " tasks of type " + queuedTaskMapEntry.getKey() + " have been started");
			}
		}
	}

	private long executionTime(long start) {
		return System.currentTimeMillis() - start;
	}

	public void run() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Start: processing task queues");
		}
		long start = System.currentTimeMillis();
		long idleTime = 0;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				synchronized (TASK_LOCK) {
					this.startTasks();
					this.finishTasks();
				}
				if (!this.isIdle()) {
					idleTime = 0;
				} else {
					idleTime += this.getProcessingIntervalMs();
				}
				if (idleTime > MAX_IDLE_TIME_MS) {
					// if the thread has been idle for a while then kill it so that this
					// app engine instance can be allowed to die
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Task queue thread has been idle for " + idleTime + " ms and will be terminated");
					}
					Thread.currentThread().interrupt();
				} else {
					Thread.sleep(this.getProcessingIntervalMs());
				}
			}
		} catch (InterruptedException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Task queue processing thread interrupted after " + this.executionTime(start) + " milliseconds");
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("End: processing task queues");
		}
	}
}
