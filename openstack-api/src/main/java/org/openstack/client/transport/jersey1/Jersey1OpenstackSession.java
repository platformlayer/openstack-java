package org.openstack.client.transport.jersey1;

import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.openstack.client.common.HeadResponse;
import org.openstack.client.common.OpenstackSession;
import org.openstack.client.common.RequestBuilder;
import org.openstack.client.transport.ExceptionMapping;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Jersey1OpenstackSession extends OpenstackSession {
	public static final String KEY = "jersey1";

	private static final long serialVersionUID = 1L;

	@Override
	protected RequestBuilder createRequestBuilder(String resourceUrl) {
		return new JerseyRequestBuilder(resourceUrl);
	}

	public class JerseyRequestBuilder extends RequestBuilder {
		public JerseyRequestBuilder(String resourceUrl) {
			super(Jersey1OpenstackSession.this, resourceUrl);
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
			Client jerseyClient = Jersey1Client.INSTANCE.getJerseyClient();
			Builder builder;
			{
				WebResource resource = jerseyClient.resource(resourceUrl);

				if (!queryParameters.isEmpty()) {
					MultivaluedMapImpl queryParametersMap = new MultivaluedMapImpl();
					for (Entry<String, String> entry : queryParameters.entries()) {
						queryParametersMap.add(entry.getKey(), entry.getValue());
					}
					resource = resource.queryParams(queryParametersMap);
				}

				if (verbose) {
					resource.addFilter(new LoggingFilter(System.out));
				}

				resource.addFilter(new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
						ClientResponse response = getNext().handle(cr);

						ExceptionMapping.mapResponse(new ResponseAdapter(response));

						return response;
					}
				});

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
