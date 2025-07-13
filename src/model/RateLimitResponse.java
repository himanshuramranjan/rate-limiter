package model;

public record RateLimitResponse(boolean isAllowed, int httpStatusCode, String message) {
}
