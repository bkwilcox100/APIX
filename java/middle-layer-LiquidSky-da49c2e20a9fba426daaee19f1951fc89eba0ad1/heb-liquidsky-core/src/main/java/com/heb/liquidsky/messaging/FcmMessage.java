package com.heb.liquidsky.messaging;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.messaging.gson.MessageMarshaller;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This class represents a Firebase Cloud Messaging message, and
 * is converted to JSON to be sent as the actual message content
 * when a message is sent.
 */
public final class FcmMessage implements Serializable {

	private static final long serialVersionUID = 1l;
	private static final Gson GSON = new GsonBuilder().create();
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(FcmMessage.class);

	private MessageMarshaller fcmMarshaller;

	private FcmMessage() {
		// must use the initialization methods to create an instance
		this.setTimestamp(new Date());
	}

	/**
	 * @param topic A topic name of the form "/topics/liquidsky_sku__all"
	 */
	public static FcmMessage initializeForTopic(String topic) {
		FcmMessage fcmMessage = new FcmMessage();
		fcmMessage.getFcmMarshaller().setRecipient(topic);
		return fcmMessage;
	}

	/**
	 * @param token A device token representing a device to receive the
	 *  message.
	 */
	public static FcmMessage initializeForToken(String token) {
		FcmMessage fcmMessage = new FcmMessage();
		fcmMessage.getFcmMarshaller().setRecipient(token);
		return fcmMessage;
	}

	public void setDataItem(DataItem dataItem) {
		this.getFcmMarshaller().getData().setDataItem(dataItem);
	}

	public void setDataItemAction(PUBSUB_ACTION action) {
		this.getFcmMarshaller().getData().setAction(action);
	}

	public void setTimestamp(Date timestamp) {
		this.getFcmMarshaller().getData().setTimestamp(timestamp);
	}

	private MessageMarshaller getFcmMarshaller() {
		if (this.fcmMarshaller == null) {
			this.fcmMarshaller = new MessageMarshaller();
		}
		return this.fcmMarshaller;
	}

	public void setNotificationBody(String notificationBody) {
		this.getFcmMarshaller().getNotification().setBody(notificationBody);
	}

	public void setNotificationTitle(String notificationTitle) {
		this.getFcmMarshaller().getNotification().setTitle(notificationTitle);
	}

	public String toJson() {
		HebTraceContext context = TRACER.startSpan("toJson");
		try {
			return GSON.toJson(this.getFcmMarshaller());
		} finally {
			TRACER.endSpan(context);
		}
	}
}
