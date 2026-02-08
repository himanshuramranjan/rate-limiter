package strategy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowStrategy implements RateLimiterStrategy {

    private final int limit;
    private final long windowSizeMillis;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowStrategy(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();

        windows.putIfAbsent(key, new Window(now, new AtomicInteger(0)));
        Window window = windows.get(key);

        synchronized (window) {
            if (now - window.startTime >= windowSizeMillis) {
                window.startTime = now;
                window.counter.set(0);
            }
            return window.counter.incrementAndGet() <= limit;
        }
    }

    private static class Window {
        long startTime;
        AtomicInteger counter;

        Window(long startTime, AtomicInteger counter) {
            this.startTime = startTime;
            this.counter = counter;
        }
    }
}
