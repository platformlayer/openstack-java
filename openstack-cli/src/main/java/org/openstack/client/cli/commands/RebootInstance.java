package org.openstack.client.cli.commands;

import org.kohsuke.args4j.Argument;
import org.openstack.client.cli.OpenstackCliContext;
import org.openstack.client.cli.model.InstanceName;
import org.openstack.client.common.OpenstackComputeClient;
import org.openstack.model.compute.Server;
import org.openstack.model.compute.server.action.RebootAction.RebootType;

public class RebootInstance extends OpenstackCliCommandRunnerBase {
	@Argument(index = 0)
	public InstanceName instanceName;

	public RebootInstance() {
		super("reboot", "instance");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackCliContext context = getContext();

		String serverId = instanceName.findInstanceId(context);
		if (serverId == null) {
			throw new IllegalArgumentException("Cannot find instance: " + instanceName.getKey());
		}

		OpenstackComputeClient tenant = context.getComputeClient();
		tenant.root().servers().server(serverId).reboot(RebootType.SOFT);

		invalidateCache(Server.class);

		return serverId;
	}

}
