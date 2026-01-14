package com.example.prefSchedule.dto;

import com.example.prefSchedule.entity.Course.CourseType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    @NotNull(message = "Course type is required")
    private CourseType type;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Code must be at most 20 characters")
    private String code;

    @NotBlank(message = "Abbreviation is required")
    @Size(max = 30, message = "Abbreviation must be at most 30 characters")
    private String abbreviation;

    @NotBlank(message = "Course name is required")
    @Size(max = 200, message = "Name is too long")
    private String name;

    @NotNull(message = "Instructor ID is required")
    private Long instructorId;

    @NotNull(message = "Pack ID is required")
    private Long packId;

    @Min(value = 1, message = "Group count must be at least 1")
    private Integer groupCount;

    @Size(max = 500, message = "Description is too long")
    private String description;
}