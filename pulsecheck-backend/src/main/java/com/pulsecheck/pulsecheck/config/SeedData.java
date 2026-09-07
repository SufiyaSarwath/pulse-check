package com.pulsecheck.pulsecheck.config;
import com.pulsecheck.pulsecheck.entity.MonitoredService;
import com.pulsecheck.pulsecheck.repository.ServiceRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedData {
    @Bean
    public ApplicationRunner initializer(ServiceRepository serviceRepository) {
        return args -> {
            if (serviceRepository.count() == 0) {
                serviceRepository.saveAll(List.of(
                    MonitoredService.builder().name("HTTPBin").url("https://httpbin.org/get").intervalSeconds(30).build(),
                    MonitoredService.builder().name("Example.com").url("https://example.com").intervalSeconds(60).build(),
                    MonitoredService.builder().name("GitHub API").url("https://api.github.com").intervalSeconds(120).build()
                ));
            }
        };
    }
}
