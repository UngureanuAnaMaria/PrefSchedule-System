package com.example.prefSchedule.controller;

import com.example.prefSchedule.dto.CourseDTO;
import com.example.prefSchedule.dto.CourseSearchCriteriaDTO;
import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.Course.CourseType;
import com.example.prefSchedule.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@Validated
@Tag(name = "Courses", description = "APIs for managing courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
   // @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new course")
    public ResponseEntity<Course> create(@RequestBody @Valid CourseDTO dto) {
        Course course = new Course();
        course.setType(dto.getType());
        course.setCode(dto.getCode());
        course.setAbbreviation(dto.getAbbreviation());
        course.setName(dto.getName());
        course.setGroupCount(dto.getGroupCount());
        course.setDescription(dto.getDescription());

        // folosim service-ul pentru a incarca entitatile complete
        courseService.assignInstructorAndPack(course, dto.getInstructorId(), dto.getPackId());

        return ResponseEntity.ok(courseService.save(course));
    }

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get course by unique code")
    public ResponseEntity<Course> getByCode(@PathVariable String code) {
        return courseService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses by criteria")
    public ResponseEntity<List<Course>> search(CourseSearchCriteriaDTO criteria) {
        return ResponseEntity.ok(courseService.searchCourses(criteria));
    }

    @PutMapping("/{id}/name")
//    @PreAuthorize("hasRole('ADMIN') or (hasRole('INSTRUCTOR') and @courseService.isCourseInstructor(#id, principal.username))")
    @Operation(summary = "Update course name")
    public ResponseEntity<Void> updateName(
            @PathVariable Long id,
            @RequestParam @NotBlank String name) {
        courseService.updateName(id, name);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/code")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course code")
    public ResponseEntity<Void> updateCode(
            @PathVariable Long id,
            @RequestParam @NotBlank String code) {
        courseService.updateCode(id, code);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/group-count")
  //  @PreAuthorize("hasRole('ADMIN') or (hasRole('INSTRUCTOR') and @courseService.isCourseInstructor(#id, principal.username))")
    @Operation(summary = "Update group count")
    public ResponseEntity<Void> updateGroupCount(
            @PathVariable Long id,
            @RequestParam @Min(1) Integer count) {
        courseService.updateGroupCount(id, count);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/instructor")
    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign a new instructor to course")
    public ResponseEntity<Void> updateInstructor(
            @PathVariable Long id,
            @RequestParam @NotNull Long instructorId) {
        courseService.updateInstructor(id, instructorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/code/{code}")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course by code")
    public ResponseEntity<Void> deleteByCode(@PathVariable String code) {
        courseService.deleteByCode(code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/type/{type}")
  //  @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete courses by type")
    public ResponseEntity<Void> deleteByType(@PathVariable CourseType type) {
        courseService.deleteByType(type);
        return ResponseEntity.ok().build();
    }
}