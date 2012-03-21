package org.openstack.client.cli.commands;

import java.io.PrintWriter;

import org.kohsuke.args4j.Argument;
import org.openstack.client.cli.model.StoragePath;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.storage.StorageObject;

public class ListFiles extends OpenstackCliCommandRunnerBase {
	@Argument(index = 0)
	public StoragePath path;

	public ListFiles() {
		super("list", "files");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackStorageClient client = getStorageClient();

		String containerName = path.getContainer();
		String objectPath = path.getObjectPath();

		String delimiter = null;
		return client.root().containers().id(containerName).objects().list(objectPath, delimiter);
	}

	@Override
	public void formatRaw(Object o, PrintWriter writer) {
		Iterable<StorageObject> items = (Iterable<StorageObject>) o;
		for (StorageObject item : items) {
			String containerName = path.getContainer();
			writer.println(containerName + "/" + item.getName());
		}
	}

}
