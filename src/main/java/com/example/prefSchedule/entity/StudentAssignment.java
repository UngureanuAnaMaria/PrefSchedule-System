package com.example.prefSchedule.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

//prb la stergere student
@Entity
@Table(name = "final_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentCode;

    @Column(nullable = false)
    private String courseCode;

    private Integer studentPreferenceRank;
    private Integer coursePreferenceRank;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // Referință către pachet pentru a știi din ce sesiune de matching face parte
    private Long packId;
}