package org.openstack.client.transport;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.openstack.client.OpenstackAuthenticationException;
import org.openstack.client.OpenstackException;
import org.openstack.client.OpenstackForbiddenException;
import org.openstack.client.OpenstackNotFoundException;
import org.openstack.model.compute.BadRequest;
import org.openstack.model.compute.ItemNotFound;
import org.openstack.utils.Io;

public class ExceptionMapping {
	static final Logger log = Logger.getLogger(ExceptionMapping.class.getName());

	public static void mapResponse(Response response) {
		int httpStatus = response.getStatus();
		if (httpStatus == 404) {
			String message = "Not found";
			MediaType responseType = response.getType();

			if (responseType != null && responseType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
				try {
					// TODO(justinsb): This is only valid on compute (I think!)
					ItemNotFound itemNotFound = response.getEntity(ItemNotFound.class);
					if (itemNotFound.getMessage() != null) {
						message = itemNotFound.getMessage();
					}
				} catch (Exception e) {
					// Ignore
					log.log(Level.FINE, "Ignoring error deserializing ItemNotFound on 404", e);
				}
			} else if (responseType != null && responseType.isCompatible(MediaType.TEXT_HTML_TYPE)) {
				InputStream inputStream = null;
				try {
					inputStream = response.getEntityInputStream();
					message = Io.readAll(inputStream);
				} catch (Exception e) {
					// Ignore
					log.log(Level.FINE, "Ignoring error reading 404 response body", e);
				} finally {
					Io.safeClose(inputStream);
				}
			}

			throw new OpenstackNotFoundException(message);
		}

		if (httpStatus == 401) {
			String message = "Not authorized";

			throw new OpenstackAuthenticationException(message);
		}

		if (httpStatus == 403) {
			String message = "Forbidden";

			throw new OpenstackForbiddenException(message);
		}

		if (httpStatus == 400) {
			String message = "Bad request";
			try {
				// TODO(justinsb): This is only valid on compute (I think!)
				BadRequest badRequest = response.getEntity(BadRequest.class);
				if (badRequest.getMessage() != null) {
					message = badRequest.getMessage();
				}
			} catch (Exception e) {
				// Ignore
				log.log(Level.FINE, "Ignoring error deserializing BadRequest on 400", e);
			}

			throw new OpenstackException(message);
		}
	}
}
