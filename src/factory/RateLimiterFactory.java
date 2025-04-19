package factory;

import config.RateLimiterConfig;
import model.ApiRateLimiter;
import model.RateLimiter;
import strategy.FixedWindowStrategy;
import strategy.RateLimiterStrategy;
import strategy.SlidingWindowStrategy;
import strategy.TokenBucketStrategy;

import java.util.HashMap;
import java.util.Map;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(Map<String, RateLimiterConfig> apiConfigs, String strategyType) {
        Map<String, RateLimiterStrategy> strategyMap = new HashMap<>();

        RateLimiterStrategy strategy;
        switch (strategyType.toLowerCase()) {
            case "fixed":
                strategy = new FixedWindowStrategy(apiConfigs);
                break;
            case "sliding":
                strategy = new SlidingWindowStrategy(apiConfigs);
                break;
            case "token":
                strategy = new TokenBucketStrategy(apiConfigs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported strategy: " + strategyType);
        }

        for (String api : apiConfigs.keySet()) {
            strategyMap.put(api, strategy); // You can also assign different strategies per API if needed
        }

        return new ApiRateLimiter(strategyMap);
    }
}
