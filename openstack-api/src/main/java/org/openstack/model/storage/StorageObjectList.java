package org.openstack.model.storage;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.NONE)
public class StorageObjectList {
    @XmlElement
    @XmlElementWrapper(name = "objects")
    private List<StorageObject> objects;

    public List<StorageObject> getObjects() {
        return objects;
    }

    public void setObjects(List<StorageObject> objects) {
        this.objects = objects;
    }

}
