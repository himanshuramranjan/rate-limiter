package config;

public class RateLimiterConfig {
    private final int maxRequest;
    private final int windowSize;
    private final int refillRate;

    public RateLimiterConfig(int maxRequest, int windowSize, int refillRate) {
        this.maxRequest = maxRequest;
        this.windowSize = windowSize;
        this.refillRate = refillRate;
    }

    public int getMaxRequest() {
        return maxRequest;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getRefillRate() {
        return refillRate;
    }
}
