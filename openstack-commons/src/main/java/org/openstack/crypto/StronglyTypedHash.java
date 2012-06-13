package org.openstack.crypto;

public abstract class StronglyTypedHash extends ByteString {

	protected StronglyTypedHash(byte[] hash) {
		super(hash);
	}

}
