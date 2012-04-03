package org.openstack.client.imagestore;

import org.openstack.client.common.OpenstackSession;
import org.openstack.model.image.Image;

public class ImageRepresentation {
	private final OpenstackSession client;
	private final Image model;

	public ImageRepresentation(OpenstackSession client, Image model) {
		this.client = client;
		this.model = model;
	}

	public Image getModel() {
		return model;
	}
}
