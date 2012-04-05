package org.openstack.client;

import java.util.List;
import org.openstack.client.common.OpenstackComputeClient;
import org.openstack.client.common.OpenstackSession;
import org.openstack.model.common.Extension;
import com.google.common.collect.Lists;

/**
 * Different clouds support different operations. This class tries to help determine the capabilities of a cloud.
 */
public class CloudCapabilities {
    private final OpenstackSession session;

    public CloudCapabilities(OpenstackSession session) {
        this.session = session;
    }

    class CachedInfo {
        private final List<Extension> extensions;

        public CachedInfo(List<Extension> extensions) {
            this.extensions = extensions;
        }

        public boolean supportsExtension(String namespace) {
            for (Extension extension : extensions) {
                if (namespace.equals(extension.getNamespace()))
                    return true;
            }
            return false;
        }
    }

    CachedInfo cachedInfo = null;

    CachedInfo getCachedInfo() {
        // TODO: Should we have a long-lived cache?
        CachedInfo info = cachedInfo;
        if (info == null) {
            OpenstackComputeClient compute = session.getComputeClient();
            List<Extension> extensions = Lists.newArrayList(compute.root().extensions().list());
            info = new CachedInfo(extensions);
            cachedInfo = info;
        }
        return info;
    }

    public boolean supportsSshKeys() {
        return supportsExtension("http://docs.openstack.org/ext/keypairs/api/v1.1")
                || supportsExtension("http://docs.openstack.org/compute/ext/keypairs/api/v1.1");
    }

    public boolean supportsSecurityGroups() {
        return supportsExtension("http://docs.openstack.org/ext/securitygroups/api/v1.1")
                || supportsExtension("http://docs.openstack.org/compute/ext/securitygroups/api/v1.1");
    }

    private boolean supportsExtension(String namespace) {
        return getCachedInfo().supportsExtension(namespace);
    }
}
