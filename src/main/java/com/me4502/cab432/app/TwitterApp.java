package com.me4502.cab432.app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.me4502.cab432.redis.RedisConnector;
import com.me4502.cab432.sentiment.SentimentConnector;
import com.me4502.cab432.twitter.TwitterConnector;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import spark.Response;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class TwitterApp {

    public static final boolean DEBUG = System.getProperty("debug", "false").equals("true");

    // This is a Singleton class - setup as soon as it's first referenced.
    private static final TwitterApp instance = new TwitterApp();

    private TwitterConnector twitterConnector;
    private SentimentConnector sentimentConnector;
    private RedisConnector redisConnector;

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

            String twitterKey = root.getNode("twitter-key").getString("INSERT");
            String twitterSecret = root.getNode("twitter-secret").getString("INSERT");

            String awsKey = root.getNode("aws-key").getString("INSERT");
            String awsSecret = root.getNode("aws-secret").getString("INSERT");

            configManager.save(root);

            // Try to use those keys to load the connectors.
            this.twitterConnector = new TwitterConnector(twitterKey, twitterSecret);
            this.sentimentConnector = new SentimentConnector();
            this.redisConnector = new RedisConnector(awsKey, awsSecret);
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
        redirect.get("/", "index.html");

        get("/twitter/get_user/:user", (request, response)
                -> getRedisConnector().getOrSet("sentiment/" + request.params("user"), ()
                        -> getTwitterConnector().getTweetsForUser(request.params("user"))
                        .map(getSentimentConnector()::getAllSentiment).map(gson::toJson)
                        .orElseGet(() -> badRequest(response, "Failed to lookup user " + request.params("user"))),
                60 * 15));

        get("/twitter/get_friends/:user", (request, response)
                -> getRedisConnector().getOrSet("friends/" + request.params("user"), ()
                        -> getTwitterConnector().getFriendsForUser(request.params("user"))
                        .map(gson::toJson)
                        .orElseGet(() -> badRequest(response, "Failed to lookup friends for " + request.params("user"))),
                60 * 15));
    }

    /**
     * Gets the Twitter Connector.
     *
     * @return The twitter connector
     */
    public TwitterConnector getTwitterConnector() {
        return this.twitterConnector;
    }

    /**
     * Gets the Redis Connector.
     *
     * @return The Redis connector
     */
    public RedisConnector getRedisConnector() {
        return this.redisConnector;
    }

    /**
     * Gets the Sentiment Connector.
     *
     * @return The sentiment connector
     */
    public SentimentConnector getSentimentConnector() {
        return this.sentimentConnector;
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
