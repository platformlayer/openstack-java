package org.openstack.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.List;

import org.openstack.utils.Io;

import com.google.common.collect.Lists;

public class KeyStoreUtils {
	public static KeyStore load(File keystoreFile, String keystoreSecret) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		KeyStore keystore;
		InputStream is = null;

		try {
			keystore = KeyStore.getInstance("JKS");
			is = new FileInputStream(keystoreFile);
			keystore.load(is, keystoreSecret.toCharArray());
		} finally {
			Io.safeClose(is);
		}

		return keystore;
	}

	public static List<String> getAliases(KeyStore keystore) throws KeyStoreException {
		List<String> ret = Lists.newArrayList();

		Enumeration<String> aliases = keystore.aliases();
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			ret.add(alias);
		}

		return ret;
	}
}
