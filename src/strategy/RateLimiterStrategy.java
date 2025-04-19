package strategy;

public interface RateLimiterStrategy {
    boolean allowRequest(String api);
}
