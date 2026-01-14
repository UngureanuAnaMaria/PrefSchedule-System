package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.Instructor;
import com.example.prefSchedule.repository.StudentAssignmentRepository;
import com.example.prefSchedule.service.CourseService;
import com.example.prefSchedule.service.InstructorService;
import com.example.prefSchedule.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {
    @Autowired
    private InstructorService service;

    @Autowired private CourseService courseService;
    @Autowired private StudentService studentService;
    @Autowired private StudentAssignmentRepository assignmentRepository;

    @GetMapping("/username/{username}")
    public ResponseEntity<Instructor> getByUsername(@PathVariable String username) {
        return service.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{instructorId}/courses")
    public ResponseEntity<Map<String, List<Course>>> getInstructorCourses(@PathVariable Long instructorId) {
        List<Course> allCourses = courseService.findByInstructorId(instructorId);

        Map<String, List<Course>> response = new HashMap<>();
        response.put("compulsory", allCourses.stream()
                .filter(c -> c.getType() == Course.CourseType.COMPULSORY).collect(Collectors.toList()));
        response.put("optional", allCourses.stream()
                .filter(c -> c.getType() == Course.CourseType.OPTIONAL).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseCode}/students")
    public ResponseEntity<List<?>> getAssignedStudents(@PathVariable String courseCode) {
        Course course = courseService.findByCode(courseCode).orElseThrow();

        if (course.getType() == Course.CourseType.COMPULSORY) {
            // Pentru obligatorii: toți studenții din același an cu pachetul cursului
            int targetYear = course.getPack().getYear();
            return ResponseEntity.ok(studentService.findByYear(targetYear));
        } else {
            // Pentru opționale: doar cei din tabela de final_assignments
            return ResponseEntity.ok(assignmentRepository.findByCourseCode(courseCode));
        }
    }

    // Create: Salvare instructor
    @PostMapping
    public ResponseEntity<Instructor> createInstructor(@RequestBody Instructor instructor) {
        return ResponseEntity.ok(service.save(instructor));
    }

    // Read: Listarea tuturor instructorilor
    @GetMapping
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        return ResponseEntity.ok(service.findAll());
    }

    // Read: Căutare după ID
    @GetMapping("/{id}")
    public ResponseEntity<Instructor> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Read: Căutare după nume
    @GetMapping("/search")
    public ResponseEntity<List<Instructor>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    // Read: Căutare după email
    @GetMapping("/email/{email}")
    public ResponseEntity<Instructor> getByEmail(@PathVariable String email) {
        return service.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update: Actualizare nume
    @PatchMapping("/{id}/name")
    public ResponseEntity<Void> updateName(@PathVariable Long id, @RequestBody String newName) {
        service.updateName(id, newName);
        return ResponseEntity.ok().build();
    }

    // Update: Actualizare email
    @PatchMapping("/{id}/email")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id, @RequestBody String newEmail) {
        service.updateEmail(id, newEmail);
        return ResponseEntity.ok().build();
    }

    // Delete: Ștergere după ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Delete: Ștergere după nume
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        service.deleteByName(name);
        return ResponseEntity.noContent().build();
    }

    // Delete: Ștergere după email
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteByEmail(@PathVariable String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }
}