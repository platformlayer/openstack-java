package org.openstack.client.compute;

import javax.ws.rs.core.MediaType;

import org.openstack.client.compute.ext.ComputeResourceBase;
import org.openstack.model.common.Extension;

public class ExtensionResource extends ComputeResourceBase {
	@Override
	protected MediaType getDefaultContentType() {
		// Extensions namespace changed late in Essex release;
		// HP public cloud has "wrong" namespace
		return MediaType.APPLICATION_JSON_TYPE;
	}

	public Extension show() {
		return resource().get(Extension.class);
	}
}
