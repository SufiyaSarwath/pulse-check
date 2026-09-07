package com.pulsecheck.pulsecheck.controller;
import com.pulsecheck.pulsecheck.dto.*;
import com.pulsecheck.pulsecheck.entity.MonitoredService;
import com.pulsecheck.pulsecheck.exception.ResourceNotFoundException;
import com.pulsecheck.pulsecheck.repository.CheckResultRepository;
import com.pulsecheck.pulsecheck.repository.ServiceRepository;
import com.pulsecheck.pulsecheck.service.StatsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceRepository serviceRepository;
    private final CheckResultRepository checkResultRepository;
    private final StatsService statsService;

    public ServiceController(ServiceRepository serviceRepository, CheckResultRepository checkResultRepository, StatsService statsService) {
        this.serviceRepository = serviceRepository;
        this.checkResultRepository = checkResultRepository;
        this.statsService = statsService;
    }

    @GetMapping
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream().map(s -> {
            var stats = statsService.getStats(s.getId());
            return new ServiceResponse(s.getId(), s.getName(), s.getUrl(), s.getIntervalSeconds(), s.getCreatedAt(), stats.currentStatus(), stats.uptimePercentage(), stats.averageResponseTimeMs());
        }).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse createService(@Valid @RequestBody CreateServiceRequest req) {
        MonitoredService service = MonitoredService.builder().name(req.name()).url(req.url()).intervalSeconds(req.intervalSeconds()).build();
        MonitoredService saved = serviceRepository.save(service);
        var stats = statsService.getStats(saved.getId());
        return new ServiceResponse(saved.getId(), saved.getName(), saved.getUrl(), saved.getIntervalSeconds(), saved.getCreatedAt(), stats.currentStatus(), stats.uptimePercentage(), stats.averageResponseTimeMs());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteService(@PathVariable Long id) {
        MonitoredService service = serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        checkResultRepository.deleteByServiceId(id);
        serviceRepository.delete(service);
    }

    @GetMapping("/{id}/checks")
    public List<CheckResultResponse> getChecks(@PathVariable Long id) {
        serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        return checkResultRepository.findTop20ByServiceIdOrderByCheckedAtDesc(id).stream()
            .map(c -> new CheckResultResponse(c.getId(), c.getServiceId(), c.getStatusCode(), c.getResponseTimeMs(), c.isUp(), c.getCheckedAt())).toList();
    }

    @GetMapping("/{id}/stats")
    public StatsService.ServiceStatsDTO getStats(@PathVariable Long id) {
        serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        return statsService.getStats(id);
    }
}
