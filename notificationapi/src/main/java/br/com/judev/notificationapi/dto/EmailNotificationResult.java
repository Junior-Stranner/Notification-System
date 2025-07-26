package br.com.judev.notificationapi.dto;

public class EmailNotificationResult {
    private final boolean success;
    private final String message;

    public EmailNotificationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
