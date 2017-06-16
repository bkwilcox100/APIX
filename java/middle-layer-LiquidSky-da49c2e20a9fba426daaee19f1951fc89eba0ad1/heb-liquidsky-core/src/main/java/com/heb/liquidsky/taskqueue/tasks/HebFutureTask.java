package com.heb.liquidsky.taskqueue.tasks;

import java.io.IOException;

public interface HebFutureTask {

	public enum MERGE_STATUS { MERGE_ALLOWED, MERGE_NOT_ALLOWED, MERGE_REQUIRES_NEW_TASK };

	/**
	 * Once the task has completed, perform any additional required
	 * work to process its result.
	 */
	public void endTask() throws IOException;

	/**
	 * Return a boolean indicating whether or not the specified task
	 * has completed.
	 */
	public boolean isFinished() throws IOException;

	/**
	 * Return a value indicating whether this task type allows tasks
	 * to be merged with existing tasks, and if so whether the current
	 * task is full or can accept more.
	 * 
	 * @return MERGE_ALLOWED if a merge into the current task is allowed,
	 *  MERGE_NOT_ALLOWED if a merge into the current task is not allowed,
	 *  or MERGE_REQUIRES_NEW_TASK if a merge is allowed for this task
	 *  type, but the current task cannot accept any new items.
	 */
	public MERGE_STATUS isMergeAllowed();

	public void merge(HebFutureTask task);

	/**
	 * Perform any work required to start the task.  This method
	 * should start the task and return immediately so that there
	 * is no overhead for the task processor as it starts tasks.
	 */
	public void startTask() throws IOException;
}
