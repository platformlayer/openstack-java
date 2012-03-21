package org.openstack.client.transport.jersey2;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

class ResponseAdapter implements org.openstack.client.transport.Response {
	final Response response;

	public ResponseAdapter(Response response) {
		super();
		this.response = response;
	}

	@Override
	public int getStatus() {
		return response.getStatus();
	}

	@Override
	public MediaType getType() {
		return response.getHeaders().getMediaType();
	}

	@Override
	public <T> T getEntity(Class<T> clazz) {
		return response.readEntity(clazz);
	}

	@Override
	public InputStream getEntityInputStream() {
		return response.readEntity(InputStream.class);
	}

}
