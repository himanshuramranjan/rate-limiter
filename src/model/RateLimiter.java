package model;

public interface RateLimiter {
    RateLimitResponse allowRequest(String api);
}
