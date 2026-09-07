package com.pulsecheck.pulsecheck.repository;
import com.pulsecheck.pulsecheck.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ServiceRepository extends JpaRepository<MonitoredService, Long> {
}
