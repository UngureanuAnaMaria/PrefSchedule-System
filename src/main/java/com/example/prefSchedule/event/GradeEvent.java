package com.example.prefSchedule.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GradeEvent {
    private String studentCode;
    private String courseCode;
    private Double grade;
}