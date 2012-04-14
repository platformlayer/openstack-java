package org.openstack.client;

import org.openstack.model.compute.Server;

/**
 * Attempts to abstract away the raw instance state values
 * 
 * @author justinsb
 * 
 */
public class InstanceState {
	private final String key;

	public static InstanceState get(String key) {
		return new InstanceState(key);
	}

	public static InstanceState get(Server server) {
		return get(server.getStatus());
	}

	private InstanceState(String key) {
		this.key = key;

	}

	public boolean isStarting() {
		return key.equals("BUILD");
	}

	public boolean isActive() {
		return key.equals("ACTIVE");
	}

	@Override
	public String toString() {
		return key;
	}

	public boolean isTerminated() {
		// TODO: Is this the right state?
		return key.equals("DELETED");
	}

	public boolean isTerminating() {
		// TODO: Is this the right state?
		return key.equals("DELETING");
	}

}
