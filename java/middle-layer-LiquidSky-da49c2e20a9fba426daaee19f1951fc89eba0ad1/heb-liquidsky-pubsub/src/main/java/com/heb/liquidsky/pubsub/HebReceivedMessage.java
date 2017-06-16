package com.heb.liquidsky.pubsub;

import com.google.api.services.pubsub.model.PubsubMessage;

/**
 * This class exists solely to allow deserializing JSON pubsub
 * messages.
 */
public class HebReceivedMessage {

	private PubsubMessage message;
	private String subscription;

	public PubsubMessage getMessage() {
		return this.message;
	}

	public void setMessage(PubsubMessage message) {
		this.message = message;
	}

	public String getSubscription() {
		return this.subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
}
