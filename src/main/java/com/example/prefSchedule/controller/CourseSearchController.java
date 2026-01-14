package com.example.prefSchedule.controller;

import com.example.prefSchedule.dto.CourseSearchCriteriaDTO;
import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseSearchController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestBody CourseSearchCriteriaDTO criteria) {
        List<Course> courses = courseService.searchCourses(criteria);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/search/paged")
    public ResponseEntity<List<Course>> searchCoursesPaged(
            @RequestBody CourseSearchCriteriaDTO criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Course> courses = courseService.searchCoursesWithPagination(criteria, page, size);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/search/count")
    public ResponseEntity<Long> countCourses(@RequestBody CourseSearchCriteriaDTO criteria) {
        long count = courseService.countCourses(criteria);
        return ResponseEntity.ok(count);
    }
}