package com.me4502.cab432.twitter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitterConnector {

    private static final int TWEET_COUNT = 30;
    private static final int FOLLOWER_COUNT = 75;

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

    private List<String> filterUserList(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparingInt(User::getStatusesCount).reversed())
                .map(User::getScreenName)
                .collect(Collectors.toList());
    }

    public Optional<List<String>> getFriendsForUser(String displayName) {
        setBearer();
        try {
            List<String> followers = filterUserList(twitter.friendsFollowers().getFollowersList(displayName, -1, FOLLOWER_COUNT, true, true));
            List<String> friends = filterUserList(twitter.friendsFollowers().getFriendsList(displayName, -1, FOLLOWER_COUNT, true, true));

            List<String> common = new ArrayList<>(followers);
            common.retainAll(friends);
            if (common.isEmpty()) {
                return Optional.of(followers.size() > friends.size() ? followers : friends);
            }

            return Optional.of(common);
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }
}
