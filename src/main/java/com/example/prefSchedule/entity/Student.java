package com.example.prefSchedule.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {
    @NotBlank(message = "Code must not be empty")
    @Column(name="code", nullable = false, unique = true)
    private String code;

    @Min(value = 1, message = "Year should be greater than 0.")
    @Column(name="year", nullable = false)
    private int year;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student")
    @JsonIgnore
    private List<StudentPreference> preferences = new ArrayList<>();

    public Student() {}

    public Student(String code, String name, String email, int year, String username, String password) {
        super(username, name, email, password);
        this.code = code;
        this.year = year;
        this.setRoles(List.of(Role.STUDENT));
    }

    public Student(String code, String name, String email, int year) {
        super(name.toLowerCase().replace(" ", ""), name, email, "temporary");
        this.code = code;
        this.year = year;
        this.setRoles(List.of(Role.STUDENT));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<StudentPreference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<StudentPreference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", year=" + year +
                ", preferences=" + preferences +
                '}';
    }
}
