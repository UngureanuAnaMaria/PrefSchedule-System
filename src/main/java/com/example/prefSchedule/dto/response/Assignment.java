package com.example.prefSchedule.dto.response;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Assignment {
    private String studentCode;
    private String courseCode;
    private int studentPreferenceRank;
    private int coursePreferenceRank;
}