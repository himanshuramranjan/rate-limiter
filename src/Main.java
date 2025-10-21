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
                "/api/user/login", new RateLimiterConfig(5, 10, null),
                "/api/user/logout", new RateLimiterConfig(50, 10, null)
        );

        Map<String, RateLimiterConfig> orderApiConfigs = Map.of(
                "/api/order", new RateLimiterConfig(3, 5, null)
        );

        Map<String, RateLimiterStrategy> strategyMap = new HashMap<>();

        RateLimiterStrategy fixedWindowStrategy = RateLimiterStrategyFactory.createRateLimiter(userApiConfigs, FIXED);
        RateLimiterStrategy slidingWindowStrategy = RateLimiterStrategyFactory.createRateLimiter(orderApiConfigs, SLIDING);

        strategyMap.put("/api/user/login", fixedWindowStrategy);
        strategyMap.put("/api/user/logout", fixedWindowStrategy);
        strategyMap.put("/api/order", slidingWindowStrategy);

        RateLimiter rateLimiter = ApiRateLimiter.getInstance(strategyMap);

        for (int i = 0; i < 15; i++) {
            RateLimitResponse res = rateLimiter.allowRequest("/api/user/login");
            System.out.println("Request " + (i + 1) + ": " + res.message());
            Thread.sleep(1000);
        }

        for(int i = 0; i < 15; i++) {
            RateLimitResponse res = rateLimiter.allowRequest("/api/order");
            System.out.println("Request " + (i + 1) + ": " + res.message());
            Thread.sleep(1000);
        }
    }
}