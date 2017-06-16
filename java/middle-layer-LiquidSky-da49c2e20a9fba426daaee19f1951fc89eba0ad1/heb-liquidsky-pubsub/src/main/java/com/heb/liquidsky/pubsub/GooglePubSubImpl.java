package com.heb.liquidsky.pubsub;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.common.base.Preconditions;
import com.heb.liquidsky.common.RetryHttpInitializerWrapper;

/**
 * Much of this code is based on samples at
 * https://github.com/GoogleCloudPlatform/cloud-pubsub-samples-java/tree/master/cmdline-pull/src/main/java/com/google/cloud/pubsub/client/demos/cli
 */
public abstract class GooglePubSubImpl {

	private static Pubsub PUBSUB;
	private static final Logger logger = Logger.getLogger(GooglePubSubImpl.class.getName());
	public static final String PUBSUB_TOKEN_PARAM = "token";

	protected GoogleCredential generateCredential() throws IOException {
		GoogleCredential credential = GoogleCredential.getApplicationDefault(this.getHttpTransport(), this.getJsonFactory());
		if (credential.createScopedRequired()) {
			credential = credential.createScoped(PubsubScopes.all());
		}
		return credential;
	}

	public abstract String getApplicationId();

	protected HttpTransport getHttpTransport() {
		HttpTransport httpTransport = Utils.getDefaultTransport();
		Preconditions.checkNotNull(httpTransport);
		return httpTransport;
	}

	protected JsonFactory getJsonFactory() {
		JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
		Preconditions.checkNotNull(jsonFactory);
		return jsonFactory;
	}

	public abstract String getProjectId();

	protected Pubsub getPubsub() throws IOException {
		if (PUBSUB == null) {
			GoogleCredential credential = this.generateCredential();
			HttpRequestInitializer initializer = new RetryHttpInitializerWrapper(credential);
			PUBSUB = new Pubsub.Builder(this.getHttpTransport(), this.getJsonFactory(), initializer).setApplicationName(this.getApplicationId()).build();
		}
		return PUBSUB;
	}

	/**
	 * Endpoint configured to process push notifications, or <code>null</code>
	 * if the application only allows pull functionality.
	 */
	protected abstract String getPushEndpoint();

	protected String getSubscriptionName(String topicName) {
		// TODO - ensure the values are valid subscription name syntax
		return this.getApplicationId().toLowerCase() + "__" + topicName.toLowerCase();
	}

	public HebPubSubTopic getTopic(String topicName) throws IOException {
		return new HebPubSubTopic(this.getPubsub(), this.getProjectId(), topicName);
	}

	protected String getTopicName(String dataTypeName) {
		// TODO - ensure the values are valid subscription name syntax
		return dataTypeName.toLowerCase();
	}

	protected void publish(HebPubSubMessage message) throws IOException {
		String topicName = message.getData().getDataType();
		HebPubSubTopic topic = new HebPubSubTopic(this.getPubsub(), this.getProjectId(), topicName);
		if (!topic.exists()) {
			topic.create();
		}
		topic.publish(message);
	}

	/**
	 * Create a subscription to retrieve notifications for updates
	 * to the specified data item.
	 * 
	 * @param dataTypeName The type of item (product, sku, etc).
	 */
	public HebPubSubSubscription subscribe(String dataTypeName, boolean pushNotifications) throws IOException {
		String pushEndpoint = (pushNotifications) ? this.getPushEndpoint() : null;
		String subscriptionName = this.getSubscriptionName(dataTypeName);
		HebPubSubSubscription subscription = new HebPubSubSubscription(this.getPubsub(), this.getProjectId(), subscriptionName);
		if (!subscription.exists()) {
			String topicName = this.getTopicName(dataTypeName);
			// create a new subscription
			HebPubSubTopic topic = this.getTopic(topicName);
			if (!topic.exists()) {
				topic.create();
			}
			subscription.create(topic, pushEndpoint);
		}
		return subscription;
	}

	/**
	 * Delete the specified subscription, meaning it is erased from
	 * the server and no further updates will be provided.
	 */
	public void unsubscribe(HebPubSubSubscription subscription) throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Deleting pubsub subscription " + subscription.getFullyQualifiedName());
		}
		this.getPubsub().projects().subscriptions().delete(subscription.getFullyQualifiedName()).execute();
	}
}
