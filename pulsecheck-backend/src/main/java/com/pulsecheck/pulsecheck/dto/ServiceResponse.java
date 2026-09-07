package com.pulsecheck.pulsecheck.dto;
import java.time.LocalDateTime;
public record ServiceResponse(
    Long id, String name, String url, int intervalSeconds, LocalDateTime createdAt,
    boolean currentStatus, double uptimePercentage, double averageResponseTimeMs
) {}
