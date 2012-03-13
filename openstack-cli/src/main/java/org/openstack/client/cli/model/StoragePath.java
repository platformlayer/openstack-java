package org.openstack.client.cli.model;

import org.openstack.client.cli.autocomplete.StoragePathAutoCompleter;
import org.openstack.client.storage.ObjectResource;
import org.openstack.client.storage.OpenstackStorageClient;

import com.fathomdb.cli.StringWrapper;
import com.fathomdb.cli.autocomplete.HasAutoCompletor;

@HasAutoCompletor(StoragePathAutoCompleter.class)
public class StoragePath extends StringWrapper {
	public StoragePath(String key) {
		super(key);
	}

	public ObjectResource getResource(OpenstackStorageClient client) {
		String containerName = getContainer();
		String objectPath = getObjectPath();
		if (containerName == null || objectPath == null) {
			throw new IllegalArgumentException("Cannot parse: " + getKey());
		}
		return client.root().containers().id(containerName).objects().id(objectPath);
	}

	public String getContainer() {
		String[] tokens = getKey().split("/");
		if (tokens.length == 0) {
			throw new IllegalArgumentException("Cannot parse: " + getKey());
		}
		return tokens[0];
	}

	public String getObjectPath() {
		String key = getKey();
		int firstSlash = key.indexOf('/');
		if (firstSlash == -1)
			return null;
		return key.substring(firstSlash + 1);
	}
}
