package com.pulsecheck.pulsecheck.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
public record CreateServiceRequest(
    @NotBlank String name,
    @NotBlank @URL String url,
    @Min(10) int intervalSeconds
) {}
