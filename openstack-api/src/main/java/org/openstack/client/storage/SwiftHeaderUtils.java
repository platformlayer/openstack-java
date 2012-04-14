package org.openstack.client.storage;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.openstack.client.common.HeadResponse;
import org.openstack.client.common.RequestBuilder;
import org.openstack.client.internals.SimpleClassInfo;
import org.openstack.client.internals.SimpleClassInfo.FieldInfo;
import org.openstack.model.storage.ContainerProperties;
import org.openstack.model.storage.ObjectProperties;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

class SwiftHeaderUtils {
	static final Logger log = Logger.getLogger(SwiftHeaderUtils.class.getName());

	// Guava has an issue with FinalizableReferenceQueue keeping ClassLoaders around...
	// http://code.google.com/p/guava-libraries/issues/detail?id=92
	// That has taken 4 years to fix (sigh)
	// Which means that someone shipped a patched version called sisu-guava,
	// but they didn't change the namespace (sigh)
	// And the signature changed so we get method-not-found errors at runtime (sigh)
	// So we have to use the obsolete cache (sigh)
	/*
	 * static final LoadingCache<Class<?>, SimpleClassInfo> CLASS_INFO = CacheBuilder.newBuilder().build( new
	 * CacheLoader<Class<?>, SimpleClassInfo>() {
	 * 
	 * @Override public SimpleClassInfo load(Class<?> c) throws Exception { return new SimpleClassInfo(c); } });
	 */
	static final ConcurrentMap<Class<?>, SimpleClassInfo> CLASS_INFO = new MapMaker()
			.makeComputingMap(new Function<Class<?>, SimpleClassInfo>() {
				@Override
				public SimpleClassInfo apply(Class<?> c) {
					return new SimpleClassInfo(c);
				}
			});

	public static void unmarshalHeaders(HeadResponse response, Object properties, Map<String, String> customProperties,
			String keyPrefix) {
		for (Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			if (values.size() != 1) {
				throw new IllegalStateException();
			}
			String value = values.get(0);
			key = key.toLowerCase();

			SimpleClassInfo headerClassInfo = getClassInfo(properties.getClass());

			String userPropertyPrefix = keyPrefix + "-meta-";
			if (key.startsWith(userPropertyPrefix)) {
				String name = key.substring(userPropertyPrefix.length());
				customProperties.put(name, value);
			} else {
				FieldInfo field = headerClassInfo.getField(key);

				if (field == null) {
					log.fine("Ignoring unknown header " + key);
					continue;
				}

				Object converted = field.convertToValue(value);
				field.setValue(properties, converted);
			}
		}
	}

	private static SimpleClassInfo getClassInfo(Class<? extends Object> c) {
		try {
			return CLASS_INFO.get(c);
			// TODO: Switch to ExecutionException when LoadingCache gets figured out
		} catch (Throwable e) {
			throw new IllegalStateException("Error reflecting on class info", e);
		}
	}

	public static ObjectProperties unmarshalObjectHeaders(HeadResponse response) {
		ObjectProperties properties = new ObjectProperties();
		Map<String, String> customProperties = properties.getCustomProperties();

		unmarshalHeaders(response, properties, customProperties, "x-object");

		return properties;
	}

	public static ContainerProperties unmarshalContainerHeaders(HeadResponse response) {
		ContainerProperties properties = new ContainerProperties();
		Map<String, String> customProperties = properties.getCustomProperties();

		unmarshalHeaders(response, properties, customProperties, "x-container");

		return properties;
	}

	public static RequestBuilder setHeadersForProperties(RequestBuilder builder, ObjectProperties changeProperties) {
		for (Map.Entry<String, String> tag : changeProperties.getCustomProperties().entrySet()) {
			builder.putHeader("x-object-meta-" + tag.getKey(), tag.getValue());
		}
		return builder;
	}

}
