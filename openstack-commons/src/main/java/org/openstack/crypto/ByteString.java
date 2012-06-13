package org.openstack.crypto;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.openstack.utils.Hex;

public class ByteString {
	final byte[] bytes;
	final int hash;

	public ByteString(byte[] bytes) {
		this.bytes = bytes;
		this.hash = Arrays.hashCode(bytes);
	}

	public int size() {
		return bytes.length;
	}

	public void put(ByteBuffer buffer) {
		if (bytes.length > Short.MAX_VALUE) {
			throw new IllegalStateException();
		}
		short keyLength = (short) bytes.length;
		buffer.putShort(keyLength);
		buffer.put(bytes);
	}

	public static ByteString get(ByteBuffer buffer) {
		int byteCount = buffer.getShort();
		if (byteCount <= 0) {
			throw new IllegalStateException("Corrupt byte count in entry");
		}
		byte[] keyData = new byte[byteCount];
		buffer.get(keyData);

		return new ByteString(keyData);
	}

	// TODO: Is this needed or does the JVM do this for us?
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ByteString)) {
			// getClass() != obj.getClass()) {
			// }
			return false;
		}
		ByteString other = (ByteString) obj;
		if (hash != other.hash) {
			return false;
		}
		if (!Arrays.equals(bytes, other.bytes)) {
			return false;
		}
		return true;
	}

	public String toHex() {
		return Hex.toHex(bytes);
	}

	@Override
	public String toString() {
		return "ByteString [" + Hex.toHex(bytes) + "]";
	}

}
