package com.heb.liquidsky.data.custom;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.DataStoreProperty;
import com.heb.liquidsky.data.db.DataStoreMarshaller;
import com.heb.liquidsky.data.xml.CustomPropertyElement;
import com.heb.liquidsky.data.xml.PropertyAttributeElement;

public class CustomProperty implements DataStoreMarshaller, DataStoreProperty {

	private final Map<String, String> attributes;
	private final CustomPropertyImplementation customPropertyImplementation;
	private final String itemType;
	private final String listItemType;
	private final String property;
	private final Class<?> propertyType;
	private final String source;

	public CustomProperty(CustomPropertyElement marshaller) throws DataStoreException {
		this.property = marshaller.getPropertyName();
		this.source = marshaller.getSource();
		this.listItemType = marshaller.getListItemType();
		this.itemType = marshaller.getItemType();
		this.attributes = marshaller.getAttributes().isEmpty() ? Collections.<String,String>emptyMap() : new HashMap<String, String>();
		if (marshaller.getAttributes() != null) {
			for (PropertyAttributeElement attribute : marshaller.getAttributes()) {
				this.attributes.put(attribute.getName(), attribute.getValue());
			}
		}
		try {
			this.customPropertyImplementation = Class.forName(this.getSource()).asSubclass(CustomPropertyImplementation.class).getDeclaredConstructor(Map.class).newInstance(this.getAttributes());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new DataStoreException("Failure while initializing class for " + this.getSource(), e);
		}
		try {
			this.propertyType = Class.forName(marshaller.getPropertyType());
		} catch (ClassNotFoundException e) {
			throw new DataStoreException("Invalid property-type value: " + marshaller.getPropertyType(), e);
		}
		this.validateConfig();
	}

	@Override
	public Map<String, String> getAttributes() {
		return this.attributes;
	}
	
	@Override
	public String getAttributeByName(String name) {
		if(!getAttributes().isEmpty()) {
			return getAttributes().get(name);
		}
		return null;
	}

	public CustomPropertyImplementation getCustomPropertyImplementation() {
		return this.customPropertyImplementation;
	}

	@Override
	public boolean isDataItem() {
		return (DataItem.class.isAssignableFrom(this.getPropertyType()) || Collection.class.isAssignableFrom(this.getPropertyType()));
	}

	@Override
	public String getItemType() {
		return this.itemType;
	}

	@Override
	public String getListItemType() {
		return this.listItemType;
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	public Class<?> getPropertyType() {
		return this.propertyType;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	protected String getSource() {
		return this.source;
	}

	@Override
	public void validateConfig() throws DataStoreException {
		if (StringUtils.isBlank(this.getProperty())) {
			throw new DataStoreException("No 'name' property value specified for custom property");
		}
	}
}
