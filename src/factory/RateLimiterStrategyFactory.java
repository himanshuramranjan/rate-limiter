package factory;

import config.RateLimiterConfig;
import enums.StrategyType;
import strategy.FixedWindowStrategy;
import strategy.RateLimiterStrategy;
import strategy.SlidingWindowStrategy;
import strategy.TokenBucketStrategy;

public class RateLimiterStrategyFactory {

    private RateLimiterStrategyFactory() {}

    public static RateLimiterStrategy getStrategy(StrategyType strategyType) {

        return switch (strategyType) {
            case FIXED -> FixedWindowStrategy.getInstance();
            case SLIDING -> SlidingWindowStrategy.getInstance();
            case TOKEN -> TokenBucketStrategy.getInstance();
        };
    }
}
