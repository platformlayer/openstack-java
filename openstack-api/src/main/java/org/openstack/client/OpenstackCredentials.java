package org.openstack.client;

import java.io.Serializable;
import java.util.Properties;

public class OpenstackCredentials implements Serializable {
	private static final long serialVersionUID = 1L;

	final String authUrl;
	final String username;
	final String secret;
	final String tenant;

	public OpenstackCredentials(String authUrl, String username, String secret, String tenant) {
		this.authUrl = authUrl;
		this.username = username;
		this.secret = secret;
		this.tenant = tenant;
	}

	public static OpenstackCredentials loadFromProperties(Properties properties) {
		String authUrl = properties.getProperty(OpenstackProperties.AUTH_URL);
		String username = properties.getProperty(OpenstackProperties.AUTH_USER);
		String password = properties.getProperty(OpenstackProperties.AUTH_SECRET);
		String tenant = properties.getProperty(OpenstackProperties.AUTH_TENANT);

		return new OpenstackCredentials(authUrl, username, password, tenant);
	}

	public String getUsername() {
		return username;
	}

	public String getSecret() {
		return secret;
	}

	public String getTenant() {
		return tenant;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public OpenstackCredentials withTenant(String tenant) {
		return new OpenstackCredentials(tenant, username, secret, tenant);
	}
}
