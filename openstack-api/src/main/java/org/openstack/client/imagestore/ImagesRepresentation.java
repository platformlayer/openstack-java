package org.openstack.client.imagestore;

import org.openstack.client.common.OpenstackSession;
import org.openstack.client.common.PagingList;
import org.openstack.model.common.PagingListBase;
import org.openstack.model.image.Image;

public class ImagesRepresentation extends PagingList<Image, ImageRepresentation> {
    public ImagesRepresentation(OpenstackSession client, PagingListBase<Image> firstPage) {
        super(client, firstPage);
    }

    @Override
    protected ImageRepresentation mapToRepresentation(Image model) {
        return new ImageRepresentation(client, model);
    }

}
