package service;

import config.RateLimiterConfig;
import enums.StrategyType;
import factory.RateLimiterStrategyFactory;
import model.RateLimitResponse;
import strategy.RateLimiterStrategy;

import java.util.HashMap;
import java.util.Map;

public class ApiRateLimiter {

    private final Map<String, RateLimiterStrategy> apiVsStrategyMap;

    private ApiRateLimiter() {
        this.apiVsStrategyMap = new HashMap<>();
    }

    private static class Holder {
        private static final ApiRateLimiter INSTANCE = new ApiRateLimiter();
    }

    public static ApiRateLimiter getInstance() {
        return Holder.INSTANCE;
    }

    public void registerApi(String api, RateLimiterConfig config, StrategyType strategyType) {

        RateLimiterStrategy strategy = RateLimiterStrategyFactory.getStrategy(strategyType);

        strategy.registerApi(api, config);
        apiVsStrategyMap.put(api, strategy);
    }

    public RateLimitResponse allowRequest(String api) {
        RateLimiterStrategy strategy = apiVsStrategyMap.get(api);

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
