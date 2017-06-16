package com.heb.liquidsky.messaging.gson;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class NotificationMarshaller implements Serializable {

	private static final long serialVersionUID = 1l;

	@SerializedName("body")
	private String body;
	@SerializedName("title")
	private String title;

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
