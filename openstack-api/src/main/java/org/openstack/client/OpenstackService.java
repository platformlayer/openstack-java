package org.openstack.client;

import java.util.Map;

import org.openstack.client.common.OpenstackComputeClient;
import org.openstack.client.common.OpenstackImageClient;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.compute.Flavor;
import org.openstack.model.compute.Image;

public interface OpenstackService {
	<T> Iterable<T> listItems(Class<T> itemClass, boolean details);

	Map<Object, Object> getExtensions();

	@Deprecated
	OpenstackStorageClient getStorageClient();

	@Deprecated
	OpenstackComputeClient getComputeClient();

	@Deprecated
	OpenstackImageClient getImageClient();

	Flavor resolveFlavor(Flavor flavor);

	Image resolveImage(Image image);
}
