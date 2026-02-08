package service;

import model.RateLimitResponse;
import strategy.RateLimiterStrategy;

public class ApiRateLimiter {

    private final RateLimiterStrategy strategy;

    public ApiRateLimiter(RateLimiterStrategy strategy) {
        this.strategy = strategy;
    }

    public RateLimitResponse allow(String userId) {

        boolean isRequestAllowed = strategy.allowRequest(userId);

        if(isRequestAllowed) {
            return new RateLimitResponse(true, 200, "Request allowed");
        } else {
            return new RateLimitResponse(false, 429, "Too Many Request");
        }
    }
}
