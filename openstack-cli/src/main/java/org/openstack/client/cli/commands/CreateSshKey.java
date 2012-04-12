package org.openstack.client.cli.commands;

import java.io.InputStream;

import org.kohsuke.args4j.Argument;
import org.openstack.client.cli.OpenstackCliContext;
import org.openstack.client.common.OpenstackComputeClient;
import org.openstack.model.compute.KeyPair;
import org.openstack.utils.Io;
import org.openstack.utils.NoCloseInputStream;

public class CreateSshKey extends OpenstackCliCommandRunnerBase {
	@Argument(index = 0)
	public String keyName;

	public CreateSshKey() {
		super("create", "sshkey");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackCliContext context = getContext();

		// This command will probably be faster _not_ in nailgun mode
		InputStream stream = new NoCloseInputStream(System.in);
		String publicKey = Io.readAll(stream);

		OpenstackComputeClient client = context.getComputeClient();
		KeyPair keyPair = new KeyPair();
		keyPair.setName(keyName);
		keyPair.setPublicKey(publicKey);
		return client.root().keyPairs().create(keyPair);
	}

}
