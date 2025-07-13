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

    private RateLimiterFactory() {}

    public static RateLimiter createRateLimiter(Map<String, RateLimiterConfig> apiConfigs, String strategyType) {
        Map<String, RateLimiterStrategy> strategyMap = new HashMap<>();

        RateLimiterStrategy strategy = switch (strategyType.toLowerCase()) {
            case "fixed" -> new FixedWindowStrategy(apiConfigs);
            case "sliding" -> new SlidingWindowStrategy(apiConfigs);
            case "token" -> new TokenBucketStrategy(apiConfigs);
            default -> throw new IllegalArgumentException("Unsupported strategy: " + strategyType);
        };

        for (String api : apiConfigs.keySet()) {
            strategyMap.put(api, strategy); // You can also assign different strategies per API if needed
        }

        return new ApiRateLimiter(strategyMap);
    }
}
