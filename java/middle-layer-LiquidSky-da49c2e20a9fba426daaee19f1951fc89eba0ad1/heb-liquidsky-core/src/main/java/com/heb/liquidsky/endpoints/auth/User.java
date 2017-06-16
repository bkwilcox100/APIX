package com.heb.liquidsky.endpoints.auth;

import java.io.Serializable;
import java.util.Objects;

/**
 * Copied from com.google.api.server.spi.auth.common.User.
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String id;
	private final String email;

	public User(String email) {
		this(null, email);
	}

	public User(String id, String email) {
		this.id = id;
		this.email = email;
	}

	public String getId() {
		return this.id;
	}

	public String getEmail() {
		return this.email;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof User) {
			User user = (User) other;
			return ((Objects.equals(this.id, user.id)) && (Objects.equals(this.email, user.email)));
		}
		return false;
	}

	public int hashCode() {
		return Objects.hash(new Object[] { this.id, this.email });
	}

	public String toString() {
		return String.format("id:%s, email:%s", new Object[] { this.id, this.email });
	}

}
