package org.openstack.client.common;

import java.util.Map;

import org.openstack.client.OpenstackService;
import org.openstack.client.compute.TenantResource;
import org.openstack.client.extensions.Extension;
import org.openstack.client.imagestore.GlanceRootResource;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.compute.Flavor;
import org.openstack.model.compute.FloatingIp;
import org.openstack.model.compute.Image;
import org.openstack.model.compute.KeyPair;
import org.openstack.model.compute.SecurityGroup;
import org.openstack.model.compute.Server;
import org.openstack.model.identity.Service;

import com.google.common.collect.Maps;

public class DirectOpenstackService implements OpenstackService {
	final OpenstackSession session;
	final Map<Object, Object> extensions = Maps.newHashMap();

	public DirectOpenstackService(OpenstackSession session) {
		super();
		this.session = session;
	}

	private TenantResource computeRoot() {
		return session.getComputeClient().root();
	}

	private GlanceRootResource imageRoot() {
		return session.getImageClient().root();
	}

	@Override
	public Map<Object, Object> getExtensions() {
		return extensions;
	}

	@Override
	public <T> Iterable<T> listItems(Class<T> itemClass, boolean details) {
		if (itemClass == Server.class) {
			return (Iterable<T>) computeRoot().servers().list(details);
		}

		if (itemClass == Image.class) {
			return (Iterable<T>) computeRoot().images().list();
		}

		if (itemClass == SecurityGroup.class) {
			return (Iterable<T>) computeRoot().securityGroups().list().getList();
		}

		if (itemClass == KeyPair.class) {
			return (Iterable<T>) computeRoot().keyPairs().list();
		}

		if (itemClass == org.openstack.model.image.Image.class) {
			return (Iterable<T>) imageRoot().images().list(true);
		}

		if (itemClass == Flavor.class) {
			return (Iterable<T>) computeRoot().flavors().list(true);
		}

		if (itemClass == Service.class) {
			return (Iterable<T>) session.getAuthenticationToken().getServiceCatalog();
		}

		if (itemClass == Extension.class) {
			return (Iterable<T>) computeRoot().extensions().list();
		}

		if (itemClass == FloatingIp.class) {
			return (Iterable<T>) computeRoot().floatingIps().list();
		}

		throw new IllegalArgumentException("Unknown type: " + itemClass);
	}

	@Override
	public OpenstackStorageClient getStorageClient() {
		return session.getStorageClient();
	}

	@Override
	public OpenstackComputeClient getComputeClient() {
		return session.getComputeClient();
	}

	@Override
	public OpenstackImageClient getImageClient() {
		return session.getImageClient();
	}

	@Override
	public Flavor resolveFlavor(Flavor flavor) {
		return session.resolveFlavor(flavor);
	}

	@Override
	public Image resolveImage(Image image) {
		return session.resolveImage(image);
	}

	@Override
	public <T> void delete(T item) {
		Class<? extends Object> itemClass = item.getClass();

		if (itemClass == Image.class) {
			Image image = (Image) item;
			computeRoot().images().image(image.getId()).delete();
		}

		if (itemClass == org.openstack.model.image.Image.class) {
			org.openstack.model.image.Image image = (org.openstack.model.image.Image) item;
			computeRoot().images().image(image.getId()).delete();
		}

		if (itemClass == SecurityGroup.class) {
			SecurityGroup securityGroup = (SecurityGroup) item;
			computeRoot().securityGroups().securityGroup(securityGroup.getId()).delete();
		}

		throw new IllegalArgumentException("Unknown type: " + itemClass);
	}

}
