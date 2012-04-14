package org.openstack.model.compute.server.action;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "reboot")
@XmlAccessorType(XmlAccessType.NONE)
public class RebootAction implements Serializable {

	public enum RebootType {
		SOFT, HARD
	}

	@XmlAttribute(required = true)
	private RebootType type;

	public RebootType getType() {
		return type;
	}

	public void setType(RebootType type) {
		this.type = type;
	}

}
