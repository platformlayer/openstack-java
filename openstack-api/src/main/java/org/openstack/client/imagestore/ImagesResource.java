package org.openstack.client.imagestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.openstack.client.OpenstackException;
import org.openstack.client.common.RequestBuilder;
import org.openstack.client.common.SimplePagingList;
import org.openstack.model.image.Image;
import org.openstack.model.image.ImageList;
import org.openstack.model.image.ImageUploadResponse;

import com.google.common.io.Closeables;

public class ImagesResource extends GlanceResourceBase {

	public Iterable<Image> list() {
		return list(true);
	}

	public Iterable<Image> list(boolean details) {
		RequestBuilder imagesResource = details ? resource("detail") : resource();

		ImageList page = imagesResource.get(ImageList.class);
		return new SimplePagingList<Image>(session, page);
	}

	public ImageResource image(String imageId) {
		return buildChildResource(imageId, ImageResource.class);
	}

	public Image addImage(File imageFile, Image properties) throws IOException, OpenstackException {
		FileInputStream fis = new FileInputStream(imageFile);
		try {
			return addImage(fis, imageFile.length(), properties);
		} finally {
			Closeables.closeQuietly(fis);
		}
	}

	public Image addImage(InputStream imageStream, long imageStreamLength, Image properties) throws OpenstackException,
			IOException {
		RequestBuilder builder = resource(null, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		builder = GlanceHeaderUtils.setHeaders(builder, properties);

		if (imageStreamLength != -1) {
			properties.setSize(imageStreamLength);

			imageStream = new KnownLengthInputStream(imageStream, imageStreamLength);
		}

		ImageUploadResponse response = builder.post(ImageUploadResponse.class, imageStream);
		Image image = response.getImage();
		return image;
	}

}
