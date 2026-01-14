package com.example.prefSchedule.controller;

import com.example.prefSchedule.dto.request.MatchingRequest;
import com.example.prefSchedule.dto.response.MatchingResponse;
import com.example.prefSchedule.client.StableMatchClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stable-match")
@Tag(name = "StableMatch Integration", description = "Endpoints for integrating with StableMatch service")
public class StableMatchController {

    private static final Logger logger = LoggerFactory.getLogger(StableMatchController.class);

    @Autowired
    private StableMatchClient stableMatchServiceClient;

//    {
//      "students": [
//        {
//          "studentCode": "S001",
//          "courses": ["CS101", "MATH101", "PHYS101"]
//        },
//        {
//          "studentCode": "S002",
//          "courses": ["MATH101", "CS101", "PHYS101"]
//        },
//        {
//          "studentCode": "S003",
//          "courses": ["PHYS101", "CS101", "MATH101"]
//        }
//      ],
//      "courses": [
//        {
//          "courseCode": "CS101",
//          "capacity": 2,
//          "students": ["S001", "S002", "S003"]
//        },
//        {
//          "courseCode": "MATH101",
//          "capacity": 1,
//          "students": ["S002", "S001", "S003"]
//        },
//        {
//          "courseCode": "PHYS101",
//          "capacity": 1,
//          "students": ["S003", "S001", "S002"]
//        }
//      ]
//    }

    @PostMapping("/solve")
    @Operation(summary = "Invoke StableMatch service to solve matching")
    public CompletableFuture<ResponseEntity<MatchingResponse>> solveMatching(
            @RequestBody MatchingRequest request,
            @RequestParam(defaultValue = "gale-shapley") String algorithm) {

        logger.info("Received request to solve matching with algorithm: {}", algorithm);

        return stableMatchServiceClient.solveMatchingAsync(request, algorithm)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        logger.info("Matching solved successfully. Algorithm used: {}", response.getAlgorithmUsed());
                        return ResponseEntity.ok(response);
                    } else {
                        logger.warn("Matching failed. Response: {}", response);
                        return ResponseEntity.status(500).body(response);
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Error solving matching: {}", ex.getMessage());
                    MatchingResponse errorResponse = new MatchingResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Internal server error: " + ex.getMessage());
                    return ResponseEntity.status(500).body(errorResponse);
                });
    }

    @PostMapping("/solve-sync")
    @Operation(summary = "Invoke StableMatch service synchronously")
    public ResponseEntity<MatchingResponse> solveMatchingSync(
            @RequestBody MatchingRequest request,
            @RequestParam(defaultValue = "gale-shapley") String algorithm) {

        logger.info("Received sync request to solve matching with algorithm: {}", algorithm);

        try {
            MatchingResponse response = stableMatchServiceClient.solveMatching(request, algorithm);

            if (response.isSuccess()) {
                logger.info("Matching solved successfully (sync). Algorithm used: {}", response.getAlgorithmUsed());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Matching failed (sync). Response: {}", response);
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            logger.error("Error solving matching (sync): {}", e.getMessage());
            MatchingResponse errorResponse = new MatchingResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/assignments/{courseCode}")
    @Operation(summary = "Get assignments for a course from StableMatch")
    public ResponseEntity<Map<String, Object>> getAssignmentsForCourse(@PathVariable String courseCode) {
        logger.info("Getting assignments for course: {}", courseCode);

        Map<String, Object> assignments = stableMatchServiceClient.getAssignmentsForCourse(courseCode);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/health")
    @Operation(summary = "Check health of StableMatch service")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean isHealthy = stableMatchServiceClient.healthCheck();

        Map<String, Object> response = Map.of(
                "service", "StableMatch",
                "status", isHealthy ? "UP" : "DOWN",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "url", stableMatchServiceClient.toString()
        );

        return ResponseEntity.ok(response);
    }
}