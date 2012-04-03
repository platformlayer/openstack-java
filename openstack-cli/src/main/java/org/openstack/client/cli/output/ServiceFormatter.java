package org.openstack.client.cli.output;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.openstack.model.identity.Service;
import org.openstack.model.identity.ServiceEndpoint;

import com.fathomdb.cli.formatter.SimpleFormatter;
import com.fathomdb.cli.output.OutputSink;
import com.google.common.collect.Maps;

public class ServiceFormatter extends SimpleFormatter<Service> {

	public ServiceFormatter() {
		super(Service.class);
	}

	@Override
	public void visit(Service o, OutputSink sink) throws IOException {
		LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();

		values.put("type", o.getType());
		values.put("name", o.getName());
		values.put("endpoints", formatEndpoints(o.getEndpoints()));

		sink.outputRow(values);
	}

	public static String formatEndpoints(List<ServiceEndpoint> endpoints) {
		StringBuilder sb = new StringBuilder();
		formatEndpoints(sb, endpoints);
		return sb.toString();
	}

	public static void formatEndpoints(StringBuilder sb, List<ServiceEndpoint> endpoints) {
		for (int i = 0; i < endpoints.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			formatEndpoint(sb, endpoints.get(i));
		}
	}

	public static void formatEndpoint(StringBuilder sb, ServiceEndpoint serviceEndpoint) {
		sb.append("publicUrl=" + serviceEndpoint.getPublicURL());
		sb.append(" internalUrl=" + serviceEndpoint.getInternalURL());
		sb.append(" region=" + serviceEndpoint.getRegion());
		sb.append(" tenant=" + serviceEndpoint.getTenantId());
	}
}
