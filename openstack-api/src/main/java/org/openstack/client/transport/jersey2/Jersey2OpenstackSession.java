package org.openstack.client.transport.jersey2;

import java.io.IOException;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Target;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.FilterContext;
import javax.ws.rs.ext.RequestFilter;
import javax.ws.rs.ext.ResponseFilter;

import org.glassfish.jersey.filter.LoggingFilter;
import org.openstack.client.common.HeadResponse;
import org.openstack.client.common.OpenstackSession;
import org.openstack.client.common.RequestBuilder;
import org.openstack.client.transport.AuthenticationToken;
import org.openstack.client.transport.ExceptionMapping;

public class Jersey2OpenstackSession extends OpenstackSession {
	public static final String KEY = "jersey2";
	
	private static final long serialVersionUID = 1L;

	@Override
	protected RequestBuilder createRequestBuilder(String resourceUrl) {
		return new JerseyRequestBuilder(resourceUrl);
	}

	public class JerseyRequestBuilder extends RequestBuilder {
		public JerseyRequestBuilder(String resourceUrl) {
			super(Jersey2OpenstackSession.this, resourceUrl);
		}

		@Override
		public <T> T doRequest0(Class<T> c) {
			Invocation.Builder builder = buildResource();

			Entity<?> bodyEntity = null;
			if (body != null) {
				if (contentType == null)
					throw new IllegalStateException();

				bodyEntity = Entity.entity(body, contentType);
			}

			if (c == Void.class) {
				if (bodyEntity != null) {
					builder.method(method, bodyEntity);
				} else {
					builder.method(method);
				}
				return null;
			} else {
				if (bodyEntity != null) {
					return builder.method(method, bodyEntity, c);
				} else {
					return builder.method(method, c);
				}
			}
		}

		private Invocation.Builder buildResource() {
			Client jerseyClient = Jersey2Client.INSTANCE.getJerseyClient();
			Invocation.Builder builder;
			{
				Target target = jerseyClient.target(resourceUrl);

				if (!queryParameters.isEmpty()) {
					MultivaluedHashMap<String, Object> queryParametersMap = new MultivaluedHashMap<String, Object>();
					for (Entry<String, String> entry : queryParameters.entries()) {
						queryParametersMap.add(entry.getKey(), entry.getValue());
					}
					target = target.queryParams(queryParametersMap);
				}

				if (verbose) {
					target.configuration().register(new LoggingFilter());
				}

				if (access != null) {
					target.configuration().register(new RequestFilter() {
						@Override
						public void preFilter(FilterContext context) throws IOException {
							String authToken = AuthenticationToken.getAuthenticationToken(access);
							if (authToken != null && !authToken.isEmpty()) {
								context.getRequestBuilder().header("X-Auth-Token", authToken);
							}
						}

					});
				}

				target.configuration().register(new ResponseFilter() {
					@Override
					public void postFilter(FilterContext context) throws IOException {
						Response response = context.getResponse();

						ExceptionMapping.mapResponse(new ResponseAdapter(response));
					}
				});

				MediaType[] acceptTypesArray = (MediaType[]) acceptTypes.toArray(new MediaType[acceptTypes.size()]);
				builder = target.request(acceptTypesArray);
			}

			for (Entry<String, String> entry : headers.entrySet()) {
				builder = builder.header(entry.getKey(), entry.getValue());
			}

			return builder;
		}

		@Override
		public HeadResponse head() {
			Invocation.Builder builder = buildResource();

			Response head = builder.head();
			return new HeadResponse(head.getStatus(), head.getMetadata());
		}

	}

}
