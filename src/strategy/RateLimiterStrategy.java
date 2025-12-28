package strategy;

import config.RateLimiterConfig;

public interface RateLimiterStrategy {
    boolean allowRequest(String api);
    void registerApi(String api, RateLimiterConfig config);
    void removeApi(String api);
}
