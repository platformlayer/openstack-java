package org.openstack.client.common;

import org.openstack.client.OpenstackException;
import org.openstack.client.compute.AsyncServerOperation;
import org.openstack.client.compute.TenantResource;
import org.openstack.model.compute.Server;
import org.openstack.model.compute.ServerForCreate;
import org.openstack.model.compute.server.action.RebootAction.RebootType;

public class OpenstackComputeClient {

	OpenstackSession session;
	TenantResource root;

	public OpenstackComputeClient(OpenstackSession session) {
		this.session = session;
		root();
	}

	public synchronized TenantResource root() throws OpenstackException {
		if (root == null) {
			String endpoint = session.getBestEndpoint("compute");
			root = new TenantResource(session, endpoint);
		}

		return root;
	}

	public AsyncServerOperation createServer(ServerForCreate create) throws OpenstackException {
		Server server = root().servers().create(create);
		return AsyncServerOperation.wrapServerCreate(this, server);
	}

	public AsyncServerOperation deleteServer(String serverId) throws OpenstackException {
		Server server = root().servers().server(serverId).show();
		root().servers().server(serverId).delete();
		return AsyncServerOperation.wrapServerDelete(this, server);
	}

	public AsyncServerOperation powerServerOn(String serverId) throws OpenstackException {
		Server server = root().servers().server(serverId).show();

		root().servers().server(serverId).reboot(RebootType.HARD);
		return AsyncServerOperation.wrapServerPowerOn(this, server);
	}

	public OpenstackSession getSession() {
		return session;
	}

	public String getRootUrl() {
		return root().resource;
	}
}
