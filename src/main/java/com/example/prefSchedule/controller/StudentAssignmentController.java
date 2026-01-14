package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.StudentAssignment;
import com.example.prefSchedule.repository.StudentAssignmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@Tag(name = "Assignments", description = "Endpoints for viewing final student course allocations")
@CrossOrigin(origins = "*")
public class StudentAssignmentController {

    @Autowired
    private StudentAssignmentRepository repository;

    @GetMapping("/student/{studentCode}")
    @Operation(summary = "Get all assignments for a specific student")
    public ResponseEntity<List<StudentAssignment>> getByStudent(@PathVariable String studentCode) {
        List<StudentAssignment> assignments = repository.findByStudentCode(studentCode);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/pack/{packId}")
    @Operation(summary = "Get all assignments for a specific course pack")
    public ResponseEntity<List<StudentAssignment>> getByPack(@PathVariable Long packId) {
        List<StudentAssignment> assignments = repository.findByPackId(packId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<StudentAssignment>> getByCourseCode(@PathVariable String courseCode) {
        return ResponseEntity.ok(repository.findByCourseCode(courseCode));
    }
}