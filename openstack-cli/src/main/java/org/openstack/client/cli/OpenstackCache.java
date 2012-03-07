package org.openstack.client.cli;

import java.util.List;
import java.util.Map;

import org.openstack.client.OpenstackException;
import org.openstack.client.OpenstackService;
import org.openstack.model.compute.Image;
import org.openstack.model.compute.SecurityGroup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OpenstackCache {

	private final OpenstackService service;

	public OpenstackCache(OpenstackService service) {
		this.service = service;
	}

	public Iterable<Image> getImages(boolean useCache) throws OpenstackException {
		return getCachedList(Image.class, useCache);
	}

	public <V> Iterable<V> listItems(Class<V> modelClass, boolean useCache) throws OpenstackException {
		return getCachedList(modelClass, useCache);
	}

	final Map<Class<?>, List<?>> cachedLists = Maps.newHashMap();

	private <V> Iterable<V> getCachedList(Class<V> modelClass, boolean useCache) throws OpenstackException {
		List<V> cached = useCache ? (List<V>) cachedLists.get(modelClass) : null;
		if (cached == null) {
			cached = (List<V>) Lists.newArrayList(service.listItems(modelClass, true));
			cachedLists.put(modelClass, cached);
		}
		return cached;
	}

	public Iterable<org.openstack.model.image.Image> getGlanceImages(boolean useCache) {
		return getCachedList(org.openstack.model.image.Image.class, useCache);
	}

	public void invalidateCache(Class<?> modelClass) {
		cachedLists.remove(modelClass);
	}

	public Iterable<SecurityGroup> getSecurityGroups(boolean useCache) {
		return getCachedList(SecurityGroup.class, useCache);
	}

}
