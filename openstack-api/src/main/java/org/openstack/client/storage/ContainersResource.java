package org.openstack.client.storage;

import org.openstack.client.common.RequestBuilder;
import org.openstack.model.storage.Account;
import org.openstack.model.storage.Container;

public class ContainersResource extends StorageResourceBase {

	// GET /account List containers
	// HEAD account Retrieve account metadata

	// Verb URI Description
	// GET /account/container List objects
	// PUT /account/container Create container
	// DELETE /account/container Delete container
	// HEAD /account/container Retrieve container metadata

	// Storage Objects
	// Verb URI Description
	// GET /account/container/object Retrieve object
	// PUT /account/container/object Create/Update Object
	// PUT /account/container/object Chunked transfer encoding
	// DELETE /account/container/object Delete container
	// HEAD /account/container/object Retrieve object metadata
	// POST /account/container/object Update object metadata

	public Iterable<Container> list() {
		RequestBuilder imagesResource = resource();

		Account account = imagesResource.get(Account.class);
		return account.getContainers();
	}

	public ContainerResource id(String id) {
		return buildChildResource(id, ContainerResource.class);
	}

	public void create(String containerName) {
		// Should return 202
		resource(encodeForUrl(containerName)).put();
	}

}
