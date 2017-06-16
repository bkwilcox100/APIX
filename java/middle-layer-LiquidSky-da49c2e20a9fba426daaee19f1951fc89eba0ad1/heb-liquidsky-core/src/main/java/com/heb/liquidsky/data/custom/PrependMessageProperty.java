package com.heb.liquidsky.data.custom;

import java.util.Map;

import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;

// TODO - move this property to the test package
public class PrependMessageProperty extends AbstractCustomPropertyDescriptor {

	private static final String ATTR_MESSAGE = "message";
	private static final String ATTR_PROPERTY = "property";

	public PrependMessageProperty(Map<String, String> attributes) throws InstantiationException {
		super(attributes);
		if (attributes == null || !attributes.containsKey(ATTR_MESSAGE)) {
			throw new InstantiationException("EchoMessageProperty must specify a " + ATTR_MESSAGE + " attribute");
		}
		if (attributes == null || !attributes.containsKey(ATTR_PROPERTY)) {
			throw new InstantiationException("EchoMessageProperty must specify a " + ATTR_PROPERTY + " attribute");
		}
	}

	@Override
	public String getPropertyValue(DataItem dataItem) throws DataStoreException {
		String property = this.getAttributeValue(ATTR_PROPERTY);
		return this.getAttributeValue(ATTR_MESSAGE) + dataItem.getString(property);
	}

}
