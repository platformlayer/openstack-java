package org.openstack.client.ssl;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class SSL {
	static final SSLContext context;

	static {
		try {
			context = SSLContext.getInstance("TLS");
			SecureRandom random = null;
			KeyManager[] km = null;
			TrustManager[] tm = { new AcceptAllServerCertificatesTrustManager() };
			context.init(km, tm, random);
		} catch (Exception e) {
			throw new IllegalStateException("Error initializing SSL", e);
		}
	}

	public static SSLContext getContext() {
		return context;
	}

	public static HostnameVerifier getHostnameVerifier() {
		return new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	}

}
