package org.ow2.proactive.ssl.jetty;

import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class EmbeddedJetty {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJetty.class);

    private static int port = 8080;
    private static String context = "/";
    private static String resourceBase = ".";

    private static String directory = ".well-known" + File.separator + "acme-challenge";
    private static String webResource = "";
    private static String content = "";


    private EmbeddedJetty(){}

    private static void main(String... args) throws ParseException {

        parseArguments(args);

        /*webResource = challenge.getToken();
        content = challenge.getAuthorization();
        createChallengeWebResource();*/

    }

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

    public static void parseArguments(String... args) throws ParseException {

        final Option help = Option.builder("h")
                .required(false)
                .longOpt("help")
                .hasArg(false)
                .desc("Help.")
                .build();
        final Option domain = Option.builder("d")
                .required(true)
                .longOpt("domain")
                .hasArg(true)
                .desc("Domain to be protected by the SSL certificate.")
                .build();
        final Option jettyPort = Option.builder("p")
                .required(false)
                .hasArg(true)
                .longOpt("port")
                .desc("Port used by embedded jetty.")
                .build();

        final Options options = new Options();
        options.addOption(help);
        options.addOption(domain);
        options.addOption(jettyPort);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
    }

    public void createChallengeWebResource() {

        Runnable webResourceProcess = new Runnable() {
            public void run() {
                try {
                    WebResource.createWebResource(resourceBase + File.separator + directory, webResource, content);
                    EmbeddedJetty.startJetty(port, context, resourceBase);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        new Thread(webResourceProcess).start();
    }

}