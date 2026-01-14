package com.example.prefSchedule.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchingResponse {
    private boolean success;
    private List<Assignment> assignments;
    private List<String> unassignedStudents;
    private String algorithmUsed;
    private Map<String, Integer> remainingCapacity;
    private String message;
}
