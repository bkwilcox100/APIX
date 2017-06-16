package com.heb.liquidsky.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.AcknowledgeRequest;
import com.google.api.services.pubsub.model.PullRequest;
import com.google.api.services.pubsub.model.PullResponse;
import com.google.api.services.pubsub.model.PushConfig;
import com.google.api.services.pubsub.model.ReceivedMessage;
import com.google.api.services.pubsub.model.Subscription;

public class HebPubSubSubscription extends HebPubSubObject {

	private final String projectName;
	private final Pubsub pubsub;
	private final Subscription subscription;
	private final String subscriptionName;

	public HebPubSubSubscription(Pubsub pubsub, String projectName, String subscriptionName) {
		this.subscription = new Subscription();
		this.pubsub = pubsub;
		this.projectName = projectName;
		this.subscriptionName = subscriptionName;
	}

	/**
	 * Acknowledge that a pub/sub message has been received by a client.
	 * If this method is not invoked then the pub/sub engine will re-send
	 * the message until the client acknowledges receipt.
	 */
	public void acknowledge(List<ReceivedMessage> receivedMessages) throws IOException {
		List<String> ackIds = new ArrayList<>();
		if (receivedMessages != null) {
			for (ReceivedMessage receivedMessage : receivedMessages) {
				ackIds.add(receivedMessage.getAckId());
			}
			AcknowledgeRequest ackRequest = new AcknowledgeRequest();
			ackRequest.setAckIds(ackIds);
			this.getPubsub().projects().subscriptions().acknowledge(this.getFullyQualifiedName(), ackRequest).execute();
		}
	}

	public HebPubSubSubscription create(HebPubSubTopic topic, String pushEndpoint) throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Creating pubsub subscription " + this.getFullyQualifiedName() + " with push endpoint " + pushEndpoint);
		}
		// TODO - handle synchronization issues
		this.getSubscription().setTopic(topic.getFullyQualifiedName());
		if (pushEndpoint != null) {
			PushConfig pushConfig = new PushConfig().setPushEndpoint(pushEndpoint);
			this.getSubscription().setPushConfig(pushConfig);
		}
		this.getPubsub().projects().subscriptions().create(this.getFullyQualifiedName(), this.getSubscription()).execute();
		return this;
	}

	@Override
	public boolean exists() throws IOException {
		boolean exists = false;
		try {
			this.getPubsub().projects().subscriptions().get(this.getFullyQualifiedName()).execute();
			exists = true;
		} catch (GoogleJsonResponseException e) {
			// subscription doesn't exist, this is not fatal
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(this.getFullyQualifiedName() + " does not exist or could not be retrieved: " + e.getMessage());
			}
		}
		return exists;
	}

	@Override
	protected Object getField(String field) {
		return this.getSubscription().get(field);
	}

	@Override
	public String getName() {
		return this.subscriptionName;
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
		return ResourceType.SUBSCRIPTION;
	}

	private Subscription getSubscription() {
		return this.subscription;
	}

	/**
	 * Pull all messages for the specified subscription.
	 * 
	 * @param subscription The subscription to pull from.
	 * @param maxMessages The maximum number of update messages to pull.
	 * @param returnImmediately Whether to wait until a message is available
	 *  or return immediately.
	 */
	public List<ReceivedMessage> pull(int maxMessages, boolean returnImmediately) throws IOException {
		PullRequest pullRequest = new PullRequest().setReturnImmediately(returnImmediately).setMaxMessages(maxMessages);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Pulling subscription " + this.getFullyQualifiedName());
		}
		PullResponse pullResponse = this.getPubsub().projects().subscriptions().pull(this.getFullyQualifiedName(), pullRequest).execute();
		return pullResponse.getReceivedMessages();
	}
}
