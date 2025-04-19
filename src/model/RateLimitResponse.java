package model;

public class RateLimitResponse {
    private final boolean isAllowed;
    private final int httpStatusCode;
    private final String message;

    public RateLimitResponse(boolean isAllowed, int httpStatusCode, String message) {
        this.isAllowed = isAllowed;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }
}
