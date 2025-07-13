package config;

public record RateLimiterConfig(int maxRequest, int windowSize, int refillRate) {
}
