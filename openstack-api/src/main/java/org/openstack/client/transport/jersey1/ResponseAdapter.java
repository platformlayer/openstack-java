package org.openstack.client.transport.jersey1;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.openstack.client.transport.Response;

import com.sun.jersey.api.client.ClientResponse;

class ResponseAdapter implements Response {

	private final ClientResponse response;

	public ResponseAdapter(ClientResponse response) {
		this.response = response;
	}

	@Override
	public int getStatus() {
		return response.getStatus();
	}

	@Override
	public MediaType getType() {
		return response.getType();
	}

	@Override
	public <T> T getEntity(Class<T> clazz) {
		return response.getEntity(clazz);
	}

	@Override
	public InputStream getEntityInputStream() {
		return response.getEntityInputStream();
	}

}
