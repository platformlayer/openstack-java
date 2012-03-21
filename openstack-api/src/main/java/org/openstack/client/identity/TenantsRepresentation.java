package org.openstack.client.identity;

import java.util.List;

import org.openstack.client.common.OpenstackSession;
import org.openstack.model.identity.TenantList;

public class TenantsRepresentation {
		
	private OpenstackSession client;
	
	private TenantList model;

	public TenantsRepresentation(OpenstackSession client, TenantList model) {
		this.client = client;
		this.model = model;
	}
	
	public List<org.openstack.model.identity.Tenant> getList() {
		return model.getList();
	}
	
	public TenantsRepresentation next() {
		TenantList tenantList = client.resource(model.getLinks().get(0).getHref()).get(TenantList.class);
		return new TenantsRepresentation(client, tenantList);
	}
	
}
