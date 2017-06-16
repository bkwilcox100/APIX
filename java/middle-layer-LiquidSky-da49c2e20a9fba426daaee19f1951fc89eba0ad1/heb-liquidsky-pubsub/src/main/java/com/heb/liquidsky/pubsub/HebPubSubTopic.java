package com.heb.liquidsky.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.Topic;

public class HebPubSubTopic extends HebPubSubObject {

	private final String projectName;
	private final Pubsub pubsub;
	private final Topic topic;
	private final String topicName;

	public HebPubSubTopic(Pubsub pubsub, String projectName, String topicName) {
		this.topic = new Topic();
		this.pubsub = pubsub;
		this.projectName = projectName;
		this.topicName = topicName;
	}

	public HebPubSubTopic create() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Creating pubsub topic " + this.getFullyQualifiedName());
		}
		this.getPubsub().projects().topics().create(this.getFullyQualifiedName(), this.getTopic()).execute();
		return this;
	}

	@Override
	public boolean exists() throws IOException {
		boolean exists = false;
		try {
			this.getPubsub().projects().topics().get(this.getFullyQualifiedName()).execute();
			exists = true;
		} catch (GoogleJsonResponseException e) {
			// topic doesn't exist, this is not fatal
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(this.getFullyQualifiedName() + " does not exist or could not be retrieved");
			}
		}
		return exists;
	}

	@Override
	protected Object getField(String field) {
		return this.getTopic().get(field);
	}

	@Override
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	protected Pubsub getPubsub() {
		return this.pubsub;
	}

	@Override
	protected ResourceType getResourceType() {
		return ResourceType.TOPIC;
	}

	private Topic getTopic() {
		return this.topic;
	}

	@Override
	public String getName() {
		return this.topicName;
	}

	public void publish(HebPubSubMessage message) throws IOException {
		List<HebPubSubMessage> messages = new ArrayList<>();
		messages.add(message);
		this.publish(messages);
	}

	public void publish(List<HebPubSubMessage> messages) throws IOException {
		// now publish to the topic
		PublishRequest publishRequest = HebPubSubMessage.publishRequest(messages);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Publishing message to topic " + this.getFullyQualifiedName());
		}
		this.getPubsub().projects().topics().publish(this.getFullyQualifiedName(), publishRequest).execute();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Mesage successfully published to topic " + this.getFullyQualifiedName());
		}
	}
}
