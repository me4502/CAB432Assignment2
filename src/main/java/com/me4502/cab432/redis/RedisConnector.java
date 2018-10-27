package com.me4502.cab432.redis;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.me4502.cab432.app.TwitterApp;
import redis.clients.jedis.Jedis;
import twitter4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class RedisConnector {

    private static final Logger logger = Logger.getLogger(RedisConnector.class);

    private Jedis jedis;

    public RedisConnector(String appKey, String secretKey) {
        if (TwitterApp.DEBUG) {
            logger.info("In Debug Mode. Redis Disabled!");
            return;
        }
        try {
            AWSCredentials credentials;
            credentials = new BasicAWSCredentials(appKey, secretKey);

            AmazonElastiCache client = AmazonElastiCacheClientBuilder
                    .standard()
                    .withRegion(Regions.AP_SOUTHEAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            DescribeCacheClustersRequest dccRequest = new DescribeCacheClustersRequest();
            dccRequest.setShowCacheNodeInfo(true);

            DescribeCacheClustersResult result = client.describeCacheClusters(dccRequest);
            result.getCacheClusters().stream().flatMap(cluster -> cluster.getCacheNodes().stream()).findFirst().ifPresent(node
                    -> jedis = new Jedis(node.getEndpoint().getAddress(), node.getEndpoint().getPort()));
            if (jedis == null) {
                logger.warn("Failed to find a Redis instance! Redis will not be used.");
            }
        } catch (Throwable t) {
            logger.warn("Failed to initialise Redis! Redis will not be used.", t);
        }
    }

    public Optional<String> getRedisValue(String key) {
        if (jedis == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(jedis.get(key));
        }
    }

    public void setRedisValue(String key, String value, int expirySeconds) {
        if (jedis != null) {
            jedis.setex(key, expirySeconds, value);
        }
    }

    public String getOrSet(String key, Supplier<String> supplier, int expirySeconds) {
        Optional<String> value = getRedisValue(key);
        if (value.isPresent()) {
            return value.get();
        } else {
            String val = supplier.get();
            setRedisValue(key, val, expirySeconds);
            return val;
        }
    }
}
