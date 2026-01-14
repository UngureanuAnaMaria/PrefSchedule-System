package com.example.prefSchedule.controller;

import com.example.prefSchedule.dto.PackDTO;
import com.example.prefSchedule.dto.response.MatchingResponse;
import com.example.prefSchedule.entity.Pack;
import com.example.prefSchedule.entity.StudentAssignment;
import com.example.prefSchedule.repository.StudentAssignmentRepository;
import com.example.prefSchedule.service.PackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packs")
@Validated
@Tag(name="Packs", description = "APIs for managing course packs")
@CrossOrigin(origins = "*")
public class PackController {

    @Autowired
    private PackService service;

    @Autowired
    private StudentAssignmentRepository assignmentRepository;

    @PostMapping
    @Operation(summary = "Create a new course pack")
    public ResponseEntity<Pack> create(@RequestBody @Valid PackDTO dto) {
        Pack pack = new Pack();
        pack.setYear(dto.getYear());
        pack.setSemester(dto.getSemester());
        pack.setName(dto.getName());

        return ResponseEntity.ok(service.save(pack));
    }

    @GetMapping
    @Operation(summary = "Get all packs")
    public ResponseEntity<List<Pack>> getAllPacks() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pack by ID")
    public ResponseEntity<Pack> getPackById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get packs by name search")
    public ResponseEntity<List<Pack>> getPacksByName(@PathVariable String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Get packs by year")
    public ResponseEntity<List<Pack>> getPacksByYear(@PathVariable @Min(1) @Max(6) Integer year) {
        return ResponseEntity.ok(service.findByYear(year));
    }

    @GetMapping("/semester/{semester}")
    @Operation(summary = "Get packs by semester")
    public ResponseEntity<List<Pack>> getPacksBySemester(@PathVariable @Min(1) @Max(2) Integer semester) {
        return ResponseEntity.ok(service.findBySemester(semester));
    }

    @GetMapping("/year-and-semester")
    @Operation(summary = "Get packs by year and semester")
    public ResponseEntity<List<Pack>> getPacksByYearAndSemester(
            @RequestParam @Min(1) @Max(6) Integer year,
            @RequestParam @Min(1) @Max(2) Integer semester) {
        return ResponseEntity.ok(service.findByYearAndSemester(year, semester));
    }

    @PutMapping("/{id}/name")
    @Operation(summary = "Update pack name")
    public ResponseEntity<Void> updateName(
            @PathVariable Long id,
            @RequestParam @NotBlank String name) {
        service.updateName(id, name);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/year")
    @Operation(summary = "Update pack year")
    public ResponseEntity<Void> updateYear(
            @PathVariable Long id,
            @RequestParam @Min(1) @Max(6) Integer year) {
        service.updateYear(id, year);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/semester")
    @Operation(summary = "Update pack semester")
    public ResponseEntity<Void> updateSemester(
            @PathVariable Long id,
            @RequestParam @Min(1) @Max(2) Integer semester) {
        service.updateSemester(id, semester);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pack by ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/name/{name}")
    @Operation(summary = "Delete packs by name")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        service.deleteByName(name);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/year/{year}")
    @Operation(summary = "Delete packs by year")
    public ResponseEntity<Void> deleteByYear(@PathVariable @Min(1) @Max(6) Integer year) {
        service.deleteByYear(year);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/semester/{semester}")
    @Operation(summary = "Delete packs by semester")
    public ResponseEntity<Void> deleteBySemester(@PathVariable @Min(1) @Max(2) Integer semester) {
        service.deleteBySemester(semester);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/year-and-semester")
    @Operation(summary = "Delete packs by year and semester")
    public ResponseEntity<Void> deleteByYearAndSemester(
            @RequestParam @Min(1) @Max(6) Integer year,
            @RequestParam @Min(1) @Max(2) Integer semester) {
        service.deleteByYearAndSemester(year, semester);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/match")
    @Operation(summary = "Run matching algorithm and save results for a specific pack")
    public ResponseEntity<MatchingResponse> runMatching(@PathVariable Long id) {
        try {
            MatchingResponse response = service.matchAndSave(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            MatchingResponse errorResponse = new MatchingResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/assignments/student/{studentCode}")
    @Operation(summary = "Get all course assignments for a specific student")
    public ResponseEntity<List<StudentAssignment>> getAssignmentsByStudent(@PathVariable String studentCode) {
        List<StudentAssignment> assignments = assignmentRepository.findByStudentCode(studentCode);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}/assignments")
    @Operation(summary = "Get all final assignments for a specific pack")
    public ResponseEntity<List<StudentAssignment>> getAssignmentsByPack(@PathVariable Long id) {
        List<StudentAssignment> assignments = assignmentRepository.findByPackId(id);
        return ResponseEntity.ok(assignments);
    }
}