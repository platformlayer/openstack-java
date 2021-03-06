package org.openstack.client.cli.autocomplete;

import java.util.List;

import org.openstack.client.cli.OpenstackCliContext;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.storage.Container;
import org.openstack.model.storage.StorageObject;

import com.fathomdb.cli.CliContext;
import com.fathomdb.cli.autocomplete.SimpleArgumentAutoCompleter;
import com.google.common.collect.Lists;

public class StoragePathAutoCompleter extends SimpleArgumentAutoCompleter {

	@Override
	public List<String> doComplete(CliContext context, String prefix) throws Exception {
		List<String> strings = Lists.newArrayList();

		OpenstackCliContext osContext = (OpenstackCliContext) context;
		OpenstackStorageClient client = osContext.getStorageClient();

		if (!prefix.contains("/")) {
			Iterable<Container> items = client.root().containers().list();
			for (Container item : items) {
				strings.add(item.getName());
			}
			addSuffix(strings, "/");
			return strings;
		} else {
			String[] pathTokens = prefix.split("/");
			if (pathTokens.length == 1 || pathTokens.length == 2) {
				Iterable<StorageObject> items = client.root().containers().id(pathTokens[0]).objects().list();
				for (StorageObject item : items) {
					strings.add(item.getName());
				}

				addPrefix(strings, pathTokens[0] + "/");
				addSuffix(strings, " ");
				return strings;
			} else {
				return null;
			}
		}
	}

}
