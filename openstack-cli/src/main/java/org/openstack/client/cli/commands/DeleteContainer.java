package org.openstack.client.cli.commands;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.openstack.client.cli.model.StoragePath;
import org.openstack.client.storage.ContainerResource;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.storage.StorageObject;

import com.fathomdb.cli.CliException;
import com.google.common.collect.Lists;

public class DeleteContainer extends OpenstackCliCommandRunnerBase {
	@Option(name = "-f", aliases = { "--force" })
	boolean force;

	@Option(name = "-n", aliases = { "--threads" })
	int threads = 32;

	@Argument(index = 0)
	public StoragePath path;

	public DeleteContainer() {
		super("delete", "container");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackStorageClient client = getStorageClient();

		String containerName = path.getContainer();
		String objectPath = path.getObjectPath();
		if (objectPath != null) {
			throw new CliException("Cannot specify path when deleting container");
		}

		final ContainerResource containerResource = client.root().containers().id(containerName);
		if (force) {
			System.out.println("Force specified; will delete all items");

			ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threads);

			List<String> storageObjectNames = Lists.newArrayList();
			for (StorageObject storageObject : containerResource.objects().list()) {
				String storageObjectName = storageObject.getName();
				storageObjectNames.add(storageObjectName);
			}

			System.out.println("Count = " + storageObjectNames.size());

			List<Future> futures = Lists.newArrayList();

			for (final String storageObjectName : storageObjectNames) {
				Future<?> submitted = newFixedThreadPool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							System.out.println("\t" + storageObjectName);
							containerResource.objects().id(storageObjectName).delete();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				});
				futures.add(submitted);
			}

			for (Future future : futures) {
				future.get(300, TimeUnit.SECONDS);
			}

			newFixedThreadPool.shutdown();

			newFixedThreadPool.awaitTermination(300, TimeUnit.SECONDS);
		}
		containerResource.delete();

		return containerName;
	}

	@Override
	public void formatRaw(Object o, PrintWriter writer) {
		String result = (String) o;
		writer.println(result);
	}

}
