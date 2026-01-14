package com.example.prefSchedule.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.*;

@Entity
@Table(name="courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //!!!!!!!!!!!!!!
    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    @NotNull(message = "Type must not be null")
    private CourseType type;

    @NotBlank(message = "Code must not be empty")
    @Size(max = 20)
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank(message = "Abbr must not be empty")
    @Size(max = 30)
    @Column(name = "abbr", unique = true, nullable = false)
    private String abbreviation;

    @NotBlank(message = "Name must not be empty")
    @Size(max = 200)
    @Column(name = "name", nullable = false)
    private String name;

    //incarca instructor nu cand citim obiectul, ci doar cand avem nevoie de el
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="instructor_id", referencedColumnName = "id")
    @NotNull(message = "Instructor must not be null")
    @JsonIgnoreProperties({"teachingCourses", "hibernateLazyInitializer", "handler"})
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pack_id", referencedColumnName = "id")
    //@NotNull(message = "Pack must not be null")
    @JsonIgnoreProperties({"courses", "hibernateLazyInitializer", "handler"}) // Ignora lista de cursuri din interiorul pachetului cand serializam un curs
    private Pack pack;

    @Column(name="group_count")
    @Min(value = 1, message = "Group count must be at least 1")
    private Integer groupCount = 1;

    @Column(name = "description")
    @Size(max = 500,message = "Description is too long")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    @JsonIgnore
    private List<StudentPreference> preferences = new ArrayList<>();

    public enum CourseType {
        COMPULSORY, OPTIONAL
    }

    public Course() {}

    public Course(CourseType type, String code, String name) {
        this.type = type;
        this.code = code;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Pack getPack() {
        return pack;
    }

    public void setPack(Pack pack) {
        this.pack = pack;
    }

    public Integer getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StudentPreference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<StudentPreference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", type=" + type +
                ", code='" + code + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", name='" + name + '\'' +
                ", instructor=" + instructor +
                ", pack=" + pack +
                ", groupCount=" + groupCount +
                ", description='" + description + '\'' +
                ", preferences=" + preferences +
                '}';
    }
}
