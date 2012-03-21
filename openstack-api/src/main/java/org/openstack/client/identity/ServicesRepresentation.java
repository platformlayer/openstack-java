package org.openstack.client.identity;

import java.util.List;

import org.openstack.client.common.OpenstackSession;
import org.openstack.model.identity.ServiceList;

public class ServicesRepresentation {
		
	private OpenstackSession client;
	
	private ServiceList model;

	public ServicesRepresentation(OpenstackSession client, ServiceList model) {
		this.client = client;
		this.model = model;
	}
	
	public List<org.openstack.model.identity.Service> getList() {
		return model.getList();
	}
	
	public ServicesRepresentation next() {
		ServiceList tenantList = client.resource(model.getLinks().get(0).getHref()).get(ServiceList.class);
		return new ServicesRepresentation(client, tenantList);
	}
	
}
