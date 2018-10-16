package com.me4502.cab432.app;

import static freemarker.template.Configuration.VERSION_2_3_26;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freemarker.template.Configuration;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import spark.ModelAndView;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class TwitterApp {

    public static final boolean DEBUG = System.getProperty("debug", "false").equals("true");

    // This is a Singleton class - setup as soon as it's first referenced.
    private static final TwitterApp instance = new TwitterApp();

    // Gson
    private Gson gson = new GsonBuilder().create();

    /**
     * Loads the main app content.
     */
    public void load() throws IOException {
        loadConfigurationAndConnectors();

        loadWebServer();
    }

    /**
     * Load the configuration file and setup the API connectors.
     */
    private void loadConfigurationAndConnectors() {
        ConfigurationLoader<CommentedConfigurationNode> configManager = HoconConfigurationLoader.builder()
                .setPath(Paths.get("twitter_sentiment.conf"))
                .setDefaultOptions(ConfigurationOptions.defaults().setShouldCopyDefaults(true))
                .build();

        try {
            CommentedConfigurationNode root = configManager.load();



            configManager.save(root);

            // Try to use those keys to load the connectors.

        } catch (Exception e) {
            // If an exception occurs here, it's bad - runtime it.
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to respond with a bad request.
     *
     * @param response The response object
     * @param message The error message
     * @return The json-ified error message
     */
    private String badRequest(Response response, String message) {
        response.status(400);
        response.header("Bad Request", message);
        return gson.toJson(Map.of("error", message));
    }

    /**
     * Setup the webserver configuration and routes.
     */
    private void loadWebServer() {
        port(Integer.parseInt(System.getProperty("twitter_sentiment.port", "5078")));
        if (DEBUG) {
            // During debug this allows hot-reloading the static changes.
            staticFiles.externalLocation("src/main/resources/static");
        } else {
            staticFiles.location("/static");
        }

        // Setup routes
        get("/", (request, response)
                -> render(Map.of(), "index.html"));
    }

    /**
     * Helper method to render Freemarker template files.
     *
     * @param model The model
     * @param templatePath The template path
     * @return The rendered template
     */
    private static String render(Map<String, Object> model, String templatePath) {
        freemarker.template.Configuration config = new Configuration(VERSION_2_3_26);
        config.setClassForTemplateLoading(TwitterApp.class, "/static/html/");

        return new FreeMarkerEngine(config).render(new ModelAndView(model, templatePath));
    }

    /**
     * Gets the Singleton instance of this class
     *
     * @return The instance
     */
    public static TwitterApp getInstance() {
        return TwitterApp.instance;
    }
}
