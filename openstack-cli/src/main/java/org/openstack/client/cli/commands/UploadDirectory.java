package org.openstack.client.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.kohsuke.args4j.Argument;
import org.openstack.client.OpenstackException;
import org.openstack.client.cli.model.StoragePath;
import org.openstack.client.storage.ObjectsResource;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.storage.ObjectProperties;
import org.openstack.model.storage.StorageObject;
import org.openstack.utils.Hex;
import org.openstack.utils.Io;
import org.openstack.utils.Md5Hasher;

import com.google.common.collect.Maps;

public class UploadDirectory extends OpenstackCliCommandRunnerBase {
	@Argument(index = 0)
	public String source;

	@Argument(index = 1)
	public StoragePath dest;

	private final Map<String, StorageObject> existing = Maps.newHashMap();

	long uploaded = 0;
	long unchanged = 0;

	public UploadDirectory() {
		super("upload", "directory");
	}

	@Override
	public Object runCommand() throws Exception {
		OpenstackStorageClient client = getStorageClient();

		for (StorageObject object : client.listObjects(dest.getContainer(), dest.getObjectPath(), null)) {
			String name = object.getName();
			existing.put(name, object);
		}

		uploadDirectory(Io.resolve(source), dest);
		return "Uploaded: " + uploaded + " Unchanged=" + unchanged;
	}

	private void uploadDirectory(File source, StoragePath target) throws OpenstackException, IOException {
		for (File file : source.listFiles()) {
			String name = file.getName();
			StoragePath childTarget = new StoragePath(target, name);

			if (file.isDirectory()) {
				uploadDirectory(file, childTarget);
			} else {
				uploadFile(file, childTarget);
			}
		}
	}

	private void uploadFile(File source, StoragePath target) throws OpenstackException, IOException {
		StorageObject storageObject = existing.get(target.getObjectPath());
		if (storageObject != null) {
			if (isUnchanged(source, storageObject)) {
				System.out.println("Unchanged: " + source);
				unchanged += source.length();
				return;
			}
		}

		OpenstackStorageClient client = getStorageClient();

		String containerName = target.getContainer();
		String objectPath = target.getObjectPath();

		ObjectsResource objects = client.root().containers().id(containerName).objects();

		ObjectProperties objectProperties = new ObjectProperties();
		objectProperties.setName(objectPath);
		objectProperties.setContentType(getContentType(source));

		objects.putObject(source, objectProperties);

		System.out.println("Uploaded: " + source);
		uploaded += source.length();
	}

	private boolean isUnchanged(File source, StorageObject storageObject) throws IOException {
		if (storageObject.getHash() == null) {
			return false;
		}

		if (storageObject.getBytes() != source.length()) {
			return false;
		}

		byte[] remoteHash = Hex.fromHex(storageObject.getHash());

		Md5Hasher hasher = new Md5Hasher();
		byte[] localHash = hasher.hash(source);

		if (Arrays.equals(localHash, remoteHash)) {
			return true;
		}

		return false;
	}

	private String getContentType(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf('.');
		if (lastDot != -1) {
			String extension = name.substring(lastDot + 1);
			extension = extension.toLowerCase();
			if (extension.equals("png")) {
				return "image/png";
			} else if (extension.equals("xml")) {
				return "application/xml";
			} else if (extension.equals("css")) {
				return "text/css";
			} else if (extension.equals("js")) {
				return "application/javascript";
			} else if (extension.equals("html")) {
				return "text/html";
			} else if (extension.equals("swf")) {
				return "application/x-shockwave-flash";
			}
		}

		System.err.println("Cannot deduce MIME type for " + name);
		return null;
	}
}
