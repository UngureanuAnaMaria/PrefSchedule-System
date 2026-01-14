package com.example.prefSchedule.entity;

import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="instructors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Instructor extends User {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<Course> teachingCourses = new ArrayList<>();

    public Instructor() {}

    public Instructor(String username, String name, String email, String password) {
        super(username, name, email, password);
        this.setRoles(List.of(Role.INSTRUCTOR));
    }

    public Instructor(String name, String email) {
        super(name.toLowerCase().replace(" ", ""), name, email, "temporary");
        this.setRoles(List.of(Role.INSTRUCTOR));
    }
    public List<Course> getTeachingCourses() {
        return teachingCourses;
    }

    public void setTeachingCourses(List<Course> teachingCourses) {
        this.teachingCourses = teachingCourses;
    }

    @Override
    public String toString() {
        Map<Long, String> courses = teachingCourses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getName));

        return "Instructor{" +
                "teachingCourses=" + (teachingCourses != null ? courses : "null") +
                '}';
    }
}
