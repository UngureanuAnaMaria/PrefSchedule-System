package com.example.prefSchedule.client;

import com.example.prefSchedule.dto.request.MatchingRequest;
import com.example.prefSchedule.dto.response.MatchingResponse;
import com.example.prefSchedule.service.StableMatchMetricsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class StableMatchClient {

    private static final Logger logger = LoggerFactory.getLogger(StableMatchClient.class);

    private final RestClient restClient;
    private final String stableMatchServiceUrl;
    private final StableMatchMetricsService metricsService;

    @Autowired
    public StableMatchClient(
            @Value("${stable-match.service.url:http://localhost:8082}") String stableMatchServiceUrl,
            StableMatchMetricsService metricsService) {
        this.stableMatchServiceUrl = stableMatchServiceUrl;
        this.metricsService = metricsService;

        // Configuram RestClient cu logging la request-uri
        this.restClient = RestClient.builder()
                .baseUrl(stableMatchServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    logger.debug("Making request to: {}", request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    @CircuitBreaker(name = "stableMatchService", fallbackMethod = "fallbackSolve")
    @Retry(name = "stableMatchService")
    @TimeLimiter(name = "stableMatchService")
    public CompletableFuture<MatchingResponse> solveMatchingAsync(MatchingRequest request, String algorithm) {
        return CompletableFuture.supplyAsync(() -> {
            return solveMatchingInternal(request, algorithm);
        });
    }

    @CircuitBreaker(name = "stableMatchService", fallbackMethod = "fallbackSolveSync")
    @Retry(name = "stableMatchService")
    public MatchingResponse solveMatching(MatchingRequest request, String algorithm) {
        return solveMatchingInternal(request, algorithm);
    }


    private MatchingResponse solveMatchingInternal(MatchingRequest request, String algorithm) {
        logger.info("Starting StableMatch algorithm: {}", algorithm);
        metricsService.incrementAlgorithmInvocation(algorithm);

        Timer.Sample timerSample = metricsService.startTimer();

        try {
            logger.debug("Request details - Students: {}, Courses: {}",
                    request.getStudents().size(),
                    request.getCourses().size());

            MatchingResponse response = restClient.post()
                    .uri("/api/matching/solve?algorithm={algorithm}", algorithm)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(MatchingResponse.class)
                    .getBody();

            logger.info("StableMatch algorithm '{}' completed successfully. Assignments: {}",
                    algorithm, response != null ? response.getAssignments().size() : 0);

            return response;

        } catch (Exception e) {
            logger.error("StableMatch algorithm '{}' failed. Error: {}", algorithm, e.getMessage(), e);
            throw e;

        } finally {
            metricsService.stopTimer(timerSample, algorithm);

            double duration = timerSample.stop(metricsService.getTimer(algorithm)); // obține Timer-ul specific algoritmului
            logger.info("✅ StableMatch service responded successfully in {} ms",
                    TimeUnit.NANOSECONDS.toMillis((long) duration));
        }
    }

    public CompletableFuture<MatchingResponse> fallbackSolve(MatchingRequest request,
                                                             String algorithm,
                                                             Throwable throwable) {
        logger.warn("Fallback triggered for StableMatch service. Algorithm: {}, Error: {}",
                algorithm, throwable.getMessage());

        return CompletableFuture.completedFuture(createFallbackResponse(request, algorithm));
    }

    public MatchingResponse fallbackSolveSync(MatchingRequest request,
                                              String algorithm,
                                              Throwable throwable) {
        logger.warn("Fallback (sync) triggered for StableMatch service. Algorithm: {}, Error: {}",
                algorithm, throwable.getMessage());

        return createFallbackResponse(request, algorithm);
    }

    private MatchingResponse createFallbackResponse(MatchingRequest request, String algorithm) {
        logger.info("Creating fallback response for algorithm: {}", algorithm);

        MatchingResponse fallbackResponse = new MatchingResponse();
        fallbackResponse.setSuccess(false);
        fallbackResponse.setAlgorithmUsed("fallback-" + algorithm);
        fallbackResponse.setMessage("StableMatch service unavailable at " + LocalDateTime.now() +
                ". Using fallback response. Please try again later.");
        fallbackResponse.setAssignments(List.of());

        return fallbackResponse;
    }

    @CircuitBreaker(name = "stableMatchService", fallbackMethod = "fallbackGetAssignments")
    public Map<String, Object> getAssignmentsForCourse(String courseCode) {
        logger.info("Getting assignments for course: {}", courseCode);

        try {
            Map<String, Object> assignments = restClient.get()
                    .uri("/api/matching/assignments/{courseCode}", courseCode)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            logger.debug("Successfully retrieved assignments for course: {}", courseCode);
            return assignments;

        } catch (Exception e) {
            logger.error("Failed to get assignments for course {}: {}", courseCode, e.getMessage(), e);
            throw e;
        }
    }

    public Map<String, Object> fallbackGetAssignments(String courseCode, Throwable throwable) {
        logger.warn("Fallback for getAssignments. Course: {}, Error: {}",
                courseCode, throwable.getMessage());

        return Map.of(
                "courseCode", courseCode,
                "error", "Service unavailable",
                "message", "StableMatch service is down",
                "assignments", List.of(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @CircuitBreaker(name = "stableMatchService", fallbackMethod = "fallbackHealthCheck")
    public boolean healthCheck() {
        logger.debug("Performing health check on StableMatch service");

        try {
            Map<String, Object> health = restClient.get()
                    .uri("/actuator/health")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            boolean isUp = health != null && "UP".equals(health.get("status"));

            if (isUp) {
                logger.debug("StableMatch service is UP");
            } else {
                logger.warn("StableMatch service health check returned DOWN");
            }

            return isUp;

        } catch (Exception e) {
            logger.error("Health check failed for StableMatch service: {}", e.getMessage());
            return false;
        }
    }

    public boolean fallbackHealthCheck(Throwable throwable) {
        logger.warn("Health check fallback triggered");
        return false;
    }

    @Override
    public String toString() {
        return "StableMatchClient [baseUrl=" + stableMatchServiceUrl + "]";
    }
}
