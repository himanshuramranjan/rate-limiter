package strategy;

import config.RateLimiterConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimiterStrategy {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, RateLimiterConfig> configMap;

    public TokenBucketStrategy(Map<String, RateLimiterConfig> configMap) {
        this.configMap = configMap;
    }

    private static class TokenBucket {
        private double tokens;
        private long lastRefillTimestamp;

        public TokenBucket(double tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    @Override
    public boolean allowRequest(String api) {
        RateLimiterConfig config = configMap.get(api);
        if (config == null) return true;

        buckets.putIfAbsent(api, new TokenBucket(config.getMaxRequest(), System.nanoTime()));

        synchronized (buckets.get(api)) {
            TokenBucket bucket = buckets.get(api);
            long now = System.nanoTime();
            long elapsedTime = now - bucket.lastRefillTimestamp;

            double refillTokens = (elapsedTime / 1e9) * config.getRefillRate();
            bucket.tokens = Math.min(config.getMaxRequest(), bucket.tokens + refillTokens);
            bucket.lastRefillTimestamp = now;

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }
            return false;
        }
    }
}
