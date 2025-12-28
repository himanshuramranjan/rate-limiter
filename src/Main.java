import config.RateLimiterConfig;
import factory.RateLimiterStrategyFactory;
import service.ApiRateLimiter;
import model.RateLimitResponse;
import strategy.RateLimiterStrategy;

import java.util.HashMap;
import java.util.Map;

import static enums.StrategyType.FIXED;
import static enums.StrategyType.SLIDING;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ApiRateLimiter rateLimiter = ApiRateLimiter.getInstance();
        rateLimiter.registerApi("/api/user/login", new RateLimiterConfig(5, 10, null), FIXED);
        rateLimiter.registerApi("/api/user/logout", new RateLimiterConfig(50, 10, null), FIXED);
        rateLimiter.registerApi("/api/order", new RateLimiterConfig(3, 5, null), SLIDING);

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