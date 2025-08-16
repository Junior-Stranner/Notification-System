package br.com.judev.notificationapi.dto;

public record AccessResponse(
        String visitorId,
        String ipAddress,
        int totalAccesses
) {}
