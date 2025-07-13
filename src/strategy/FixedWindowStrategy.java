package strategy;

import config.RateLimiterConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowStrategy implements RateLimiterStrategy {

    private final Map<String, Window> windowMap;
    private final Map<String, RateLimiterConfig> configMap;

    public FixedWindowStrategy(Map<String, RateLimiterConfig> configMap) {
        this.windowMap = new ConcurrentHashMap<>();
        this.configMap = configMap;
    }

    @Override
    public boolean allowRequest(String api) {
        RateLimiterConfig config = configMap.get(api);
        if(config == null) return true;

        long currentTime = System.currentTimeMillis() / 1000;
        windowMap.putIfAbsent(api, new Window(currentTime));

        synchronized (windowMap.get(api)) {
            Window window = windowMap.get(api);
            long windowEnd = window.windowStart + config.windowSize();

            if(currentTime > windowEnd) {
                window.windowStart = currentTime;
                window.requestCount.set(0);
            }

            if(window.requestCount.incrementAndGet() <= config.maxRequest()) {
                return true;
            }

            return false;
        }
    }

    private static class Window {
        private long windowStart;
        private AtomicInteger requestCount;

        public Window(long windowStart) {
            this.windowStart = windowStart;
            this.requestCount = new AtomicInteger(0);
        }
    }
}
