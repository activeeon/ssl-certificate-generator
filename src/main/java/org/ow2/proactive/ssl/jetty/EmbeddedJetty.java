package org.ow2.proactive.ssl.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJetty {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJetty.class);

    public static void startJetty(int port, String context, String resourceBase) throws Exception
    {
        Server server = new Server(port);

        HandlerList handlers = new HandlerList();
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(context);

        contextHandler.setBaseResource(Resource.newResource(resourceBase));
        LOG.debug("Base Resource URI is {} ", resourceBase);

        // Add something to serve the static files
        // It's named "default" to conform to servlet spec
        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        contextHandler.addServlet(staticHolder, "/");

        handlers.addHandler(contextHandler);
        handlers.addHandler(new DefaultHandler()); // always last handler

        server.setHandler(handlers);
        server.start();

        LOG.info("Jetty web server is started on {}", server.getURI());
        server.join();
    }
}