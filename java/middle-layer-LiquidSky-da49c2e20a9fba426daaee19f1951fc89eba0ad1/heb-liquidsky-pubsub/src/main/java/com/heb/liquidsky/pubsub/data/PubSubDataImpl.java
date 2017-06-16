package com.heb.liquidsky.pubsub.data;

import java.util.Date;

import com.heb.liquidsky.pubsub.GooglePubSubImpl;

/**
 * This is a minimal implementation providing basic data for all messages
 * shared via pub/sub.
 */
public class PubSubDataImpl implements PubSubData {

	private static final long serialVersionUID = 1l;

	private final PUBSUB_ACTION action;
	private final String dataType;
	private final String id;
	private final String sourceApplication;
	private final String sourceProject;
	private final Date timestamp;

	public PubSubDataImpl(GooglePubSubImpl pubsub, String dataType, String id, PUBSUB_ACTION action) {
		this.sourceApplication = pubsub.getApplicationId();
		this.sourceProject = pubsub.getProjectId();
		this.timestamp = new Date();
		this.dataType = dataType;
		this.id = id;
		this.action = action;
	}

	public PubSubDataImpl(PubSubData data) {
		this.dataType = data.getDataType();
		this.id = data.getId();
		this.action = data.getAction();
		this.sourceApplication = data.getSourceApplication();
		this.sourceProject = data.getSourceProject();
		this.timestamp = data.getTimestamp();
	}

	@Override
	public PUBSUB_ACTION getAction() {
		return this.action;
	}

	@Override
	public String getDataType() {
		return this.dataType;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getSourceApplication() {
		return this.sourceApplication;
	}

	@Override
	public String getSourceProject() {
		return this.sourceProject;
	}

	@Override
	public Date getTimestamp() {
		return this.timestamp;
	}
}
