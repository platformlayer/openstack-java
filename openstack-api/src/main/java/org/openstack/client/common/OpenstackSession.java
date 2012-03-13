package org.openstack.client.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.openstack.client.OpenstackCredentials;
import org.openstack.client.OpenstackException;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.atom.Link;
import org.openstack.model.compute.Flavor;
import org.openstack.model.compute.Image;
import org.openstack.model.compute.Server;
import org.openstack.model.identity.Access;
import org.openstack.model.identity.Service;
import org.openstack.model.identity.Access.Token;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class OpenstackSession implements Serializable {
	static final Logger log = Logger.getLogger(OpenstackSession.class.getName());

	public final Map<Object, Object> extensions = Maps.newHashMap();
	
	public enum Feature {

		VERBOSE(false), FORCE_JSON(false), FORCE_XML(false);

		private boolean enabled;

		public boolean isEnabled() {
			return enabled;
		}

		public int mask() {
			return (1 << ordinal());
		}

		private Feature(boolean enabled) {
			this.enabled = enabled;
		}
	}

	private int features;

	private OpenStackIdentityConfig identityConfig;

	private OpenStackComputeConfig computeConfig;

	private OpenStackImageConfig imageConfig;

	Access access;

	transient LinkResolver linkResolver;

	private OpenstackCredentials credentials;

	public OpenstackSession() {

		// calculate the bitmap
		for (Feature f : Feature.class.getEnumConstants()) {
			if (f.isEnabled()) {
				features = features | f.mask();
			}
		}

		computeConfig = new OpenStackComputeConfig();

		identityConfig = new OpenStackIdentityConfig();

		imageConfig = new OpenStackImageConfig();

	}

	public OpenstackSession enable(Feature feature) {
		features = features | feature.mask();
		return this;
	}

	public OpenstackSession disable(Feature feature) {
		features = features & ~feature.mask();
		return this;
	}

	public OpenStackIdentityConfig getIdentityConfig() {
		return identityConfig;
	}

	public void setIdentityConfig(OpenStackIdentityConfig identityConfig) {
		this.identityConfig = identityConfig;
	}

	public OpenStackComputeConfig getComputeConfig() {
		return computeConfig;
	}

	public void setComputeConfig(OpenStackComputeConfig computeConfig) {
		this.computeConfig = computeConfig;
	}

	public OpenStackImageConfig getImageConfig() {
		return imageConfig;
	}

	public void setImageConfig(OpenStackImageConfig imageConfig) {
		this.imageConfig = imageConfig;
	}

	public boolean isEnabled(Feature feature) {
		return (features & feature.mask()) == 1;
	}

	public OpenstackSession with(Feature... features) {
		for (Feature feature : features) {
			this.features = this.features | feature.mask();
		}
		return this;
	}

	public OpenstackSession without(Feature... features) {
		for (Feature feature : features) {
			this.features = this.features & ~feature.mask();
		}
		return this;
	}

	//

	/*
	 * public OpenstackSession(String authURL) { this.authenticationUrl = authURL; }
	 * 
	 * public OpenstackSession(String authUrl, OpenstackCredentials credentials) { this(authUrl);
	 * authenticate(credentials); }
	 */

	public boolean isAuthenticated() {
		return access != null;
	}

	public synchronized Access getAuthenticationToken() throws OpenstackException {
		// TODO: Allow saving credentials and automatically re-authenticate on timeout
		// if (access == null)
		// access = getAuthenticationClient().authenticate();
		return access;
	}

	public RequestBuilder resource(String resourceUrl) {
		RequestBuilder request = createRequestBuilder(resourceUrl);

		if (isEnabled(Feature.VERBOSE)) {
			request.setVerbose(true);
		}

		Token token = null;
		if (access != null) {
			token = access.getToken();
		}

		String authTokenId = null;
		if (token != null) {
			authTokenId = token.getId();
		}

		if (authTokenId != null && !authTokenId.isEmpty()) {
			request.putHeader("X-Auth-Token", authTokenId);
		}

		return request;
	}

	protected abstract RequestBuilder createRequestBuilder(String resourceUrl);

	public OpenstackImageClient getImageClient() {
		return new OpenstackImageClient(this);
	}

	public OpenstackComputeClient getComputeClient() {
		return new OpenstackComputeClient(this);
	}

	public OpenstackAuthenticationClient getAuthenticationClient() {
		return new OpenstackAuthenticationClient(this);
	}

	public OpenstackStorageClient getStorageClient() {
		return new OpenstackStorageClient(this);
	}

	public void authenticate(OpenstackCredentials credentials) throws OpenstackException {
		authenticate(credentials, false);
	}

	public void authenticate(OpenstackCredentials credentials, boolean storeCredentials) throws OpenstackException {
		if (storeCredentials) {
			this.credentials = credentials;
		}
		this.access = null;
		Access access = getAuthenticationClient().authenticate(credentials);
		this.access = access;
	}

	public String getBestEndpoint(String serviceType) throws OpenstackException {
		Access access = getAuthenticationToken();
		List<Service> foundServices = Lists.newArrayList();
		Set<String> serviceTypes = Sets.newHashSet();
		for (Service service : access.getServiceCatalog()) {
			serviceTypes.add(service.getType());

			if (serviceType.equals(service.getType())) {
				foundServices.add(service);
			}
		}

		if (foundServices.isEmpty()) {
			throw new OpenstackException("Cannot find service: " + serviceType + ".  Available services: "
					+ Joiner.on(",").join(serviceTypes));
		}

		Service service;
		if (foundServices.size() != 1) {
			log.fine("Found multiple services of type: " + serviceType + ".  Found: "
					+ Joiner.on(',').join(foundServices));
			service = pickBest(foundServices);
		} else {
			service = foundServices.get(0);
		}

		if (service.getEndpoints().size() != 1) {
			throw new OpenstackException("Unhandled number of endpoints");
		}

		String bestUrl = service.getEndpoints().get(0).getPublicURL();

		if (bestUrl == null) {
			throw new OpenstackException("Cannot find endpoint URL for image service");
		}

		return bestUrl;
	}

	private Service pickBest(List<Service> services) {
		Function<Service, Float> scoreFunction = new Function<Service, Float>() {
			@Override
			public Float apply(Service s) {
				float score = 0;
				if (s.getName().endsWith("OpenStack")) {
					score += 10;
				}
				if (s.getName().endsWith("CDN")) {
					score -= 10;
				}
				return score;
			}
		};

		Float bestScore = null;
		Service best = null;
		for (Service candidate : services) {
			Float score = scoreFunction.apply(candidate);
			if (bestScore == null || bestScore.floatValue() < score.floatValue()) {
				bestScore = score;
				best = candidate;
			} else if (bestScore.floatValue() == score.floatValue()) {
				throw new IllegalArgumentException("Cannot choose between candidates: " + best + " vs " + candidate);
			}
		}

		return best;
	}

	public <T> T followLink(Link link, Class<T> clazz) {
		return link.follow(this, null, clazz);
	}

	public LinkResolver getLinkResolver() {
		if (linkResolver == null) {
			linkResolver = new SimpleLinkResolver(this);
		}
		return linkResolver;
	}

	public void setLinkResolver(LinkResolver linkResolver) {
		this.linkResolver = linkResolver;
	}

	public MediaType getForceContentType() {
		if (isEnabled(Feature.FORCE_JSON)) {
			return MediaType.APPLICATION_JSON_TYPE;
		}
		if (isEnabled(Feature.FORCE_XML)) {
			return MediaType.APPLICATION_XML_TYPE;
		}
		return null;
	}

	public Image resolveImage(Image image) throws OpenstackException {
		if (image == null)
			return null;

		return getLinkResolver().resolveImage(image.getId(), image.getLinks());
	}

	public Flavor resolveFlavor(Flavor flavor) throws OpenstackException {
		if (flavor == null)
			return null;

		return getLinkResolver().resolveFlavor(flavor.getId(), flavor.getLinks());
	}

	public static OpenstackSession create() {
		return new JerseyOpenstackSession();
	}

	public boolean hasStoredCredentials() {
		return credentials != null;
	}

	public void reauthenticate() throws OpenstackException {
		if (credentials == null) {
			throw new IllegalStateException("Credentials were not saved");
		}
		authenticate(credentials, false);
	}
}
