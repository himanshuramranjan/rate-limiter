package model;

import strategy.RateLimiterStrategy;

import java.util.Map;

public class ApiRateLimiter implements RateLimiter {

    private final Map<String, RateLimiterStrategy> strategyMap;

    private ApiRateLimiter(Map<String, RateLimiterStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    private static class ApiRateLimiterHolder {

        private static ApiRateLimiter INSTANCE;

        private static void initialize(Map<String, RateLimiterStrategy> strategyMap) {
            if (INSTANCE == null) {
                INSTANCE = new ApiRateLimiter(strategyMap);
            }
        }
    }

    public static ApiRateLimiter getInstance(Map<String, RateLimiterStrategy> strategyMap) {
        ApiRateLimiterHolder.initialize(strategyMap);
        return ApiRateLimiterHolder.INSTANCE;
    }

    @Override
    public RateLimitResponse allowRequest(String api) {
        RateLimiterStrategy strategy = strategyMap.get(api);

        if(strategy == null) {
            return new RateLimitResponse(true, 200, "No strategy defined, allowing the request " + api);
        }

        boolean isRequestAllowed = strategy.allowRequest(api);

        if(isRequestAllowed) {
            return new RateLimitResponse(true, 200, "Request allowed");
        } else {
            return new RateLimitResponse(false, 429, "Too Many Request");
        }
    }
}
