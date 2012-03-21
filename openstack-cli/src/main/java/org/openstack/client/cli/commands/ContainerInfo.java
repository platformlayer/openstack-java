package org.openstack.client.cli.commands;

import org.kohsuke.args4j.Argument;
import org.openstack.client.cli.model.ContainerName;
import org.openstack.client.storage.OpenstackStorageClient;

public class ContainerInfo extends OpenstackCliCommandRunnerBase {
	@Argument(index = 0)
	public ContainerName name;

	public ContainerInfo() {
		super("container", "info");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackStorageClient client = getStorageClient();

		return client.root().containers().id(name.getKey()).get();
	}

}
