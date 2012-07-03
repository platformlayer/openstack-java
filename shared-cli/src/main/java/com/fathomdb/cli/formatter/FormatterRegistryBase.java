package com.fathomdb.cli.formatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fathomdb.cli.discovery.Discovery;

public class FormatterRegistryBase implements FormatterRegistry {
	final Map<Class<?>, Formatter> registry = new HashMap<Class<?>, Formatter>();

	@Override
	public Formatter getFormatter(Class<?> clazz) {
		Formatter formatter = registry.get(clazz);
		if (formatter != null) {
			return formatter;
		}

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			formatter = registry.get(interfaceClass);
			if (formatter != null) {
				return formatter;
			}
		}

		return null;
	}

	protected void addDefaultFormatters() {
		addFormatter(new StringFormatter());
		addFormatter(new ClientActionDefaultFormatter());
	}

	protected void addFormatter(Formatter formatter) {
		for (Class<?> command : formatter.getHandledClasses()) {
			registry.put(command, formatter);
		}
	}

	protected void discoverFormatters(Package package1) {
		Discovery discovery = new Discovery();
		List<Class<?>> classes = discovery.findClasses(getClass().getPackage());
		List<Formatter> formatters = discovery.buildInstances(Formatter.class, classes);
		for (Formatter formatter : formatters) {
			addFormatter(formatter);
		}
	}
}
