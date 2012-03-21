package org.openstack.client.storage;

import org.openstack.client.OpenstackException;
import org.openstack.client.OpenstackNotFoundException;
import org.openstack.client.common.HeadResponse;
import org.openstack.model.storage.ContainerProperties;
import org.openstack.model.storage.ObjectProperties;

public class ContainerResource extends StorageResourceBase {
	public void delete() {
		resource().delete();
	}

	public ObjectsResource objects() {
		return buildChildResource("", ObjectsResource.class);
	}

	public ContainerProperties get() {
		HeadResponse response = resource().head();
		int httpStatus = response.getStatus();
		if (httpStatus == 200 || httpStatus == 204) {
			ContainerProperties properties = SwiftHeaderUtils.unmarshalContainerHeaders(response);
			return properties;
		}

		if (httpStatus == 404) {
			throw new OpenstackNotFoundException("Object not found");
		}

		throw new OpenstackException("Unexpected HTTP status code: " + httpStatus);
	}

}
