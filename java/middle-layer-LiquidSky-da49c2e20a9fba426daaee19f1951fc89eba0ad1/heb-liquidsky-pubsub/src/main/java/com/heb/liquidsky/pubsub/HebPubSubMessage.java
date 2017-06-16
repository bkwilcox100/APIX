package com.heb.liquidsky.pubsub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heb.liquidsky.pubsub.data.PubSubData;

public class HebPubSubMessage implements Serializable {

	private static final long serialVersionUID = 1l;
	private static final Gson GSON = new GsonBuilder().create();

	private final PubsubMessage pubsubMessage;

	/**
	 * This constructor is used to build messages to be sent via pub/sub.
	 */
	public HebPubSubMessage(String messageId) {
		this.pubsubMessage = new PubsubMessage();
		this.getPubsubMessage().setMessageId(messageId);
	}

	/**
	 * This constructor is used for messages received from a pub/sub
	 * notification via a pull request.
	 */
	public HebPubSubMessage(PubsubMessage message) {
		this.pubsubMessage = message;
	}

	/**
	 * This constructor is used for messages received from a pub/sub
	 * notification via a push request.
	 */
	public HebPubSubMessage(InputStream stream) throws IOException {
		try (Reader reader = new InputStreamReader(stream)) {
			this.pubsubMessage = GSON.fromJson(reader, HebReceivedMessage.class).getMessage();
		}
	}

	public PubSubData getData() throws IOException {
		byte[] bytes = this.getPubsubMessage().decodeData();
		return this.toObject(bytes);
	}

	public void setData(PubSubData data) throws IOException {
		byte[] bytes = this.toByteArray(data);
		this.getPubsubMessage().encodeData(bytes);
	}

	public String getMessageId() {
		return this.getPubsubMessage().getMessageId();
	}

	private PubsubMessage getPubsubMessage() {
		return this.pubsubMessage;
	}

	protected PublishRequest publishRequest() {
		PublishRequest publishRequest = new PublishRequest();
		List<PubsubMessage> messages = new ArrayList<>();
		messages.add(this.getPubsubMessage());
		publishRequest.setMessages(messages);
		return publishRequest;
	}

	protected static PublishRequest publishRequest(List<HebPubSubMessage> messages) {
		PublishRequest publishRequest = new PublishRequest();
		List<PubsubMessage> pubsubMessages = new ArrayList<>();
		for (HebPubSubMessage message : messages) {
			pubsubMessages.add(message.getPubsubMessage());
		}
		publishRequest.setMessages(pubsubMessages);
		return publishRequest;
	}

	public byte[] toByteArray(PubSubData data) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(data);
			out.flush();
			return bos.toByteArray();
		}
	}

	/**
	 * Convert a byte array to a serializable object.
	 * 
	 * @param bytes The byte array to convert.
	 * @param clazz The class type of the expected object
	 * @return The expected object
	 */
	private PubSubData toObject(byte[] bytes) throws IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
			try {
				return (PubSubData) in.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
		}
	}

	public String toPrettyString() {
		try {
			return this.getPubsubMessage().toPrettyString();
		} catch (IOException e) {
			return e.getMessage();
		}
	}
}
