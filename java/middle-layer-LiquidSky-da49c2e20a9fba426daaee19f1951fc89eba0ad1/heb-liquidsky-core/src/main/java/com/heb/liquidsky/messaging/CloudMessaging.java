package com.heb.liquidsky.messaging;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;
import com.heb.liquidsky.taskqueue.HebFlexTaskQueue;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

public final class CloudMessaging {

	private static final Logger logger = Logger.getLogger(CloudMessaging.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(CloudMessaging.class);
	private static final String FCM_ENDPOINT_SEND = "https://fcm.googleapis.com/fcm/send";
	private static final String FCM_ENDPOINT_SUBSCRIBE = "https://iid.googleapis.com/iid/v1/";
	private static final String FCM_KEY = CloudUtil.getProperty("FIREBASE_KEY");
	private static final CloudMessaging INSTANCE = new CloudMessaging();
	private static HttpRequestFactory REQUEST_FACTORY;

	private CloudMessaging() {
		// only allow access to this class via the singleton instance
	}

	public static CloudMessaging getInstance() {
		return INSTANCE;
	}

	protected static String getFcmKey() {
		return FCM_KEY;
	}

	private FcmMessage generateFcmMessage(DataItem dataItem, PUBSUB_ACTION action, boolean notify) {
		String topic = "/topics/" + this.generateTopicName(dataItem, notify);
		FcmMessage fcmMessage = FcmMessage.initializeForTopic(topic);
		if (notify) {
			// TODO - hard-coded test data
			fcmMessage.setNotificationTitle("INSERT_DATA_TYPE_HERE has changed");
			fcmMessage.setNotificationBody("Your INSERT_DATA_TYPE_HERE has been modified on another device.  Please refresh the page to see the latest data.");
		}
		fcmMessage.setDataItem(dataItem);
		fcmMessage.setDataItemAction(action);
		return fcmMessage;
	}

	private String generateTopicName(DataItem dataItem, boolean notify) {
		if (dataItem == null) {
			throw new IllegalArgumentException("Cannot build topic name for null data item");
		}
		String topicName = dataItem.getDataType().getName() + "__" + dataItem.getId();
		if (notify) {
			topicName += "__notify";
		}
		return topicName;
	}

	private Future<HttpResponse> manageSubscription(DataItem dataItem, String deviceToken, boolean notify, String method) throws IOException {
		String topicName = this.generateTopicName(dataItem, notify);
		String endpoint = this.subscriptionUrl(topicName, deviceToken);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Start: Subscription endpoint: " + endpoint);
		}
		GenericUrl url = new GenericUrl(endpoint);
		HttpRequest request = this.prepareRequest(url, method, null);
		return request.executeAsync();
	}

	private HttpRequestFactory createRequestFactory(HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
				request.getHeaders().setAuthorization("key=" + getFcmKey());
				request.getHeaders().setContentType("application/json");
			}
		});
	}

	private HttpRequestFactory getRequestFactory() {
		if (REQUEST_FACTORY == null) {
			REQUEST_FACTORY = this.createRequestFactory(new NetHttpTransport.Builder().build());
		}
		return REQUEST_FACTORY;
	}

	private HttpRequest prepareRequest(GenericUrl url, String method, String json) throws IOException {
		HttpContent content = (json != null) ? new ByteArrayContent("application/json", json.getBytes()) : null;
		HttpRequest request = this.getRequestFactory().buildRequest(method, url, content);
		if (json != null && logger.isLoggable(Level.FINE)) {
			logger.fine("Sending message: " + json);
		}
		return request;
	}

	public void processResponse(HttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("Failure sending HTTP request: " + response.getStatusCode());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Received HTTP response: " + response.parseAsString());
		}
	}

	private boolean publishAllowed(DataItem dataItem) {
		if (!dataItem.getDataType().isFcmEnabled()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("FCM messaging is disabled for data type " + dataItem.getDataType().getName());
			}
			return false;
		}
		return true;
	}

	// https://firebase.google.com/docs/cloud-messaging/send-message
	public Future<HttpResponse> publishAsync(FcmMessage fcmMessage) throws IOException {
		HebTraceContext context = TRACER.startSpan("publishAsync");
		GenericUrl url = new GenericUrl(FCM_ENDPOINT_SEND);
		try {
			HttpRequest request = this.prepareRequest(url, HttpMethods.POST, fcmMessage.toJson());
			return request.executeAsync();
		} finally {
			TRACER.endSpan(context);
		}
	}

	public void publishDeferred(FcmMessage fcmMessage) {
		HebFlexTaskQueue.getInstance().addFcmTask(fcmMessage);
	}

	public void publishDeferred(DataItem dataItem, PUBSUB_ACTION action) {
		if (!this.publishAllowed(dataItem)) {
			return;
		}
		FcmMessage fcmMessageWithNotification = this.generateFcmMessage(dataItem, action, true);
		this.publishDeferred(fcmMessageWithNotification);
		FcmMessage fcmMessageWithoutNotification = this.generateFcmMessage(dataItem, action, false);
		this.publishDeferred(fcmMessageWithoutNotification);
	}

	public Future<HttpResponse> subscribe(DataItem dataItem, String deviceToken, boolean notify) throws IOException {
		return this.manageSubscription(dataItem, deviceToken, notify, HttpMethods.POST);
	}

	private String subscriptionUrl(String topicName, String deviceToken) throws IOException  {
		return FCM_ENDPOINT_SUBSCRIBE + URLEncoder.encode(deviceToken, "UTF-8") + "/rel/topics/" + URLEncoder.encode(topicName, "UTF-8");
	}

	public Future<HttpResponse> unsubscribe(DataItem dataItem, String deviceToken, boolean notify) throws IOException {
		return this.manageSubscription(dataItem, deviceToken, notify, HttpMethods.DELETE);
	}
}
