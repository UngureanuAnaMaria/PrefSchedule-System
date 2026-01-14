package com.example.prefSchedule.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EnrichedGradeEvent {
    private String studentCode;
    private String courseCode;
    private String studentName;
    private int year;
    private Double grade;
}
