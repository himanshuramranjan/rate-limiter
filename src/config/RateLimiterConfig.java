package config;

public record RateLimiterConfig(Integer maxRequest, Integer windowSize, Integer refillRate) {
}
