package com.example.prefSchedule.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FinalGradeEvent {
    private String studentCode;
    private String courseCode;
    private String studentName;
    private int year;
    private String courseName;
    private int semester;
    private Double grade;
}
