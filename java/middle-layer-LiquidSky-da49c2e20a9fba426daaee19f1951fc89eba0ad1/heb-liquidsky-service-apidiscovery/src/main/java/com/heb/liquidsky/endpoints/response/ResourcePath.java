package com.heb.liquidsky.endpoints.response;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.common.CloudUtil;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.endpoints.ApiDiscoveryConstants;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

public class ResourcePath{

	private static final Logger logger = Logger.getLogger(ResourcePath.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ResourcePath.class);
	
	private String id;
	private String name;
	private String description;
	private String path;
	private String batchPath;
	
	private DataItem dataItem;
	

	/**
	 * Default Constructor
	 */
	public ResourcePath(){}
	
	/**
	 * Id Constructor
	 * @param id
	 */
	public ResourcePath(String id){
		this.setId(id);
	}
	
	/**
	 * Constructor from DataItem
	 * @param dataItem
	 */
	public ResourcePath(DataItem dataItem){
		this.setDataItem(dataItem);
	}
	
	/*
	 * ====  getters and setters ====
	 */

	/**
	 * Public getter for the Id property
	 * @return
	 */
	public String getId() {
		if (StringUtils.isBlank(this.id) && this.getDataItem() != null){
			this.setId(this.dataItem.getId());
		}
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Public getter for the name property
	 * @return
	 */
	public String getName() {
		if (StringUtils.isBlank(this.name) && this.getDataItem() != null){
			try {
				this.setName(this.dataItem.getString(ApiDiscoveryConstants.RESOURCE_PATH_PROPERTY_NAME));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Public getter for the description property
	 * @return
	 */
	public String getDescription() {
		if (StringUtils.isBlank(this.description) && this.getDataItem() != null){
			try {
				this.setDescription(this.dataItem.getString(ApiDiscoveryConstants.RESOURCE_PATH_PROPERTY_DESCRIPTION));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Public getter for the path property
	 * @return
	 */
	public String getPath() {
		if (StringUtils.isBlank(this.path) && this.getDataItem() != null){
			try {
				this.setPath(this.dataItem.getString(ApiDiscoveryConstants.RESOURCE_PATH_PROPERTY_PATH));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Public getter for the batchPath property
	 * @return
	 */
	public String getBatchPath() {
		if (StringUtils.isBlank(this.batchPath) && this.getDataItem() != null){
			try {
				this.setBatchPath(this.dataItem.getString(ApiDiscoveryConstants.RESOURCE_PATH_PROPERTY_BATCH_PATH));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return batchPath;
	}
	public void setBatchPath(String batchPath) {
		this.batchPath = batchPath;
	}
	
	/**
	 * Getter for the DataItem.  If it is not set and the ID is, then get it from the Liquid Sky Data Layer
	 * @return DataItem
	 */
	private DataItem getDataItem() {
		HebTraceContext context = TRACER.startSpan("getDataItem");
		try {
			if (this.dataItem != null){
				return this.dataItem;
			}
			if (!StringUtils.isBlank(id)){
				try {
					this.dataItem = DataStore.getInstance().readItemImmutable(this.id, ApiDiscoveryConstants.DATA_ITEM_NAME_RESOURCE_PATH);
					if (this.dataItem == null){
						logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Could not read data item for id " + this.id);
					}
				} catch (DataStoreException e) {
					logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Exception while reading data item for " + this.id, e);
				}
			}
			return this.dataItem;
		} finally {
			TRACER.endSpan(context);
		}
	}
	private void setDataItem(DataItem dataItem) {
		this.dataItem = dataItem;
	}

}