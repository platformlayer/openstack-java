package org.openstack.model.identity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.Lists;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.NONE)
public class Service implements Serializable {

	@JsonProperty("endpoints")
	@XmlElement(nillable = true, name = "endpoint")
	private List<ServiceEndpoint> endpoints;

	// Not sure what these are...
	@JsonProperty("endpoints_links")
	private List<String> endpointsLinks;

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String type;

	@XmlElement
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Service [id=" + id + ", name=" + name + ", type=" + type + ", description=" + description
				+ ", endpoints=" + endpoints + "]";
	}

	public List<ServiceEndpoint> getEndpoints() {
		if (endpoints == null) {
			endpoints = Lists.newArrayList();
		}
		return endpoints;
	}

}
