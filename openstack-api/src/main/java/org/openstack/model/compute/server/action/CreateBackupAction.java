package org.openstack.model.compute.server.action;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openstack.model.compute.Metadata;

@XmlRootElement(name = "createBackup")
@XmlAccessorType(XmlAccessType.NONE)
public class CreateBackupAction implements Serializable {

	@XmlAttribute
	private String name;

	@XmlAttribute(name = "backup_type")
	private String type;

	@XmlAttribute
	private String rotation;

	@XmlElement
	private Metadata metadata;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRotation() {
		return rotation;
	}

	public void setRotation(String rotation) {
		this.rotation = rotation;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "CreateBackupAction [name=" + name + ", type=" + type + ", rotation=" + rotation + ", metadata="
				+ metadata + "]";
	}

}
