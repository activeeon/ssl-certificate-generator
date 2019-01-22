package org.ow2.proactive.ssl.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

public class WebResource {

    private static final Logger LOG = LoggerFactory.getLogger(WebResource.class);

    private WebResource(){}

    public static void createWebResource(String path, String fileName, String content) throws IOException {

        //create web resource directory
        File directories = new File(path);

        if (!directories.exists()) {
            if (directories.mkdirs()) {
                LOG.debug("Directory '{}' created? true", path);
            } else {
                throw new RuntimeException("Directory " + path + " cannot be created");
            }
        } else {
            LOG.debug("Directory '{}' already exists", path);
        }

        //create web resource file
        File file = new File(path+File.separator+fileName);
        if(file.createNewFile()){
            LOG.debug("Web resource '{}' Created ", file.getPath());
        } else  LOG.debug("Web resource '{}' already exists", file.getPath());

        //create web resource content
        Files.write(file.toPath(), Arrays.asList(content), Charset.forName("UTF-8"));
        LOG.info("Content '{}' written in web resource '{}'",content, file.getPath());
    }
}
