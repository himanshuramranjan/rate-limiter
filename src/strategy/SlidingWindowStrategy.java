package strategy;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowStrategy implements RateLimiterStrategy {
    private final int limit;
    private final long windowSizeMillis;

    private final ConcurrentHashMap<String, Deque<Long>> requests = new ConcurrentHashMap<>();

    public SlidingWindowStrategy(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();

        requests.putIfAbsent(key, new LinkedList<>());
        Deque<Long> timestamps = requests.get(key);

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowSizeMillis) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < limit) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }
}
