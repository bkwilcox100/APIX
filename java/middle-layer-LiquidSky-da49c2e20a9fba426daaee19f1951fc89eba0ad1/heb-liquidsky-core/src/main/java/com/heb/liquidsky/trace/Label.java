package com.heb.liquidsky.trace;

public class Label {

	private final String key;
	private final String value;

	public Label(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
}
