import config.RateLimiterConfig;
import factory.RateLimiterStrategyFactory;
import model.ApiRateLimiter;
import model.RateLimitResponse;
import model.RateLimiter;
import strategy.RateLimiterStrategy;

import java.util.HashMap;
import java.util.Map;

import static enums.StrategyType.FIXED;
import static enums.StrategyType.SLIDING;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Map<String, RateLimiterConfig> userApiConfigs = Map.of(
                "/api/user", new RateLimiterConfig(5, 10, 2)
        );

        Map<String, RateLimiterConfig> orderApiConfigs = Map.of(
                "/api/order", new RateLimiterConfig(3, 5, 1)
        );

        Map<String, RateLimiterStrategy> strategyMap = new HashMap<>();

        RateLimiterStrategy fixedWindowStrategy = RateLimiterStrategyFactory.createRateLimiter(userApiConfigs, FIXED);
        RateLimiterStrategy slidingWindowStrategy = RateLimiterStrategyFactory.createRateLimiter(orderApiConfigs, SLIDING);

        strategyMap.put("/api/user", fixedWindowStrategy);
        strategyMap.put("/api/order", slidingWindowStrategy);

        RateLimiter rateLimiter = new ApiRateLimiter(strategyMap);

        for (int i = 0; i < 15; i++) {
            RateLimitResponse res = rateLimiter.allowRequest("/api/user");
            System.out.println("Request " + (i + 1) + ": " + res.getMessage());
            Thread.sleep(1000);
        }

        for(int i = 0; i < 15; i++) {
            RateLimitResponse res = rateLimiter.allowRequest("/api/order");
            System.out.println("Request " + (i + 1) + ": " + res.getMessage());
            Thread.sleep(1000);
        }
    }
}