package com.heb.liquidsky.common;

import java.io.Serializable;

/**
 * Container for any object value.
 * 
 * The purpose of using this ValueContainer is to enable lazy-initialization of nullable
 * values.
 * 
 * Normally, if the result of some calculation was null, then future calls to obtain
 * that value would result in repeated calls to obtain the null value.
 */
public class ValueContainer<T> implements Serializable {

	private static final long serialVersionUID = 1l;

	@SuppressWarnings("rawtypes")
	public static ValueContainer NULL_VALUE_CONTAINER = new ValueContainer<Object>(null);

	private final T value;

	public ValueContainer(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public static <T> ValueContainer<T> getEmptyValueContainer() {
		return NULL_VALUE_CONTAINER;
	}
}
