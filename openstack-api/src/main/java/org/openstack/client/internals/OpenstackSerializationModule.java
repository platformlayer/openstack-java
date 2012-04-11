package org.openstack.client.internals;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.openstack.model.common.ExtensionList;
import org.openstack.model.compute.Addresses;
import org.openstack.model.compute.BadRequest;
import org.openstack.model.compute.SecurityGroup;
import org.openstack.model.compute.SecurityGroupList;
import org.openstack.model.compute.Server;
import org.openstack.model.identity.Access;
import org.openstack.model.storage.StorageObjectList;

public class OpenstackSerializationModule extends SimpleModule {

	public OpenstackSerializationModule() {
		super(OpenstackSerializationModule.class.getName(), new Version(1, 0, 0, null));
		addSerializer(Addresses.class, new AddressesSerializer());
		addDeserializer(Addresses.class, new AddressesDeserializer());

		// Common
		installSmartDeserializer(ExtensionList.class);

		// Compute
		installSmartDeserializer(SecurityGroup.class);
		installSmartDeserializer(SecurityGroupList.class);
		installSmartDeserializer(Server.class);
		installSmartDeserializer(BadRequest.class);

		// Keystone (Redux)
		installSmartDeserializer(Access.class);
		
		// Storagea
		addDeserializer(StorageObjectList.class, new ArrayDeserializer<StorageObjectList>(StorageObjectList.class, "objects"));
	}

	private <T> void installSmartDeserializer(Class<T> c) {
		addDeserializer(c, new SmartDeserializer<T>(c));
	}
}
