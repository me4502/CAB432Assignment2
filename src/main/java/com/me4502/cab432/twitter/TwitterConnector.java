package com.me4502.cab432.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterConnector {

    public TwitterConnector(String apiKey, String apiSecret) {
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(apiKey, apiSecret);
    }

}
