package strategy;

import config.RateLimiterConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimiterStrategy {

    private final Map<String, TokenBucket> buckets;
    private final Map<String, RateLimiterConfig> apiVsConfigMap;

    public TokenBucketStrategy() {
        this.apiVsConfigMap = new HashMap<>();
        buckets = new ConcurrentHashMap<>();
    }

    private static class Holder {
        private static final TokenBucketStrategy INSTANCE = new TokenBucketStrategy();
    }

    public static TokenBucketStrategy getInstance() {
        return TokenBucketStrategy.Holder.INSTANCE;
    }

    public void registerApi(String api, RateLimiterConfig config) {
        apiVsConfigMap.put(api, config);
    }

    public void removeApi(String api) {
        apiVsConfigMap.remove(api);
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
        RateLimiterConfig config = apiVsConfigMap.get(api);
        if (config == null) return true;

        buckets.putIfAbsent(api, new TokenBucket(config.maxRequest(), System.nanoTime()));

        // access to 1 thread per API by locking on each Token object
        synchronized (buckets.get(api)) {
            TokenBucket bucket = buckets.get(api);
            long now = System.nanoTime();
            long elapsedTime = now - bucket.lastRefillTimestamp;

            double refillTokens = (elapsedTime / 1e9) * config.refillRate();
            bucket.tokens = Math.min(config.maxRequest(), bucket.tokens + refillTokens);
            bucket.lastRefillTimestamp = now;

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }
            return false;
        }
    }
}
