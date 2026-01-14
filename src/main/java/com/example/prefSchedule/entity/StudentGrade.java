package com.example.prefSchedule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import validation constraints
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_grades")
public class StudentGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student code cannot be empty")
    @Column(name = "studentCode", nullable = false)
    private String studentCode;

    @NotBlank(message = "Course code cannot be empty")
    @Column(name = "courseCode", nullable = false)
    private String courseCode;

    @NotNull(message = "Grade is required")
    @DecimalMin(value = "1.0", message = "Grade must be at least 1.0")
    @DecimalMax(value = "10.0", message = "Grade cannot exceed 10.0")
    @Column(nullable = false)
    private Double grade;

    @Column(name = "createdAt", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public StudentGrade() {}

    public StudentGrade(String studentCode, String courseCode, Double grade) {
        this.studentCode = studentCode;
        this.courseCode = courseCode;
        this.grade = grade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "StudentGrade{" +
                "id=" + id +
                ", studentCode='" + studentCode + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", grade=" + grade +
                ", createdAt=" + createdAt +
                '}';
    }
}