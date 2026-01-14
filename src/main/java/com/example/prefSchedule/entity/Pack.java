package com.example.prefSchedule.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CollectionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Entity
@Table(name="packs")
public class Pack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 6, message = "Year cannot be greater than 6")
    @Column(name="year", nullable = false)
    private int year;

    @Min(value = 1, message = "Semester must be 1 or 2")
    @Max(value = 2, message = "Semester must be 1 or 2")
    @Column(name="semester", nullable = false)
    private int semester;

    @NotBlank(message = "Pack name cannot be empty")
    @Size(max = 100, message = "Name too long")
    @Column(name="name", nullable = false, length = 100)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pack", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("pack")
    private List<Course> courses = new ArrayList<>();

    public Pack() {}

    public Pack(int year, int semester, String name) {
        this.year = year;
        this.semester = semester;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        Map<Long, String> coursesMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getName));
        return "Pack{" +
                "id=" + id +
                ", year=" + year +
                ", semester=" + semester +
                ", name='" + name + '\'' +
                ", courses=" + (courses != null ? coursesMap : "null") +
                '}';
    }
}
