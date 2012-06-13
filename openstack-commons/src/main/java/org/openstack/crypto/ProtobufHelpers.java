package org.openstack.crypto;

public class ProtobufHelpers {
	public static com.google.protobuf.ByteString toProtobuf(ByteString b) {
		return com.google.protobuf.ByteString.copyFrom(b.bytes);
	}
}
