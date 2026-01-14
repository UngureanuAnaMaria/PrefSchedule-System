package com.example.prefSchedule.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CoursePrefs {
    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotNull(message = "Course capacity is required")
    @Min(value = 1, message = "Course capacity must be at least 1")
    private Integer capacity;

    @NotEmpty(message = "Course preferences can't be empty")
    @Valid
    private List<String> students;
}