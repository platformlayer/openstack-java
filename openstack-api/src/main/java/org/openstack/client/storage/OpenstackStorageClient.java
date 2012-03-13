package org.openstack.client.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.openstack.client.OpenstackException;
import org.openstack.client.common.OpenstackSession;
import org.openstack.model.storage.ObjectProperties;
import org.openstack.model.storage.StorageObject;
import org.openstack.utils.Io;

public class OpenstackStorageClient {

	final OpenstackSession session;
	AccountResource root;

	public OpenstackStorageClient(OpenstackSession session) {
		this.session = session;
		root();
	}

	public synchronized AccountResource root() {
		if (root == null) {
			String endpoint = session.getBestEndpoint("object-store");

			root = new AccountResource(session, endpoint);
		}

		return root;
	}

	public OpenstackSession getSession() {
		return session;
	}
	
	public ObjectProperties putObject(String containerName, String objectName, File file) throws OpenstackException, IOException {
		ObjectProperties properties = new ObjectProperties();
		properties.setName(objectName);
		return root().containers().id(containerName).objects().putObject(file, properties);
	}

	public ObjectProperties putObject(String containerName, String objectName, InputStream objectData, long contentLength) throws OpenstackException, IOException {
		ObjectProperties properties = new ObjectProperties();
		properties.setName(objectName);
		return root().containers().id(containerName).objects().putObject(objectData, contentLength, properties);
	}
	
	public ObjectProperties getObject(String containerName, String objectName, File destFile) throws IOException {
		ObjectResource objectResource = buildObjectResource(containerName, objectName);

		// TODO: It would be nicer to get this in one call
		ObjectProperties metadata = objectResource.metadata();

		InputStream objectStream = objectResource.openStream();
		try {
			Io.copyStreams(objectStream, destFile);
		} finally {
			Io.safeClose(objectStream);
		}
		
		return metadata;
	}

	public void deleteObject(String containerName, String objectName) {
		ObjectResource objectResource = buildObjectResource(containerName, objectName);
		objectResource.delete();
	}

	public ObjectProperties getObjectDetails(String containerName, String objectName) {
		ObjectResource objectResource = buildObjectResource(containerName, objectName);
		return objectResource.metadata();
	}

	public InputStream getDataInputStream(String containerName, String objectName) {
		ObjectResource objectResource = buildObjectResource(containerName, objectName);
		return objectResource.openStream();
	}

	private ObjectResource buildObjectResource(String containerName, String objectName) {
		ObjectResource objectResource = root().containers().id(containerName).objects().id(objectName);
		return objectResource;
	}

	public Iterable<StorageObject> listObjects(String containerName, String objectNamePrefix, String delimiter) {
		return root().containers().id(containerName).objects().list(objectNamePrefix, delimiter);
	}
}
