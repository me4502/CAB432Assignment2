package com.me4502.cab432.twitter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Optional;

public class TwitterConnector {

    private static final int TWEET_COUNT = 10;

    private static final ThreadLocal<Paging> PAGING_LOCAL = ThreadLocal.withInitial(() -> new Paging(1, TWEET_COUNT));

    private Twitter twitter;

    private OAuth2Token bearerToken;

    public TwitterConnector(String apiKey, String apiSecret) throws TwitterException {
        Configuration config = new ConfigurationBuilder()
                .setApplicationOnlyAuthEnabled(true)
                .build();

        twitter = new TwitterFactory(config).getInstance();

        twitter.setOAuthConsumer(apiKey, apiSecret);

        this.bearerToken = twitter.getOAuth2Token();
    }

    private void setBearer() {
        twitter.setOAuth2Token(bearerToken);
    }

    public Optional<ResponseList<Status>> getTweetsForUser(String displayName) {
        setBearer();
        try {
            return Optional.of(twitter.timelines().getUserTimeline(displayName, PAGING_LOCAL.get()));
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }
}
