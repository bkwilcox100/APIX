package com.heb.liquidsky.pubsub;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.google.api.services.pubsub.Pubsub;

public abstract class HebPubSubObject {

	protected static final Logger logger = Logger.getLogger(HebPubSubObject.class.getName());

	protected static final String FIELD_CREATION_DATE = "CREATION_DATE";
	protected static final String FIELD_LIFESPAN = "LIFESPAN";

	protected enum ResourceType {
		TOPIC("topics"), SUBSCRIPTION("subscriptions");

		private String collectionName;

		private ResourceType(final String collectionName) {
			this.collectionName = collectionName;
		}

		public String getCollectionName() {
			return this.collectionName;
		}
	}

	public abstract boolean exists() throws IOException;

	public String getFullyQualifiedName() {
		return String.format("projects/%s/%s/%s", this.getProjectName(), this.getResourceType().getCollectionName(), this.getName());
	}

	protected abstract Object getField(String field);

	public Date getCreationDate() {
		return new Date((long) this.getField(FIELD_CREATION_DATE));
	}

	public abstract String getName();

	public abstract String getProjectName();

	protected abstract Pubsub getPubsub();

	protected abstract ResourceType getResourceType();
}
