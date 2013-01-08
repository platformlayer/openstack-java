package org.openstack.client.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.openstack.client.OpenstackException;
import org.openstack.client.common.RequestBuilder;
import org.openstack.client.imagestore.KnownLengthInputStream;
import org.openstack.model.storage.ObjectProperties;
import org.openstack.model.storage.StorageObject;
import org.openstack.model.storage.StorageObjectList;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import com.sun.jersey.api.client.ClientResponse;

public class ObjectsResource extends StorageResourceBase {
	public Iterable<StorageObject> list() {
		return list(null, null);
	}

	public Iterable<StorageObject> list(String prefix, String delimiter) {
		return list(prefix, delimiter, true);
	}

	public Iterable<StorageObject> list(final String prefix, final String delimiter, final boolean fetchMetadata) {
		return new Iterable<StorageObject>() {
			@Override
			public Iterator<StorageObject> iterator() {
				return new FileIterator(prefix, delimiter, fetchMetadata);
			}
		};
	}

	class FileIterator implements Iterator<StorageObject> {
		final String prefix;
		final String delimiter;
		final boolean fetchMetadata;

		Queue<StorageObject> queue = null;

		String marker;
		boolean couldHaveMore = true;

		public FileIterator(String prefix, String delimiter, boolean fetchMetadata) {
			super();
			this.prefix = prefix;
			this.delimiter = delimiter;
			this.fetchMetadata = fetchMetadata;
		}

		@Override
		public boolean hasNext() {
			if (queue == null || queue.isEmpty()) {
				if (couldHaveMore) {
					queue = nextPage(marker);
				}
				if (queue == null || queue.isEmpty()) {
					queue = null;
				}
			}

			return (queue != null && !queue.isEmpty());
		}

		@Override
		public StorageObject next() {
			if (queue == null) {
				throw new IllegalStateException();
			}

			StorageObject so = queue.remove();
			marker = so.getName();
			return so;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		Queue<StorageObject> nextPage(String marker) {
			RequestBuilder requestBuilder = resource();

			if (prefix != null) {
				requestBuilder.addQueryParameter("prefix", prefix);
			}
			if (!Strings.isNullOrEmpty(delimiter)) {
				requestBuilder.addQueryParameter("delimiter", delimiter);
			}

			if (!Strings.isNullOrEmpty(marker)) {
				requestBuilder.addQueryParameter("marker", marker);
			}

			int limit = 10000;

			if (fetchMetadata) {
				requestBuilder.clearAcceptTypes();
				requestBuilder.addAcceptType(MediaType.APPLICATION_JSON_TYPE);

				StorageObjectList storageObjectList = requestBuilder.get(StorageObjectList.class);

				ArrayDeque<StorageObject> list = new ArrayDeque<StorageObject>();
				list.addAll(storageObjectList.getObjects());
				if (list.size() != limit) {
					couldHaveMore = false;
				}
				return list;
			} else {
				requestBuilder.clearAcceptTypes();
				requestBuilder.addAcceptType(MediaType.TEXT_PLAIN_TYPE);

				String listing = requestBuilder.get(String.class);
				ArrayDeque<StorageObject> list = new ArrayDeque<StorageObject>();
				for (String line : Splitter.on("\n").split(listing)) {
					if (line.isEmpty()) {
						continue;
					}

					StorageObject storageObject = new StorageObject();
					storageObject.setName(line);
					list.add(storageObject);
				}

				if (list.size() != limit) {
					couldHaveMore = false;
				}
				return list;
			}

		}
	}

	public void addObject(File imageFile, ObjectProperties properties) throws IOException, OpenstackException {
		FileInputStream fis = new FileInputStream(imageFile);
		try {
			putObject(fis, imageFile.length(), properties);
		} finally {
			Closeables.closeQuietly(fis);
		}
	}

	public ObjectProperties putObject(File srcFile, ObjectProperties properties) throws OpenstackException, IOException {
		FileInputStream fis = new FileInputStream(srcFile);
		try {
			return putObject(fis, srcFile.length(), properties);
		} finally {
			Closeables.closeQuietly(fis);
		}
	}

	public ObjectProperties putObject(InputStream objectStream, long objectStreamLength, ObjectProperties properties)
			throws OpenstackException, IOException {

		if (objectStreamLength != -1) {
			objectStream = new KnownLengthInputStream(objectStream, objectStreamLength);
		}

		RequestBuilder builder = buildPutRequest(properties);

		ClientResponse response = builder.put(ClientResponse.class, objectStream);
		MultivaluedMap<String, String> responseHeaders = response.getHeaders();

		ObjectProperties responseProperties = new ObjectProperties();
		String etag = responseHeaders.getFirst("ETag");

		if (etag != null) {
			responseProperties.setETag(etag);
		}
		return responseProperties;
	}

	public RequestBuilder buildPutRequest(ObjectProperties properties) {
		String name = properties.getName();
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Must set name");
		}

		String encoded = encodeForUrl(name);
		RequestBuilder builder = resource(encoded, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		if (properties.getContentType() != null) {
			MediaType contentType = MediaType.valueOf(properties.getContentType());
			builder.setContentType(contentType);
		} else {
			builder.clearContentType();
		}

		builder = SwiftHeaderUtils.setHeadersForProperties(builder, properties);

		builder.setMethod("PUT");
		return builder;
	}

	public ObjectResource id(String objectName) {
		return buildChildResource(objectName, ObjectResource.class);
	}

}
