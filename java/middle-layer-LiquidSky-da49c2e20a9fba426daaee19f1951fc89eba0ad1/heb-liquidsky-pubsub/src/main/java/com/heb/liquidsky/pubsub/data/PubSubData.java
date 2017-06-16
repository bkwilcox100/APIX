package com.heb.liquidsky.pubsub.data;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the generic interface for all items shared via pub/sub, ensuring
 * that all senders agree to a minimal contract for the data being shared.
 */
public interface PubSubData extends Serializable {

	public enum PUBSUB_ACTION { CREATE, UPDATE, DELETE };

	public PUBSUB_ACTION getAction();

	public String getDataType();

	public String getId();

	public String getSourceApplication();

	public String getSourceProject();

	public Date getTimestamp();
}
