package com.heb.liquidsky.taskqueue.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.pubsub.HEBPubSub;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;
import com.heb.liquidsky.taskqueue.HebTaskProcessor;

public class PubSubFutureTask implements HebFutureTask {

	private final PUBSUB_ACTION action;
	private final DataItem dataItem;
	private Future<Boolean> future = null;

	public PubSubFutureTask(DataItem dataItem, PUBSUB_ACTION action) {
		this.dataItem = dataItem;
		this.action = action;
	}

	@Override
	public void endTask() throws IOException {
		// nothing to do
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
		throw new UnsupportedOperationException("PubSubFutureTask does not support task merging");
	}

	@Override
	public void startTask() throws IOException {
		this.future = this.publishDeferred(this.dataItem, this.action);
	}

	private Future<Boolean> publishDeferred(final DataItem dataItem, final PUBSUB_ACTION action) {
		return HebTaskProcessor.getExecutorService().submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				HEBPubSub.getInstance().publish(dataItem, action);
				return Boolean.TRUE;
			}
		});
	}
}
