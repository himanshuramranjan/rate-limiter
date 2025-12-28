package strategy;

import config.RateLimiterConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowStrategy implements RateLimiterStrategy {

    private final Map<String, Window> apiVsWindowMap;
    private final Map<String, RateLimiterConfig> apiVsConfigMap;

    private FixedWindowStrategy() {
        apiVsConfigMap = new HashMap<>();
        apiVsWindowMap = new HashMap<>();
    }

    private static class Holder {
        private static final FixedWindowStrategy INSTANCE = new FixedWindowStrategy();
    }

    public static FixedWindowStrategy getInstance() {
        return Holder.INSTANCE;
    }

    public void registerApi(String api, RateLimiterConfig config) {
        apiVsConfigMap.put(api, config);
    }

    public void removeApi(String api) {
        apiVsConfigMap.remove(api);
        apiVsWindowMap.remove(api);
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

    // Declaring it static prevents unnecessary memory reference to the outer class.
    private static class Window {
        private long windowStart;
        private final AtomicInteger requestCount;

        public Window(long windowStart) {
            this.windowStart = windowStart;
            this.requestCount = new AtomicInteger(0);
        }
    }
}
