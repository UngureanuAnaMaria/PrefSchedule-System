package com.example.prefSchedule.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentPrefs {
    @NotBlank(message = "Student code is required")
    private String studentCode;

    //@NotEmpty(message = "Student preferences can't be empty")
    //@Valid
    private List<String> courses;
}