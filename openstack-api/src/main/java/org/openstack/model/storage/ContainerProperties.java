package org.openstack.model.storage;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.Maps;

public class ContainerProperties {
	@JsonProperty("date")
	private Date date;

	@JsonProperty("x-container-object-count")
	private Long objectCount;

	@JsonProperty("x-container-bytes-used")
	private Long bytesUsed;

	private Map<String, String> customProperties;

	public Map<String, String> getCustomProperties() {
		if (customProperties == null) {
			customProperties = Maps.newHashMap();
		}
		return customProperties;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getObjectCount() {
		return objectCount;
	}

	public void setObjectCount(Long objectCount) {
		this.objectCount = objectCount;
	}

	public Long getBytesUsed() {
		return bytesUsed;
	}

	public void setBytesUsed(Long bytesUsed) {
		this.bytesUsed = bytesUsed;
	}

	public void setCustomProperties(Map<String, String> customProperties) {
		this.customProperties = customProperties;
	}

}
