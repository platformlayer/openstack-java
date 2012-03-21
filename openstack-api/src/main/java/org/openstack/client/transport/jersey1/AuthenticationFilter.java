package org.openstack.client.transport.jersey1;

import java.util.logging.Logger;

import org.openstack.client.transport.AuthenticationToken;
import org.openstack.model.identity.Access;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

class AuthenticationFilter extends ClientFilter {
	static final Logger log = Logger.getLogger(AuthenticationFilter.class.getName());

	final Access access;

	public AuthenticationFilter(Access access) {
		this.access = access;
	}

	public ClientResponse handle(ClientRequest request) {
		String authTokenId = AuthenticationToken.getAuthenticationToken(access);

		if (authTokenId != null && !authTokenId.isEmpty()) {
			request.getHeaders().putSingle("X-Auth-Token", authTokenId);
		}

		ClientResponse response = getNext().handle(request);
		return response;
	}
}
