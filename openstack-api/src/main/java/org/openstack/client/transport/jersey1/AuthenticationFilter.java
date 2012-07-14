package org.openstack.client.transport.jersey1;

import java.util.logging.Logger;

import org.openstack.model.identity.Access;
import org.openstack.model.identity.Access.Token;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

class AuthenticationFilter extends ClientFilter {
	static final Logger log = Logger.getLogger(AuthenticationFilter.class.getName());

	final Access access;

	public AuthenticationFilter(Access access) {
		this.access = access;
	}

	@Override
	public ClientResponse handle(ClientRequest request) {
		String authTokenId = getAuthenticationToken(access);

		if (authTokenId != null && !authTokenId.isEmpty()) {
			request.getHeaders().putSingle("X-Auth-Token", authTokenId);
		}

		ClientResponse response = getNext().handle(request);
		return response;
	}

	public static String getAuthenticationToken(Access access) {
		Token token = null;
		if (access != null) {
			token = access.getToken();
		}

		String authTokenId = null;
		if (token != null) {
			authTokenId = token.getId();
		}

		return authTokenId;
	}

}
