import config.RateLimiterConfig;
import factory.RateLimiterFactory;
import model.ApiRateLimiter;
import model.RateLimitResponse;
import model.RateLimiter;
import strategy.FixedWindowStrategy;
import strategy.RateLimiterStrategy;
import strategy.SlidingWindowStrategy;
import strategy.TokenBucketStrategy;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Map<String, RateLimiterConfig> userApiConfigs = Map.of(
                "/api/user", new RateLimiterConfig(5, 10, 2)
        );

        Map<String, RateLimiterConfig> orderApiConfigs = Map.of(
                "/api/order", new RateLimiterConfig(3, 5, 1)
        );

        Map<String, RateLimiterStrategy> strategyMap = new HashMap<>();
        strategyMap.put("/api/user", new FixedWindowStrategy(userApiConfigs));
        strategyMap.put("/api/order", new SlidingWindowStrategy(orderApiConfigs));

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