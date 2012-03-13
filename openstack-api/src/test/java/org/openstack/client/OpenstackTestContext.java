package org.openstack.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openstack.client.common.OpenstackSession;
import org.openstack.utils.Io;

public class OpenstackTestContext {

	public OpenstackSession session;
	private boolean glanceEnabled;
	private boolean swiftEnabled;

	public OpenstackSession connect(OpenstackCredentials credentials, String format, boolean verbose) {
		session = OpenstackSession.create();
		if (verbose) {
			session.with(OpenstackSession.Feature.VERBOSE);
		}

		if (format != null) {
			if (format.equals("json")) {
				session.with(OpenstackSession.Feature.FORCE_JSON);
			} else if (format.equals("xml")) {
				session.with(OpenstackSession.Feature.FORCE_XML);
			} else {
				throw new IllegalArgumentException("Unknown format: " + format);
			}
		}

		session.authenticate(credentials, false);
		return session;
	}

	public static OpenstackTestContext buildFromProperties() {
		Properties properties = new Properties();

		String configPath = System.getProperties().getProperty("openstack.config", null);
		if (configPath != null) {
			if (configPath.startsWith("~/")) {
				String home = System.getProperty("user.home");
				configPath = home + File.separator + configPath.substring(2);
			}
			File configFile = new File(configPath);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(configFile);
				properties.load(fis);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error loading config file: " + configPath, e);
			} finally {
				Io.safeClose(fis);
			}
		}

		// Command line properties should take precedence
		properties.putAll(System.getProperties());

		boolean verbose = Boolean.parseBoolean(properties.getProperty("openstack.debug", "true"));

		String url = properties.getProperty("openstack.auth.url", "http://127.0.0.1:5000/v2.0");
		String username = properties.getProperty("openstack.auth.user", "demo");
		String secret = properties.getProperty("openstack.auth.secret", "supersecret");
		String tenant = properties.getProperty("openstack.auth.tenant", "demo");

		String format = properties.getProperty("openstack.format", null);

		boolean glanceEnabled = Boolean.parseBoolean(properties.getProperty("openstack.glance", "true"));
		boolean swiftEnabled = Boolean.parseBoolean(properties.getProperty("openstack.swift", "true"));

		OpenstackTestContext context = new OpenstackTestContext();
		context.glanceEnabled = glanceEnabled;
		context.swiftEnabled = swiftEnabled;

		OpenstackCredentials credentials = new OpenstackCredentials(url, username, secret, tenant);
		context.connect(credentials, format, verbose);
		return context;
	}

	public boolean isGlanceEnabled() {
		return glanceEnabled;
	}

	public boolean isSwiftEnabled() {
		return swiftEnabled;
	}
}
