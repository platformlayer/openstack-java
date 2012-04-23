package org.openstack.client.compute;

import javax.ws.rs.core.MediaType;

import org.openstack.client.compute.ext.ComputeResourceBase;
import org.openstack.model.common.ExtensionList;

public class ExtensionsResource extends ComputeResourceBase {

	@Override
	protected MediaType getDefaultContentType() {
		// Extensions namespace changed late in Essex release;
		// HP public cloud has "wrong" namespace
		return MediaType.APPLICATION_JSON_TYPE;
	}

	public ExtensionList list() {
		return resource().get(ExtensionList.class);
	}

	public ExtensionResource extension(String extensionAlias) {
		return buildChildResource(extensionAlias, ExtensionResource.class);
	}
}
