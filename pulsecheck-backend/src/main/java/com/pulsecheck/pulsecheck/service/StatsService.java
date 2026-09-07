package com.pulsecheck.pulsecheck.service;

import com.pulsecheck.pulsecheck.entity.CheckResult;
import com.pulsecheck.pulsecheck.repository.CheckResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {

    private final CheckResultRepository checkResultRepository;

    public StatsService(CheckResultRepository checkResultRepository) {
        this.checkResultRepository = checkResultRepository;
    }

    public record ServiceStatsDTO(
            double uptimePercentage,
            double averageResponseTimeMs,
            long totalChecks,
            boolean currentStatus
    ) {}

    public ServiceStatsDTO getStats(Long serviceId) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<CheckResult> recentChecks = checkResultRepository.findByServiceIdAndCheckedAtAfter(serviceId, twentyFourHoursAgo);

        if (recentChecks.isEmpty()) {
            var allTimeChecks = checkResultRepository.findTop20ByServiceIdOrderByCheckedAtDesc(serviceId);
            if (allTimeChecks.isEmpty()) {
                return new ServiceStatsDTO(0.0, 0.0, 0, false);
            }
            recentChecks = allTimeChecks;
        }

        long totalChecks = recentChecks.size();
        long upCount = recentChecks.stream().filter(CheckResult::isUp).count();

        double uptime = (upCount * 100.0) / totalChecks;
        double roundedUptime = Math.round(uptime * 10.0) / 10.0;

        double avgResponse = recentChecks.stream()
                .mapToLong(CheckResult::getResponseTimeMs)
                .average()
                .orElse(0.0);
        double roundedResponse = Math.round(avgResponse * 10.0) / 10.0;

        boolean currentStatus = recentChecks.get(0).isUp();

        return new ServiceStatsDTO(roundedUptime, roundedResponse, totalChecks, currentStatus);
    }
}
