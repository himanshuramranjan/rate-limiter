package strategy;

import config.RateLimiterConfig;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowStrategy implements RateLimiterStrategy {
    private final Map<String, Deque<Long>> requestTimestamps;
    private final Map<String, RateLimiterConfig> configMap;

    public SlidingWindowStrategy(Map<String, RateLimiterConfig> configMap) {
        this.requestTimestamps = new ConcurrentHashMap<>();
        this.configMap = configMap;
    }

    @Override
    public boolean allowRequest(String api) {
        RateLimiterConfig config = configMap.get(api);
        if (config == null) return true;

        requestTimestamps.putIfAbsent(api, new ArrayDeque<>());
        Deque<Long> timestamps = requestTimestamps.get(api);
        long currentTime = System.currentTimeMillis();

        synchronized (timestamps) {
            while (!timestamps.isEmpty() &&
                    (currentTime - timestamps.peekFirst()) > config.getWindowSize() * 1000) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < config.getMaxRequest()) {
                timestamps.addLast(currentTime);
                return true;
            }
            return false;
        }
    }
}
