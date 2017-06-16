package com.heb.liquidsky.endpoints.response;

import java.util.ArrayList;
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

public class ServiceVersion{

	private static final Logger logger = Logger.getLogger(ServiceVersion.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ServiceVersion.class);
	
	private String id;
	private String versionNumber;
	private String description;
	private String hostName;
	private String basePath;
	private String openApiSpecUrl;
	private ArrayList<ResourcePath> resourcePaths;
	
	private DataItem dataItem;
	

	/**
	 * Default Constructor
	 */
	public ServiceVersion(){}
	
	/**
	 * Id Constructor
	 * @param id
	 */
	public ServiceVersion(String id){
		this.setId(id);
	}
	
	/**
	 * Constructor from DataItem
	 * @param dataItem
	 */
	public ServiceVersion(DataItem dataItem){
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
	 * 
	 * @return
	 */
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * Public getter for the description property
	 * @return
	 */
	public String getDescription() {
		if (StringUtils.isBlank(this.description) && this.getDataItem() != null){
			try {
				this.setDescription(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_VERSION_PROPERTY_DESCRIPTION));
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
	 * Public getter for the hostName property
	 * @return
	 */
	public String getHostName() {
		if (StringUtils.isBlank(this.hostName) && this.getDataItem() != null){
			try {
				this.setHostName(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_VERSION_PROPERTY_HOST_NAME));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Public getter for the basePath property
	 * @return
	 */
	public String getBasePath() {
		if (StringUtils.isBlank(this.basePath) && this.getDataItem() != null){
			try {
				this.setBasePath(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_VERSION_PROPERTY_BASE_PATH));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * Public getter for the openApiSpecUrl property
	 * @return
	 */
	public String getOpenApiSpecUrl() {
		if (StringUtils.isBlank(this.openApiSpecUrl) && this.getDataItem() != null){
			try {
				this.setOpenApiSpecUrl(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_VERSION_PROPERTY_OPEN_API_SPEC));
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return openApiSpecUrl;
	}
	public void setOpenApiSpecUrl(String openApiSpecUrl) {
		this.openApiSpecUrl = openApiSpecUrl;
	}

	/**
	 * Public getter for the resourcePaths property
	 * @return
	 */
	public ArrayList<ResourcePath> getResourcePaths() {
		if (this.resourcePaths == null && this.getDataItem() != null){
			try {
				this.setResourcePaths(new ArrayList<ResourcePath>());
				for (DataItem currentItem : this.getDataItem().getList(ApiDiscoveryConstants.SERVICE_VERSION_PROPERTY_RESOURCE_PATHS)) {
					resourcePaths.add(new ResourcePath(currentItem));
				}
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
		return resourcePaths;
	}
	public void setResourcePaths(ArrayList<ResourcePath> resourcePaths) {
		this.resourcePaths = resourcePaths;
	}
	
	
	/**
	 * Private Getter for the DataItem.  If it is not set and the ID is, then get it from the Liquid Sky Data Layer
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
					this.dataItem = DataStore.getInstance().readItemImmutable(this.id, ApiDiscoveryConstants.DATA_ITEM_NAME_SERVICE_VERSION);
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