package com.example.prefSchedule.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchingRequest {
    @NotEmpty(message = "Student list can't be empty")
    @Valid // It validates each StudentPrefs object
    private List<StudentPrefs> students;

    @NotEmpty(message = "Courses list can't be empty")
    @Valid
    private List<CoursePrefs> courses;
}