package com.pulsecheck.pulsecheck.scheduler;
import com.pulsecheck.pulsecheck.entity.MonitoredService;
import com.pulsecheck.pulsecheck.repository.CheckResultRepository;
import com.pulsecheck.pulsecheck.repository.ServiceRepository;
import com.pulsecheck.pulsecheck.service.HealthCheckService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class HealthCheckScheduler {
    private final ServiceRepository serviceRepository;
    private final CheckResultRepository checkResultRepository;
    private final HealthCheckService healthCheckService;

    public HealthCheckScheduler(ServiceRepository serviceRepository, CheckResultRepository checkResultRepository, HealthCheckService healthCheckService) {
        this.serviceRepository = serviceRepository;
        this.checkResultRepository = checkResultRepository;
        this.healthCheckService = healthCheckService;
    }

    @Scheduled(fixedDelay = 10000)
    public void runChecks() {
        try {
            List<MonitoredService> services = serviceRepository.findAll();
            System.out.println("--- Scheduler Woke Up! Checking " + services.size() + " services... ---");
            LocalDateTime now = LocalDateTime.now();

            for (MonitoredService service : services) {
                try {
                    var lastChecks = checkResultRepository.findTop20ByServiceIdOrderByCheckedAtDesc(service.getId());
                    boolean shouldCheck = false;
                    if (lastChecks.isEmpty()) {
                        shouldCheck = true;
                    } else {
                        LocalDateTime lastCheckedAt = lastChecks.get(0).getCheckedAt();
                        if (ChronoUnit.SECONDS.between(lastCheckedAt, now) >= service.getIntervalSeconds()) {
                            shouldCheck = true;
                        }
                    }

                    if (shouldCheck) {
                        System.out.println(" -> Pinging: " + service.getName() + " (" + service.getUrl() + ")");
                        healthCheckService.check(service);
                    }
                } catch (Exception e) {
                    System.err.println(" -> Failed to check " + service.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Scheduler master loop crashed: " + e.getMessage());
        }
    }
}
