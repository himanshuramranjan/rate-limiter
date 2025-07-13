package strategy;

import config.RateLimiterConfig;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowStrategy implements RateLimiterStrategy {
    private final Map<String, Deque<Long>> requestTimestamps;
    private final Map<String, RateLimiterConfig> apiVsConfigMap;

    public SlidingWindowStrategy(Map<String, RateLimiterConfig> apiVsConfigMap) {
        this.requestTimestamps = new ConcurrentHashMap<>();
        this.apiVsConfigMap = apiVsConfigMap;
    }

    @Override
    public boolean allowRequest(String api) {
        RateLimiterConfig config = apiVsConfigMap.get(api);
        if (config == null) return true;

        requestTimestamps.putIfAbsent(api, new ArrayDeque<>());
        Deque<Long> timestamps = requestTimestamps.get(api);
        long currentTime = System.currentTimeMillis();

        // access to 1 thread per API by locking each instance of timestamps (Deque<Long>)
        synchronized (timestamps) {
            while (!timestamps.isEmpty() &&
                    (currentTime - timestamps.peekFirst()) > config.windowSize() * 1000L) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < config.maxRequest()) {
                timestamps.addLast(currentTime);
                return true;
            }
            return false;
        }
    }
}
