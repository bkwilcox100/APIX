package com.heb.liquidsky.messaging.gson;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.pubsub.data.PubSubData.PUBSUB_ACTION;

public class DataMarshaller implements Serializable {

	private static final long serialVersionUID = 1l;

	@SerializedName("action")
	private PUBSUB_ACTION action;
	@SerializedName("data-item-endpoint")
	private String dataItemEndpoint;
	@SerializedName("data-item-id")
	private String dataItemId;
	@SerializedName("data-item-type")
	private String dataItemType;
	@SerializedName("timestamp")
	private Date timestamp;

	public PUBSUB_ACTION getAction() {
		return this.action;
	}

	public void setAction(PUBSUB_ACTION action) {
		this.action = action;
	}

	public void setDataItem(DataItem dataItem) {
		if (dataItem != null) {
			// TODO - clean this up, testing only at the moment
			if ("shoppinglist".equals(dataItem.getDataType().getName())) {
				this.setDataItemEndpoint("/_ah/api/shoppinglist/v1/shoppingLists/" + dataItem.getId());
			}
		}
		this.setDataItemId((dataItem != null) ? dataItem.getId() : null);
		this.setDataItemType((dataItem != null) ? dataItem.getDataType().getName() : null);
	}

	public String getDataItemEndpoint() {
		return this.dataItemEndpoint;
	}

	public void setDataItemEndpoint(String dataItemEndpoint) {
		this.dataItemEndpoint = dataItemEndpoint;
	}

	public String getDataItemId() {
		return this.dataItemId;
	}

	public void setDataItemId(String dataItemId) {
		this.dataItemId = dataItemId;
	}

	public String getDataItemType() {
		return this.dataItemType;
	}

	public void setDataItemType(String dataItemType) {
		this.dataItemType = dataItemType;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
