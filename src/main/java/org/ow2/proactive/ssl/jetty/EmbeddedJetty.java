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
    private static String base = ".";

    private static String directory = ".well-known" + File.separator + "acme-challenge";
    private static String webResource = "";
    private static String content = "";


    private EmbeddedJetty(){}

    public static void main(String... args) throws Exception {
        parseArguments(args);
        WebResource.createWebResource(base + File.separator + directory, webResource, content);
        EmbeddedJetty.startJetty(port, context, base);
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
        final Option application = Option.builder("a")
                .required(false)
                .longOpt("application")
                .hasArg(true)
                .desc("Web application context")
                .build();
        final Option jettyPort = Option.builder("p")
                .required(false)
                .hasArg(true)
                .longOpt("port")
                .desc("Port used by embedded jetty.")
                .build();
        final Option resourceBase = Option.builder("b")
                .required(false)
                .hasArg(true)
                .longOpt("base")
                .desc("Resource base used by jetty.")
                .build();
        final Option resourceDirectory = Option.builder("d")
                .required(false)
                .hasArg(true)
                .longOpt("directory")
                .desc("Directory containing the web resource served by jetty.")
                .build();
        final Option resourceName = Option.builder("r")
                .required(true)
                .hasArg(true)
                .longOpt("resource")
                .desc("Web resource to be served by jetty.")
                .build();
        final Option resourceContent = Option.builder("c")
                .required(true)
                .hasArg(true)
                .longOpt("content")
                .desc("Content of the web resource to be served by jetty.")
                .build();

        final Options options = new Options();
        options.addOption(help);
        options.addOption(application);
        options.addOption(jettyPort);
        options.addOption(resourceBase);
        options.addOption(resourceDirectory);
        options.addOption(resourceName);
        options.addOption(resourceContent);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if (cmd.hasOption("h")){
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Jetty Web Resource", options );
            System.exit(0);
        } else {
            if (cmd.hasOption("a")){
                context = cmd.getOptionValue("a");
            }
            if (cmd.hasOption("b")){
                base = cmd.getOptionValue("b");
            }
            if (cmd.hasOption("d")){
                directory = cmd.getOptionValue("d");
            }
            if (cmd.hasOption("p")){
                port = Integer.valueOf(cmd.getOptionValue("p"));
            }
        }

        webResource = cmd.getOptionValue("r");
        content = cmd.getOptionValue("c");
    }
}