package com.pulsecheck.pulsecheck.dto;
import java.time.LocalDateTime;
public record CheckResultResponse(
    Long id, Long serviceId, Integer statusCode, long responseTimeMs,
    boolean isUp, LocalDateTime checkedAt
) {}
