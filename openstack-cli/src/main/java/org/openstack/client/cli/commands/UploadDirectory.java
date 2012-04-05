package org.openstack.client.cli.commands;

import java.io.File;
import java.io.IOException;

import org.kohsuke.args4j.Argument;
import org.openstack.client.OpenstackException;
import org.openstack.client.cli.model.StoragePath;
import org.openstack.client.storage.ObjectsResource;
import org.openstack.client.storage.OpenstackStorageClient;
import org.openstack.model.storage.ObjectProperties;
import org.openstack.utils.Io;

public class UploadDirectory extends OpenstackCliCommandRunnerBase {
    @Argument(index = 0)
    public String source;

    @Argument(index = 1)
    public StoragePath dest;

    public UploadDirectory() {
        super("upload", "directory");
    }

    @Override
    public Object runCommand() throws Exception {
        uploadDirectory(Io.resolve(source), dest);
        return "Done";
    }

    private void uploadDirectory(File source, StoragePath target) throws OpenstackException, IOException {
        for (File file : source.listFiles()) {
            String name = file.getName();
            StoragePath childTarget = new StoragePath(target, name);

            if (file.isDirectory()) {
                uploadDirectory(file, childTarget);
            } else {
                uploadFile(file, childTarget);
            }
        }
    }

    private void uploadFile(File source, StoragePath target) throws OpenstackException, IOException {
        OpenstackStorageClient client = getStorageClient();

        String containerName = target.getContainer();
        String objectPath = target.getObjectPath();

        ObjectsResource objects = client.root().containers().id(containerName).objects();

        ObjectProperties objectProperties = new ObjectProperties();
        objectProperties.setName(objectPath);
        objectProperties.setContentType(getContentType(source));

        objects.putObject(source, objectProperties);
    }

    private String getContentType(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot != -1) {
            String extension = name.substring(lastDot + 1);
            extension = extension.toLowerCase();
            if (extension.equals("png")) {
                return "image/png";
            } else if (extension.equals("xml")) {
                return "application/xml";
            } else if (extension.equals("css")) {
                return "text/css";
            } else if (extension.equals("js")) {
                return "application/javascript";
            } else if (extension.equals("html")) {
                return "text/html";
            } else if (extension.equals("swf")) {
                return "application/x-shockwave-flash";
            }
        }

        System.err.println("Cannot deduce MIME type for " + name);
        return null;
    }
}
