package br.com.judev.notificationapi.dto;

public record AccessResponse(
        String visitorId,
        int totalAccesses
) {}
