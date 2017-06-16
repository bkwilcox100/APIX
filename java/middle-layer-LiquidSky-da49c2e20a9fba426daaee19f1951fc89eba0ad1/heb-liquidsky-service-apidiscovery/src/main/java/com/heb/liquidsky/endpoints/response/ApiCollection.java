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

public class ApiCollection{

	private static final Logger logger = Logger.getLogger(ApiCollection.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ApiCollection.class);
	
	private String id;
	private String name;
	private String description;
	private String contactInfo;
	private ArrayList<ServiceDescription> serviceDescriptions;
	
	private DataItem dataItem;
	

	/**
	 * Default Constructor
	 */
	public ApiCollection(){}
	
	/**
	 * Id Constructor
	 * @param id
	 */
	public ApiCollection(String id) throws ServiceException {
		this.setId(id);
		initialize();
	}
	
	/**
	 * Constructor from DataItem
	 * @param dataItem
	 */
	public ApiCollection(DataItem dataItem) throws ServiceException {
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
	private void initialize() throws ServiceException {
		if (this.getDataItem() != null){
			try {
				this.setName(this.dataItem.getString(ApiDiscoveryConstants.API_COLLECTION_PROPERTY_NAME));
				this.setDescription(this.dataItem.getString(ApiDiscoveryConstants.API_COLLECTION_PROPERTY_DESCRIPTION));
				this.setContactInfo(this.dataItem.getString(ApiDiscoveryConstants.API_COLLECTION_PROPERTY_CONTACT_INFO));
				
				this.setServiceDescriptions(new ArrayList<ServiceDescription>());
				for (DataItem currentItem : this.getDataItem().getList(ApiDiscoveryConstants.API_COLLECTION_PROPERTY_SERVICE_DESCRIPTIONS)) {
					serviceDescriptions.add(new ServiceDescription(currentItem));
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
	public String getId() throws ServiceException {
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
	 * @throws NotFoundException 
	 */
	private DataItem getDataItem() throws ServiceException {
		HebTraceContext context = TRACER.startSpan("getDataItem");
		try {
			if (this.dataItem != null){
				return this.dataItem;
			}
			if (!StringUtils.isBlank(id)){
				try {
					this.dataItem = DataStore.getInstance().readItemImmutable(this.id, ApiDiscoveryConstants.DATA_ITEM_NAME_API_COLLECTION);
					if (this.dataItem == null){
						throw new NotFoundException(CloudUtil.getMethodName() + " Could not read data item for id " + this.id);
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
		this.description = null;
		this.name = null;
		this.contactInfo = null;
		this.serviceDescriptions = null;
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

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public ArrayList<ServiceDescription> getServiceDescriptions() {
		return serviceDescriptions;
	}

	public void setServiceDescriptions(ArrayList<ServiceDescription> serviceDescriptions) {
		this.serviceDescriptions = serviceDescriptions;
	}


}