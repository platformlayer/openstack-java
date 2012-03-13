package org.openstack.client.common;

import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyOpenstackSession extends OpenstackSession {

	private static final long serialVersionUID = 1L;

	@Override
	protected RequestBuilder createRequestBuilder(String resourceUrl) {
		return new JerseyRequestBuilder(resourceUrl);
	}

	public class JerseyRequestBuilder extends RequestBuilder {

		public JerseyRequestBuilder(String resourceUrl) {
			super(JerseyOpenstackSession.this, resourceUrl);
		}

		@Override
		public <T> T doRequest0(Class<T> c) {
			Builder builder = buildResource();

			if (c == Void.class) {
				if (body != null) {
					builder.method(method, body);
				} else {
					builder.method(method);
				}
				return null;
			} else {
				if (body != null) {
					return builder.method(method, c, body);
				} else {
					return builder.method(method, c);
				}
			}
		}

		private Builder buildResource() {
			Client jerseyClient = JerseyClient.INSTANCE.getJerseyClient();
			Builder builder;
			{
				WebResource resource = jerseyClient.resource(resourceUrl);

				if (!queryParameters.isEmpty()) {
					MultivaluedMapImpl queryParametersMap = new MultivaluedMapImpl();
					for (Entry<String, String> entry : queryParameters.entries()) {
						queryParametersMap.add(entry.getKey(), entry.getValue());
					}
					resource = resource.queryParams(queryParametersMap );
				}

				if (verbose) {
					resource.addFilter(new LoggingFilter(System.out));
				}

				resource.addFilter(new OpenstackExceptionClientFilter());

				builder = resource.getRequestBuilder();
			}

			for (Entry<String, String> entry : headers.entrySet()) {
				builder = builder.header(entry.getKey(), entry.getValue());
			}

			for (MediaType accept : acceptTypes) {
				builder = builder.accept(accept);
			}

			if (contentType != null) {
				builder = builder.type(contentType);
			}

			return builder;
		}

		@Override
		public HeadResponse head() {
			Builder builder = buildResource();

			ClientResponse head = builder.head();
			return new HeadResponse(head.getStatus(), head.getHeaders());
		}

	}

}
