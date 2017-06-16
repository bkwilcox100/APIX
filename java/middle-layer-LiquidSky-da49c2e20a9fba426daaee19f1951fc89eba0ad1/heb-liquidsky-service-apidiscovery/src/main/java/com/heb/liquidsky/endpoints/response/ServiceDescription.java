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

public class ServiceDescription{

	private static final Logger logger = Logger.getLogger(ServiceDescription.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ServiceDescription.class);
	
	private String id;
	private String name;
	private String description;
	private String labels;
	private String openApiSpecUrl;
	private String documentation;
	private String currentVersion;
	private ArrayList<ServiceVersion> serviceVersions;
	
	private DataItem dataItem;
	

	/**
	 * Default Constructor
	 */
	public ServiceDescription(){}
	
	/**
	 * Id Constructor
	 * @param id
	 */
	public ServiceDescription(String id){
		this.setId(id);
		initialize();
	}
	
	/**
	 * Constructor from DataItem
	 * @param dataItem
	 */
	public ServiceDescription(DataItem dataItem){
		this.setDataItem(dataItem);
		initialize();
	}
	
	
	/*
	 * In this class, I am trying something different. I am questioning the value of having the response objects be lazy, so 
	 * In this class, I am testing initializing all properties during construction in order to compare ease of new class creation.
	 * This is non standard by previous examples, but should have the same result in the response.
	 */
	/**
	 * Initializes all properties of the class.
	 */
	private void initialize(){
		if (this.getDataItem() != null){
			try {
				this.setName(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_NAME));
				this.setDescription(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_DESCRIPTION));
				this.setLabels(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_LABELS));
				this.setOpenApiSpecUrl(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_OPEN_API_SPEC));
				this.setDocumentation(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_DOCUMENTATION));
				this.setCurrentVersion(this.dataItem.getString(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_CURRENT_VERSION));

				this.setServiceVersions(new ArrayList<ServiceVersion>());
				for (DataItem currentItem : this.getDataItem().getList(ApiDiscoveryConstants.SERVICE_DESCRIPTION_PROPERTY_SERVICE_VERSIONS)) {
					serviceVersions.add(new ServiceVersion(currentItem));
				}
				
			} catch (DataStoreException e) {
				logger.log(Level.SEVERE, CloudUtil.getMethodName() + " Failed to read property from " + this.dataItem.getId(), e);
			}
		}
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
					this.dataItem = DataStore.getInstance().readItemImmutable(this.id, ApiDiscoveryConstants.DATA_ITEM_NAME_SERVICE_DESCRIPTION);
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
		this.id = dataItem.getId();
		this.currentVersion = null;
		this.description = null;
		this.documentation = null;
		this.labels = null;
		this.name = null;
		this.openApiSpecUrl = null;
		this.serviceVersions = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getOpenApiSpecUrl() {
		return openApiSpecUrl;
	}

	public void setOpenApiSpecUrl(String openApiSpecUrl) {
		this.openApiSpecUrl = openApiSpecUrl;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public ArrayList<ServiceVersion> getServiceVersions() {
		return serviceVersions;
	}

	public void setServiceVersions(ArrayList<ServiceVersion> serviceVersions) {
		this.serviceVersions = serviceVersions;
	}


}