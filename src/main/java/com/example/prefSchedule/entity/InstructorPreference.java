package com.example.prefSchedule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructor_preferences" ,
        uniqueConstraints = {@UniqueConstraint(columnNames =
                {"instructor_id", "course_code", "compulsory_course_abbr"})})
public class InstructorPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instructor_id", nullable = false)
    @NotNull
    private Long instructorId;

    @Column(name = "course_code", nullable = false)
    @NotBlank
    private String courseCode;

    @Column(name = "compulsory_course_abbr", nullable = false)
    @NotBlank
    private String compulsoryCourseAbbr;

    @Column(name = "weight_percentage", nullable = false)
    @NotNull(message = "weightPercentage is required")
    private Double weightPercentage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public InstructorPreference() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCompulsoryCourseAbbr() {
        return compulsoryCourseAbbr;
    }

    public void setCompulsoryCourseAbbr(String compulsoryCourseAbbr) {
        this.compulsoryCourseAbbr = compulsoryCourseAbbr;
    }

    public Double getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(Double weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
