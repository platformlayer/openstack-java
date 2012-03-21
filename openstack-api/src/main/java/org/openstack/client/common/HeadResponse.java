package org.openstack.client.common;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Maps;

public class HeadResponse {
	final int httpStatus;
	final Map<String, List<String>> headers;

	public HeadResponse(int httpStatus, Map<String, List<String>> headers) {
		super();
		this.httpStatus = httpStatus;
		this.headers = headers;
	}

	public HeadResponse(int httpStatus, MultivaluedMap<String, Object> headers) {
		super();
		this.httpStatus = httpStatus;
		this.headers = Maps.newHashMap();
		for (Entry<String, List<Object>> entry : headers.entrySet()) {
			String key = entry.getKey();
			for (Object value : entry.getValue()) {
				String valueString = (String) value;
				headers.add(key, valueString);
			}
		}
	}

	public int getStatus() {
		return httpStatus;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

}
