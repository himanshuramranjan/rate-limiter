package strategy;

import config.RateLimiterConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowStrategy implements RateLimiterStrategy {

    private final Map<String, Window> apiVsWindowMap;
    private final Map<String, RateLimiterConfig> apiVsConfigMap;

    public FixedWindowStrategy(Map<String, RateLimiterConfig> apiVsConfigMap) {
        this.apiVsWindowMap = new ConcurrentHashMap<>();
        this.apiVsConfigMap = apiVsConfigMap;
    }

    @Override
    public boolean allowRequest(String api) {
        RateLimiterConfig config = apiVsConfigMap.get(api);
        if(config == null) return true;

        long currentTime = System.currentTimeMillis() / 1000;
        apiVsWindowMap.putIfAbsent(api, new Window(currentTime));

        // access to 1 thread per API by locking on each Window object
        synchronized (apiVsWindowMap.get(api)) {
            Window window = apiVsWindowMap.get(api);
            long windowEnd = window.windowStart + config.windowSize();

            if(currentTime > windowEnd) {
                window.windowStart = currentTime;
                window.requestCount.set(0);
            }

            return window.requestCount.incrementAndGet() <= config.maxRequest();
        }
    }

    private static class Window {
        private long windowStart;
        private final AtomicInteger requestCount;

        public Window(long windowStart) {
            this.windowStart = windowStart;
            this.requestCount = new AtomicInteger(0);
        }
    }
}
