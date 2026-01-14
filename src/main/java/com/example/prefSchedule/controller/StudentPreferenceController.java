package com.example.prefSchedule.controller;

import com.example.prefSchedule.dto.StudentPreferenceRequestDTO;
import com.example.prefSchedule.dto.StudentPreferenceResponseDTO;
import com.example.prefSchedule.exception.StudentPreferenceException;
import com.example.prefSchedule.service.StudentPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//http://localhost:8080/swagger-ui.html
//http://localhost:8080/v3/api-docs

//global exception handler should try!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

@RestController
@RequestMapping("/api/student-preferences")
@Validated
@Tag(name="Student Preferences", description = "APIs for managing student course preferences")
@CrossOrigin(origins = "*")
public class StudentPreferenceController {
    @Autowired
    private StudentPreferenceService service;

    //ETag -> hash/version identifica starea unei resurse
    //If-None-Match: Header cerere - clientul trimite ETag-ul pe care il are;
    // daca resursa nu s-a modificat, serverul returneaza 304 (Not Modified)
    //Bean Validation -> POST/PUT, nu GET
    //409 Confict!!!!!!!!!!!!!
    //ResponseEntity<?> -> raspuns HTTP(poate include header, body, status)
    //@Parameter -> documenteaza parametrii pentru swagger
    //@Valid -> va valida adnotari precum NotNull, Min, etc
    //content = @Content(schema = @Schema(implementation = StudentPreferenceResponseDTO.class)) ->
    // content -> structura raspuns HTTP, schema -> tipul DTO returnat
    @PostMapping
    @Operation(summary = "Create a new student preference",
            description = "Create a new student course preference with validation for course and " +
                    "student year being the same and unique student-course combination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference created successfully",
                    content = @Content(schema = @Schema(implementation = StudentPreferenceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Student or course not found"),
            @ApiResponse(responseCode = "409", description = "Preference already exists or validation failed")

    })
    public ResponseEntity<?> create(@Parameter(description = "Preference data", required = true)
                                    @RequestBody @Valid StudentPreferenceRequestDTO request) {
        try {
            StudentPreferenceResponseDTO savedPreference = service.save(request);
            //ok si badRequest -> genereaza automat status code, ele sunt shortcut uri
            return ResponseEntity.ok()
                    .eTag(savedPreference.getETag())
                    .body(savedPreference);
        } catch (StudentPreferenceException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating preference: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all student preferences")
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getAll() {
        List<StudentPreferenceResponseDTO> preferences = service.findAll();
        return ResponseEntity.ok().body(preferences);
    }

    @GetMapping(value = "/id/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get preference by ID", description = "Retrieve a specific preference by its ID with ETag support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference found"),
            @ApiResponse(responseCode = "304", description = "Preference not modified"),
            @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    public ResponseEntity<StudentPreferenceResponseDTO> getById(@Parameter(description="Student Preference Id", required = true)
                                                                @PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false)
                                                                String ifNoneMatch) {
        Optional<StudentPreferenceResponseDTO> preferenceOptional = service.findById(id);
        if(!preferenceOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        StudentPreferenceResponseDTO preference = preferenceOptional.get();

        String eTag = preference.getETag();
        if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(eTag)
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(eTag)
                .body(preference);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get preferences by student ID")
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getByStudentId(@PathVariable Long studentId) {
        List<StudentPreferenceResponseDTO> preferences = service.findByStudentId(studentId);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get preferences by course ID")
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getByCourseId(@PathVariable Long courseId) {
        List<StudentPreferenceResponseDTO> preferences = service.findByCourseId(courseId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/{id}/priority")
    @Operation(summary = "Update preference priority")
    public ResponseEntity<StudentPreferenceResponseDTO> updatePriority(
            @PathVariable Long id,
            @RequestParam @Min(1) @Max(10) Integer priority) {
        StudentPreferenceResponseDTO updated = service.updatePriority(id, priority);
        return ResponseEntity.ok().eTag(updated.getETag()).body(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete preference by ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/student/{studentId}")
    @Operation(summary = "Delete all preferences for a student")
    public ResponseEntity<Void> deleteByStudentId(@PathVariable Long studentId) {
        service.deleteByStudentId(studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/course/{courseId}")
    @Operation(summary = "Delete all preferences for a course")
    public ResponseEntity<Void> deleteByCourseId(@PathVariable Long courseId) {
        service.deleteByCourseId(courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/student-and-course")
    @Operation(summary = "Delete specific preference by student and course IDs")
    public ResponseEntity<Void> deleteByStudentAndCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        service.deleteByStudentIdAndCourseId(studentId, courseId);
        return ResponseEntity.ok().build();
    }
}