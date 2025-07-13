package factory;

import config.RateLimiterConfig;
import enums.StrategyType;
import strategy.FixedWindowStrategy;
import strategy.RateLimiterStrategy;
import strategy.SlidingWindowStrategy;
import strategy.TokenBucketStrategy;

import java.util.Map;

public class RateLimiterStrategyFactory {

    private RateLimiterStrategyFactory() {}

    public static RateLimiterStrategy createRateLimiter(Map<String, RateLimiterConfig> apiConfigs, StrategyType strategyType) {

        return switch (strategyType) {
            case FIXED -> new FixedWindowStrategy(apiConfigs);
            case SLIDING -> new SlidingWindowStrategy(apiConfigs);
            case TOKEN -> new TokenBucketStrategy(apiConfigs);
        };
    }
}
