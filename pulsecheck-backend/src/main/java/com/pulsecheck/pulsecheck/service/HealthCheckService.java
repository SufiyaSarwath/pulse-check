package com.pulsecheck.pulsecheck.service;
import com.pulsecheck.pulsecheck.entity.CheckResult;
import com.pulsecheck.pulsecheck.entity.MonitoredService;
import com.pulsecheck.pulsecheck.repository.CheckResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class HealthCheckService {
    private final CheckResultRepository checkResultRepository;

    public HealthCheckService(CheckResultRepository checkResultRepository) {
        this.checkResultRepository = checkResultRepository;
    }

    @Transactional
    public CheckResult check(MonitoredService service) {
        long startTime = System.currentTimeMillis();
        Integer statusCode = null;
        boolean isUp = false;
        long responseTimeMs = 0;

        try {
            String cleanUrl = service.getUrl().trim();
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(cleanUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                    .header("Accept", "*/*")
                    .GET()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            statusCode = response.statusCode();
            
            // FIX: Accept 2xx (Success) and 3xx (Redirect) as "UP"
            isUp = (statusCode >= 200 && statusCode < 400);
            
        } catch (Exception e) {
            isUp = false;
            System.err.println(" [!] Failed to reach " + service.getName() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            responseTimeMs = System.currentTimeMillis() - startTime;
        }

        try {
            CheckResult result = CheckResult.builder()
                .serviceId(service.getId())
                .statusCode(statusCode)
                .responseTimeMs(responseTimeMs)
                .isUp(isUp)
                .checkedAt(LocalDateTime.now())
                .build();
            return checkResultRepository.save(result);
        } catch (Exception e) {
            return null;
        }
    }
}
