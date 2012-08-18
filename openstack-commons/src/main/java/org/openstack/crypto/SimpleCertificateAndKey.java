package org.openstack.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class SimpleCertificateAndKey implements CertificateAndKey {

	final X509Certificate[] chain;
	final PrivateKey privateKey;

	public SimpleCertificateAndKey(X509Certificate[] chain, PrivateKey privateKey) {
		super();
		this.chain = chain;
		this.privateKey = privateKey;
	}

	@Override
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	@Override
	public X509Certificate[] getCertificateChain() {
		return chain;
	}

	@Override
	public PublicKey getPublicKey() {
		return chain[0].getPublicKey();
	}
}