package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.StudentGrade;
import com.example.prefSchedule.repository.StudentGradeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student-grades")
@Validated
@Tag(name = "Student Grades", description = "APIs for managing student academic performance and grades")
@CrossOrigin(origins = "*")
public class StudentGradeController {

    @Autowired
    private StudentGradeRepository repository;

    @PostMapping
    @Operation(summary = "Create a single student grade")
    public ResponseEntity<?> create(@RequestBody @Valid StudentGrade grade) {
        Optional<StudentGrade> existingGrade = repository.findByStudentCodeAndCourseCode(
                grade.getStudentCode(),
                grade.getCourseCode()
        );

        if (existingGrade.isPresent()) {
            return ResponseEntity
                    .status(409)
                    .body("Grade already exists for student " + grade.getStudentCode() +
                            " at course " + grade.getCourseCode() +
                            ". Use update instead.");
        }

        return ResponseEntity.ok(repository.save(grade));
    }

    @GetMapping
    @Operation(summary = "Get all student grades")
    public ResponseEntity<List<StudentGrade>> getAllGrades() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by database ID")
    public ResponseEntity<StudentGrade> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentCode}")
    @Operation(summary = "Find all grades for a specific student")
    public ResponseEntity<List<StudentGrade>> getByStudent(@PathVariable String studentCode) {
        return ResponseEntity.ok(repository.findByStudentCode(studentCode));
    }

    @GetMapping("/course/{courseCode}")
    @Operation(summary = "Find all grades for a specific course")
    public ResponseEntity<List<StudentGrade>> getByCourse(@PathVariable String courseCode) {
        return ResponseEntity.ok(repository.findByCourseCode(courseCode));
    }

    @GetMapping("/search")
    @Operation(summary = "Find a specific grade entry by student and course codes")
    public ResponseEntity<StudentGrade> getByStudentAndCourse(
            @RequestParam String studentCode,
            @RequestParam String courseCode) {
        return repository.findByStudentCodeAndCourseCode(studentCode, courseCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter/above")
    @Operation(summary = "Find grades equal to or higher than a threshold")
    public ResponseEntity<List<StudentGrade>> getGradesAbove(@RequestParam @DecimalMin("1.0") @DecimalMax("10.0") Double minGrade) {
        return ResponseEntity.ok(repository.findGradesAbove(minGrade));
    }

    @GetMapping("/filter/range")
    @Operation(summary = "Find grades within a specific range")
    public ResponseEntity<List<StudentGrade>> getGradesInRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(repository.findGradesInHistory(min, max));
    }

    @PatchMapping("/{id}/value")
    @Operation(summary = "Update grade value only")
    public ResponseEntity<Void> updateGrade(
            @PathVariable Long id,
            @RequestParam @DecimalMin("1.0") @DecimalMax("10.0") Double newGrade) {
        repository.updateGrade(id, newGrade);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/student-code")
    @Operation(summary = "Update student code for a specific entry")
    public ResponseEntity<Void> updateStudentCode(
            @PathVariable Long id,
            @RequestParam @NotBlank String newStudentCode) {
        repository.updateStudentCode(id, newStudentCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/course-code")
    @Operation(summary = "Update course code for a specific entry")
    public ResponseEntity<Void> updateCourseCode(
            @PathVariable Long id,
            @RequestParam @NotBlank String newCourseCode) {
        repository.updateCourseCode(id, newCourseCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete specific grade by ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/student/{studentCode}")
    @Operation(summary = "Delete all grades associated with a student code")
    public ResponseEntity<Void> deleteByStudent(@PathVariable String studentCode) {
        repository.deleteByStudentCode(studentCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/course/{courseCode}")
    @Operation(summary = "Delete all grades associated with a course code")
    public ResponseEntity<Void> deleteByCourse(@PathVariable String courseCode) {
        repository.deleteByCourseCode(courseCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "Delete specific grade by student and course codes")
    public ResponseEntity<Void> deleteByStudentAndCourse(
            @RequestParam String studentCode,
            @RequestParam String courseCode) {
        repository.deleteByStudentAndCourse(studentCode, courseCode);
        return ResponseEntity.ok().build();
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // swagger recunoaste fisierele incarcate ca multipart doar cu @RequestPart
    @PostMapping(path = "/upload-csv", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk upload grades from a CSV file",
            description = "Format: studentCode, courseCode, gradeValue")
    public ResponseEntity<String> uploadGradesFromCsv(
            @Parameter(description = "CSV file containing student grades", schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file) {

        int savedCount = 0;
        int skippedCount = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String studentCode = data[0].trim();
                    String courseCode = data[1].trim();
                    Double gradeValue = Double.parseDouble(data[2].trim());

                    if (repository.findByStudentCodeAndCourseCode(studentCode, courseCode).isPresent()) {
                        skippedCount++;
                        continue; // Skip this record if it already exists
                    }

                    StudentGrade grade = new StudentGrade();
                    grade.setStudentCode(studentCode);
                    grade.setCourseCode(courseCode);
                    grade.setGrade(gradeValue);

                    repository.save(grade);
                    savedCount++;
                }
            }
            return ResponseEntity.ok(String.format("Upload finished: %d saved, %d skipped (already existed).", savedCount, skippedCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}