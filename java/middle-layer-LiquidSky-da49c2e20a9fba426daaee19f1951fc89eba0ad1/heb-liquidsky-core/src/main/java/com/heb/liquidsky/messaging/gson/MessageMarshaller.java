package com.heb.liquidsky.messaging.gson;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * See also https://firebase.google.com/docs/cloud-messaging/concept-options
 */
public class MessageMarshaller implements Serializable {

	// if a message isn't delivered within 30 minutes then forget about it
	private static final int DEFAULT_TIME_TO_LIVE = 1800;
	private enum PRIORITY { NORMAL, HIGH };
	private static final long serialVersionUID = 1l;

	@SerializedName("collapse_key")
	private String collapseKey;
	@SerializedName("condition")
	private String condition;
	@SerializedName("data")
	private DataMarshaller data;
	@SerializedName("notification")
	private NotificationMarshaller notification;
	@SerializedName("priority")
	private String priority;
	@SerializedName("to")
	private String recipient;
	@SerializedName("time_to_live")
	private Integer timeToLive = DEFAULT_TIME_TO_LIVE;

	/**
	 * If a key is specified, then if a newer message is sent with the
	 * same key, the older message will be considered obsolete and
	 * discarded.
	 */
	public String getCollapseKey() {
		return this.collapseKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public DataMarshaller getData() {
		if (this.data == null) {
			this.data = new DataMarshaller();
		}
		return this.data;
	}

	public void setData(DataMarshaller data) {
		this.data = data;
	}

	public NotificationMarshaller getNotification() {
		if (this.notification == null) {
			this.notification = new NotificationMarshaller();
		}
		return this.notification;
	}

	public void setNotification(NotificationMarshaller notification) {
		this.notification = notification;
	}

	/**
	 * Priority notifications are interpreted differently on different devices,
	 * and may (for example) cause a device to wake from sleep mode for a high
	 * priority message.
	 */
	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		if (priority != null && !priority.equalsIgnoreCase(PRIORITY.NORMAL.toString()) && !priority.equalsIgnoreCase(PRIORITY.HIGH.toString())) {
			throw new IllegalArgumentException("Invalid priority value: " + priority);
		}
		this.priority = priority;
	}

	public void setPriority(PRIORITY priority) {
		this.priority = (priority != null) ? priority.toString() : null;
	}

	public String getRecipient() {
		return this.recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * This parameter controls how long FCM will attempt to deliver
	 * the message (specified in seconds).  The FCM default is four weeks, a
	 * value of "0" means that the message will be discarded if it can't
	 * be delivered immediately.
	 * @return
	 */
	public Integer getTimeToLive() {
		return this.timeToLive;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}
}
