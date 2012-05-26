package org.openstack.utils;

import java.security.MessageDigest;

public class Md5Hasher extends MessageDigestBase {

	@Override
	protected MessageDigest buildDigest() {
		return buildDigest("MD5");
	}

}
