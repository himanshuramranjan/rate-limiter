import service.ApiRateLimiter;
import strategy.FixedWindowStrategy;
import strategy.SlidingWindowStrategy;
import strategy.TokenBucketStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Demo {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("===== FIXED WINDOW DEMO =====");
        fixedWindowDemo();

        System.out.println("\n===== SLIDING WINDOW DEMO =====");
        slidingWindowDemo();

        System.out.println("\n===== TOKEN BUCKET DEMO =====");
        tokenBucketDemo();

        System.out.println("\n===== CONCURRENCY DEMO =====");
        concurrencyDemo();
    }

    private static void fixedWindowDemo() {

        ApiRateLimiter limiter =
                new ApiRateLimiter(new FixedWindowStrategy(3, 1000));

        String user = "user1";

        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // false (limit hit)
    }

    private static void slidingWindowDemo() {

        ApiRateLimiter limiter =
                new ApiRateLimiter(new SlidingWindowStrategy(2, 1000));

        String user = "user2";

        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // false
    }

    private static void tokenBucketDemo() throws InterruptedException {

        ApiRateLimiter limiter =
                new ApiRateLimiter(new TokenBucketStrategy(2, 1));

        String user = "user3";

        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // false

        Thread.sleep(1100); // refill 1 token

        System.out.println(limiter.allow(user)); // true
        System.out.println(limiter.allow(user)); // false
    }

    private static void concurrencyDemo() throws InterruptedException {

        ApiRateLimiter limiter =
                new ApiRateLimiter(new FixedWindowStrategy(100, 1000));

        ExecutorService pool = Executors.newFixedThreadPool(10);
        AtomicInteger allowed = new AtomicInteger();

        for (int i = 0; i < 200; i++) {
            pool.submit(() -> {
                if (limiter.allow("concurrentUser").httpStatusCode() == 200) {
                    allowed.incrementAndGet();
                }
            });
        }

        pool.shutdown();
        Thread.sleep(500);

        System.out.println("Allowed requests (should be <= 100): " + allowed.get());
    }
}