package org.openstack.client.transport;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

public interface Response {
	int getStatus();

	MediaType getType();

	<T> T getEntity(Class<T> clazz);

	InputStream getEntityInputStream();
}
