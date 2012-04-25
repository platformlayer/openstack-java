package org.openstack.client.transport.jersey1;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.openstack.client.imagestore.KnownLengthInputStream;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * A logging filter that can be turned off per request e.g. for image upload
 */
public class SelectiveLoggingFilter extends LoggingFilter {
	@Override
	public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
		Object entity = request.getEntity();
		if (entity != null) {
			if (entity instanceof KnownLengthInputStream) {
				long length = ((KnownLengthInputStream) entity).getLength();
				if (length > 32768) {
					logTruncatedRequest(0, request);

					return getNext().handle(request);
				}
			}
		}

		return super.handle(request);
	}

	// Grrr... we need to duplicate from the base class because it's all private
	public SelectiveLoggingFilter(PrintStream loggingStream) {
		super(loggingStream);
		this.loggingStream = loggingStream;
		this.logger = null;
	}

	private final PrintStream loggingStream;
	private final Logger logger;

	private void log(StringBuilder b) {
		if (logger != null) {
			logger.info(b.toString());
		} else {
			loggingStream.print(b);
		}
	}

	private void printRequestLine(StringBuilder b, long id, ClientRequest request) {
		// prefixId(b, id).append(NOTIFICATION_PREFIX).append("Client out-bound request").append("\n");
		// prefixId(b, id).append(REQUEST_PREFIX).append(request.getMethod()).append(" ").
		// append(request.getURI().toASCIIString()).append("\n");
		b.append(request.getMethod()).append(" ").append(request.getURI().toASCIIString()).append("\n");
	}

	private void logTruncatedRequest(long id, ClientRequest request) {
		StringBuilder b = new StringBuilder();

		printRequestLine(b, id, request);
		b.append("Request has big payload; truncating logging\n");
		// printRequestHeaders(b, id, request.getHeaders());
		//
		// if (request.getEntity() != null) {
		// request.setAdapter(new Adapter(request.getAdapter(), b));
		// } else {
		log(b);
		// }
	}

}
