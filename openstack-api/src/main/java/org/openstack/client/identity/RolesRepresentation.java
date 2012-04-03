package org.openstack.client.identity;

import java.util.List;

import org.openstack.client.common.OpenstackSession;
import org.openstack.model.identity.RoleList;

public class RolesRepresentation {

	private OpenstackSession client;

	private RoleList model;

	public RolesRepresentation(OpenstackSession client, RoleList model) {
		this.client = client;
		this.model = model;
	}

	public List<org.openstack.model.identity.Role> getList() {
		return model.getList();
	}

	public RolesRepresentation next() {
		RoleList tenantList = client.resource(model.getLinks().get(0).getHref()).get(RoleList.class);
		return new RolesRepresentation(client, tenantList);
	}

}
