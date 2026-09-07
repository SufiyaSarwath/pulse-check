package com.pulsecheck.pulsecheck.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long serviceId;
    private Integer statusCode;
    private long responseTimeMs;
    private boolean isUp;
    private LocalDateTime checkedAt;
    @PrePersist
    protected void onCreate() { this.checkedAt = LocalDateTime.now(); }
}
