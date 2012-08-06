package org.openstack.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

public class PropertyUtils {
	public static Properties loadProperties(File file) throws IOException {
		Properties properties = new Properties();
		loadProperties(properties, file);
		return properties;
	}

	public static void loadProperties(Properties properties, File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		try {
			properties.load(is);
		} finally {
			Io.safeClose(is);
		}
	}

	public static Properties getChildProperties(Properties base, String prefix) {
		Properties children = new Properties();

		for (Map.Entry<Object, Object> entry : base.entrySet()) {
			Object keyObject = entry.getKey();
			if (!(keyObject instanceof String)) {
				continue;
			}

			String key = (String) keyObject;
			if (!key.startsWith(prefix)) {
				continue;
			}

			String suffix = key.substring(prefix.length());
			children.put(suffix, entry.getValue());
		}

		return children;
	}

	public static void copyToMap(Properties properties, Map<String, String> dest) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			dest.put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	public static String serialize(Properties properties) throws IOException {
		StringWriter writer = new StringWriter();
		properties.store(writer, null);

		// The properties serialization normally puts a comment at the top with the date
		// That causes lots of false-positive changes; remove it
		return stripComments(writer.toString());
	}

	private static String stripComments(String s) {
		StringBuilder sb = new StringBuilder();
		for (String line : Splitter.on("\n").split(s)) {
			if (line.startsWith("#")) {
				continue;
			}
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}
}
