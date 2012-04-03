package org.openstack.client.identity;

import java.util.List;

import org.openstack.client.common.OpenstackSession;
import org.openstack.model.identity.User;
import org.openstack.model.identity.UserList;

public class UsersRepresentation {

	private OpenstackSession client;

	private UserList model;

	public UsersRepresentation(OpenstackSession client, UserList model) {
		this.client = client;
		this.model = model;
	}

	public List<User> getList() {
		return model.getList();
	}

	public UsersRepresentation next() {
		UserList userList = client.resource(model.getLinks().get(0).getHref()).get(UserList.class);
		return new UsersRepresentation(client, userList);
	}

}
