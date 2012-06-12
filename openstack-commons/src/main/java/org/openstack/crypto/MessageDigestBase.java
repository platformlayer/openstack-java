package org.openstack.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openstack.utils.Io;
import org.openstack.utils.Utf8;

public abstract class MessageDigestBase<T extends StronglyTypedHash> {
	public T hash(String a) {
		return hash(Utf8.getBytes(a));
	}

	public T hash(byte[] data) {
		MessageDigest digest = buildDigest();

		byte[] hash = digest.digest(data);
		return wrap(hash);
	}

	protected abstract T wrap(byte[] hash);

	public T hash(File source) throws IOException {
		FileInputStream fis = new FileInputStream(source);
		try {
			return hash(fis);
		} finally {
			Io.safeClose(fis);
		}
	}

	public T hash(InputStream is) throws IOException {
		MessageDigest digest = buildDigest();

		byte[] buffer = new byte[8192];
		while (true) {
			int available = is.read(buffer);
			if (available == -1) {
				break;
			}
			digest.update(buffer, 0, available);
		}
		byte[] hash = digest.digest();
		return wrap(hash);

	}

	protected abstract MessageDigest buildDigest();

	public static MessageDigest buildDigest(String name) {
		try {
			MessageDigest digest = MessageDigest.getInstance(name);
			return digest;
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			throw new IllegalStateException("Could not find message digest algorithm: " + name, e);
		}
	}

}
