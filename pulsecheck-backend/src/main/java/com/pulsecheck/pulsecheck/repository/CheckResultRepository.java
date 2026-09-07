package com.pulsecheck.pulsecheck.repository;
import com.pulsecheck.pulsecheck.entity.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {
    List<CheckResult> findTop20ByServiceIdOrderByCheckedAtDesc(Long serviceId);
    List<CheckResult> findByServiceIdAndCheckedAtAfter(Long serviceId, LocalDateTime after);
    long countByServiceIdAndCheckedAtAfter(Long serviceId, LocalDateTime after);
    long countByServiceIdAndIsUpTrueAndCheckedAtAfter(Long serviceId, LocalDateTime after);
    void deleteByServiceId(Long serviceId);
}
