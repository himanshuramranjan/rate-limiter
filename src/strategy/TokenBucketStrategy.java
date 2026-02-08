package strategy;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimiterStrategy {

    private final long capacity;
    private final long refillRatePerSecond;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketStrategy(long capacity, long refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public boolean allowRequest(String key) {
        buckets.putIfAbsent(key, new Bucket(capacity, System.nanoTime()));
        Bucket bucket = buckets.get(key);

        synchronized (bucket) {
            refill(bucket);

            if (bucket.tokens > 0) {
                bucket.tokens--;
                return true;
            }
            return false;
        }
    }

    private void refill(Bucket bucket) {
        long now = System.nanoTime();
        long elapsedSeconds = (now - bucket.lastRefillTime) / 1_000_000_000;

        if (elapsedSeconds > 0) {
            long tokensToAdd = elapsedSeconds * refillRatePerSecond;
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
        }
    }

    private static class Bucket {
        long tokens;
        long lastRefillTime;

        Bucket(long tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}
