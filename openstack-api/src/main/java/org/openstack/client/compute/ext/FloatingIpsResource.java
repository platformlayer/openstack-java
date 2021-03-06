package org.openstack.client.compute.ext;

import javax.ws.rs.core.MediaType;

import org.openstack.client.common.Resource;
import org.openstack.model.compute.CreateFloatingIpResponse;
import org.openstack.model.compute.FloatingIp;
import org.openstack.model.compute.FloatingIpList;

/**
 * Floating IPs support
 * 
 * @author sp
 * 
 */
public class FloatingIpsResource extends Resource {

	// Floating IPs seems to be JSON only
	// TODO: Is this an OpenStack bug or an HP bug?
	@Override
	protected MediaType getDefaultContentType() {
		return MediaType.APPLICATION_JSON_TYPE;
	}

	/**
	 * Return a list of floating ips allocated to a project.
	 * 
	 * @return
	 */
	public FloatingIpList list() {
		return resource().get(FloatingIpList.class);
	}

	public FloatingIp create(String pool) {
		return resource().post(FloatingIp.class, pool);
	}

	public FloatingIp create() {
		CreateFloatingIpResponse response = resource().post(CreateFloatingIpResponse.class, "{}");
		return response.getFloatingIp();
	}

	public FloatingIpResource floatingIp(String id) {
		return buildChildResource(id, FloatingIpResource.class);
	}

}
