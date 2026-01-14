package com.example.prefSchedule.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackDTO {
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 6, message = "Year cannot be greater than 6")
    private int year;

    @Min(value = 1, message = "Semester must be 1 or 2")
    @Max(value = 2, message = "Semester must be 1 or 2")
    private int semester;

    @NotBlank(message = "Pack name is required")
    @Size(max = 100, message = "Name is too long")
    private String name;
}