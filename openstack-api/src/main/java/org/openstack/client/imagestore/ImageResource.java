package org.openstack.client.imagestore;

import java.io.InputStream;
import java.util.Map;

import org.openstack.client.OpenstackException;
import org.openstack.client.OpenstackNotFoundException;
import org.openstack.client.common.HeadResponse;
import org.openstack.client.common.RequestBuilder;
import org.openstack.model.image.Image;

public class ImageResource extends GlanceResourceBase {
    public void updateMetadata(Map<String, Object> metadata, boolean replace) {
    	RequestBuilder builder = resource();
        builder = GlanceHeaderUtils.setHeadersForProperties(builder, metadata);
        builder.put();
    }

    public Image show() throws OpenstackException {
        HeadResponse response = resource().head();
        int httpStatus = response.getStatus();
        if (httpStatus == 200) {
            Image image = GlanceHeaderUtils.unmarshalHeaders(response);
            return image;
        }

        if (httpStatus == 404) {
            throw new OpenstackNotFoundException("Image not found");
        }

        throw new OpenstackException("Unexpected HTTP status code: " + httpStatus);
    }

	public InputStream openStream() {
        return resource().get(InputStream.class);
    }

    public void delete() {
        resource().delete();
    }
}
