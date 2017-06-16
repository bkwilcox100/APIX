package com.heb.liquidsky.data.db;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.data.xml.NamedQueryElement;

public class NamedQuery implements DataStoreMarshaller {

	private final String name;
	private final String sql;

	protected NamedQuery(NamedQueryElement marshaller) throws DataStoreException {
		this.name = marshaller.getName();
		this.sql = marshaller.getSql();
		this.validateConfig();
	}

	protected String getName() {
		return this.name;
	}

	protected String getSql() {
		return this.sql;
	}

	@Override
	public void validateConfig() throws DataStoreException {
		if (StringUtils.isBlank(this.getName())) {
			throw new DataStoreException("Invalid config: name attribute must be specified");
		}
		if (StringUtils.isBlank(this.getSql())) {
			throw new DataStoreException("Invalid config for " + this.getName() + ": SQL must be specified");
		}
	}
}
