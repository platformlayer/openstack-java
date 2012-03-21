package org.openstack.client.transport;

import org.openstack.model.identity.Access;
import org.openstack.model.identity.Access.Token;

public class AuthenticationToken {
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
