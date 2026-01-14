package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.InstructorPreference;
import com.example.prefSchedule.repository.InstructorPreferenceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructor-preferences")
public class InstructorPreferenceController {
    @Autowired
    private InstructorPreferenceRepository preferenceRepository;

    //{
    //  "students": [
    //    {
    //      "studentCode": "S1",
    //      "courses": ["C1", "C2"]
    //    },
    //    {
    //      "studentCode": "S2",
    //      "courses": ["C1", "C2"]
    //    },
    //    {
    //      "studentCode": "S3",
    //      "courses": ["C1", "C2"]
    //    },
    //    {
    //      "studentCode": "S4",
    //      "courses": ["C2", "C1"]
    //    }
    //  ],
    //  "courses": [
    //    {
    //      "courseCode": "C1",
    //      "capacity": 1,
    //      "students": ["S1", "S2", "S3", "S4"]
    //    },
    //    {
    //      "courseCode": "C2",
    //      "capacity": 2,
    //      "students": ["S4", "S1", "S2", "S3"]
    //    }
    //  ]
    //}


    @PostMapping
    @Operation(summary = "Create instructor preference",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Create example",
                                    value = """
                       {
                         "instructorId": 1,
                         "courseCode": "CS101",
                         "compulsoryCourseAbbr": "MATH",
                         "weightPercentage": 100.0
                       }
                       """
                            )
                    )
            ))
    public ResponseEntity<?> create(@Valid @RequestBody InstructorPreference preference) {
        List<InstructorPreference> existingPrefs = preferenceRepository
                .findByCourseCodeAndInstructorId(
                        preference.getCourseCode(),
                        preference.getInstructorId()
                );

        double existingTotalWeight = existingPrefs.stream()
                .mapToDouble(InstructorPreference::getWeightPercentage)
                .sum();

        // validare weight
        if (preference.getWeightPercentage() <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Weight percentage must be greater than 0",
                    "providedWeight", preference.getWeightPercentage()
            ));
        }

        if (preference.getWeightPercentage() > 100) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Weight percentage cannot exceed 100%",
                    "providedWeight", preference.getWeightPercentage(),
                    "maxAllowed", 100.0
            ));
        }

        Optional<InstructorPreference> existingPreferenceWithSameAbbr = existingPrefs.stream()
                .filter(p -> p.getCompulsoryCourseAbbr()
                        .equalsIgnoreCase(preference.getCompulsoryCourseAbbr()))
                .findFirst();

        if (existingPreferenceWithSameAbbr.isPresent()) {
            InstructorPreference existing = existingPreferenceWithSameAbbr.get();

            // calc noul total daca ar face update la weight-ul existent
            double newTotalIfUpdate = existingTotalWeight - existing.getWeightPercentage() + preference.getWeightPercentage();

            if (newTotalIfUpdate > 100.0) {
                Map<String, Object> errorResponse = Map.of(
                        "error", "Cannot update preference - total weight would exceed 100%",
                        "currentTotal", existingTotalWeight,
                        "existingWeight", existing.getWeightPercentage(),
                        "newWeight", preference.getWeightPercentage(),
                        "projectedTotal", newTotalIfUpdate,
                        "maxAllowed", 100.0,
                        "remainingCapacity", 100.0 - (existingTotalWeight - existing.getWeightPercentage()),
                        "message", String.format("Can only add up to %.2f%% more weight",
                                100.0 - (existingTotalWeight - existing.getWeightPercentage()))
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            existing.setWeightPercentage(preference.getWeightPercentage());
            InstructorPreference updated = preferenceRepository.save(existing);

            double newTotalAfterUpdate = existingPrefs.stream()
                    .mapToDouble(p -> p.getId().equals(updated.getId()) ?
                            updated.getWeightPercentage() : p.getWeightPercentage())
                    .sum();

            Map<String, Object> successResponse = Map.of(
                    "message", "Preference updated successfully",
                    "preference", updated,
                    "totalWeight", newTotalAfterUpdate,
                    "remainingCapacity", 100.0 - newTotalAfterUpdate,
                    "isComplete", Math.abs(newTotalAfterUpdate - 100.0) < 0.001
            );
            return ResponseEntity.ok(successResponse);
        }

        double projectedTotal = existingTotalWeight + preference.getWeightPercentage();

        if (projectedTotal > 100.0) {
            Map<String, Object> errorResponse = Map.of(
                    "error", "Cannot add preference - total weight would exceed 100%",
                    "currentTotal", existingTotalWeight,
                    "newWeight", preference.getWeightPercentage(),
                    "projectedTotal", projectedTotal,
                    "maxAllowed", 100.0,
                    "remainingCapacity", 100.0 - existingTotalWeight,
                    "message", String.format("Can only add up to %.2f%% more weight",
                            100.0 - existingTotalWeight)
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (Math.abs(existingTotalWeight - 100.0) < 0.001) {
            Map<String, Object> errorResponse = Map.of(
                    "error", "Cannot add preference - total weight is already 100%",
                    "currentTotal", existingTotalWeight,
                    "message", "Total weight has reached 100%. Consider updating existing preferences instead."
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        InstructorPreference savedPreference = preferenceRepository.save(preference);

        double newTotalAfterAdd = existingTotalWeight + savedPreference.getWeightPercentage();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Preference created successfully");
        response.put("preference", savedPreference);
        response.put("totalWeight", newTotalAfterAdd);
        response.put("remainingCapacity", 100.0 - newTotalAfterAdd);
        response.put("isComplete", Math.abs(newTotalAfterAdd - 100.0) < 0.001);
        response.put("existingPreferencesCount", existingPrefs.size());

        if (Math.abs(newTotalAfterAdd - 100.0) < 0.001) {
            response.put("suggestion", "Total weight reached 100% - no more preferences can be added");
        } else if (newTotalAfterAdd > 90.0) {
            response.put("suggestion",
                    String.format("Only %.2f%% weight remaining", 100.0 - newTotalAfterAdd));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create or update multiple instructor preferences",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Batch example",
                                    value = """
                                       [
                                         {
                                           "instructorId": 1,
                                           "courseCode": "CS101",
                                           "compulsoryCourseAbbr": "MATH",
                                           "weightPercentage": 40.0
                                         },
                                         {
                                           "instructorId": 1,
                                           "courseCode": "CS101",
                                           "compulsoryCourseAbbr": "PHYSICS",
                                           "weightPercentage": 60.0
                                         }
                                       ]
                                       """
                            )
                    )
            ))
    public ResponseEntity<?> createBatch(@Valid @RequestBody List<InstructorPreference> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Preferences list cannot be empty",
                    "message", "Please provide at least one preference"
            ));
        }

        // (instructorId, courseCode)
        Map<String, List<InstructorPreference>> preferencesByInstructorAndCourse = preferences.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getInstructorId() + "|" + p.getCourseCode(),
                        Collectors.toList()
                ));

        List<InstructorPreference> allPreferencesToSave = new ArrayList<>();
        List<Map<String, Object>> validationResults = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Procesează fiecare grup (instructor + course)
        for (Map.Entry<String, List<InstructorPreference>> entry : preferencesByInstructorAndCourse.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            Long instructorId = Long.parseLong(keyParts[0]);
            String courseCode = keyParts[1];
            List<InstructorPreference> groupPreferences = entry.getValue();

            // Găsește preferințele existente pentru acest grup
            List<InstructorPreference> existingPrefs = preferenceRepository
                    .findByCourseCodeAndInstructorId(courseCode, instructorId);

            double existingTotalWeight = existingPrefs.stream()
                    .mapToDouble(InstructorPreference::getWeightPercentage)
                    .sum();

            // Creează o mapă a preferințelor existente după abbreviation pentru acces rapid
            Map<String, InstructorPreference> existingByAbbr = existingPrefs.stream()
                    .collect(Collectors.toMap(
                            InstructorPreference::getCompulsoryCourseAbbr,
                            p -> p,
                            (p1, p2) -> p1
                    ));

            // Procesează fiecare preferință din grup
            for (InstructorPreference pref : groupPreferences) {
                // Resetează ID-ul pentru noile preferințe
                if (pref.getId() == null || pref.getId() == 0) {
                    pref.setId(null);
                    pref.setCreatedAt(null);
                    pref.setUpdatedAt(null);
                }

                // Validare weight basic
                if (pref.getWeightPercentage() <= 0) {
                    errors.add(String.format(
                            "Weight percentage must be greater than 0 for %s (instructorId: %d, course: %s)",
                            pref.getCompulsoryCourseAbbr(), instructorId, courseCode
                    ));
                    continue;
                }

                if (pref.getWeightPercentage() > 100) {
                    errors.add(String.format(
                            "Weight percentage cannot exceed 100%% for %s (instructorId: %d, course: %s)",
                            pref.getCompulsoryCourseAbbr(), instructorId, courseCode
                    ));
                    continue;
                }

                // Verifică dacă există deja aceeași abreviere
                InstructorPreference existing = existingByAbbr.get(pref.getCompulsoryCourseAbbr());

                if (existing != null) {
                    // Update la preferința existentă
                    double weightToAdd = pref.getWeightPercentage() - existing.getWeightPercentage();
                    double projectedTotal = existingTotalWeight + weightToAdd;

                    if (projectedTotal > 100.0) {
                        errors.add(String.format(
                                "Cannot update %s - total weight would exceed 100%% (current: %.2f%%, change: %.2f%%, projected: %.2f%%)",
                                pref.getCompulsoryCourseAbbr(), existingTotalWeight, weightToAdd, projectedTotal
                        ));
                        continue;
                    }

                    // Actualizează weight-ul preferinței existente
                    existing.setWeightPercentage(pref.getWeightPercentage());
                    existingTotalWeight = projectedTotal; // Actualizează totalul pentru următoarele calcule
                    allPreferencesToSave.add(existing);

                    validationResults.add(Map.of(
                            "action", "UPDATED",
                            "abbreviation", pref.getCompulsoryCourseAbbr(),
                            "oldWeight", existing.getWeightPercentage() - weightToAdd,
                            "newWeight", pref.getWeightPercentage(),
                            "message", String.format("Updated %s from %.2f%% to %.2f%%",
                                    pref.getCompulsoryCourseAbbr(),
                                    existing.getWeightPercentage() - weightToAdd,
                                    pref.getWeightPercentage())
                    ));
                } else {
                    // Nouă preferință
                    double projectedTotal = existingTotalWeight + pref.getWeightPercentage();

                    if (projectedTotal > 100.0) {
                        errors.add(String.format(
                                "Cannot add %s - total weight would exceed 100%% (current: %.2f%%, new: %.2f%%, projected: %.2f%%)",
                                pref.getCompulsoryCourseAbbr(), existingTotalWeight, pref.getWeightPercentage(), projectedTotal
                        ));
                        continue;
                    }

                    if (Math.abs(existingTotalWeight - 100.0) < 0.001) {
                        errors.add(String.format(
                                "Cannot add %s - total weight is already 100%%",
                                pref.getCompulsoryCourseAbbr()
                        ));
                        continue;
                    }

                    allPreferencesToSave.add(pref);
                    existingTotalWeight = projectedTotal; // Actualizează totalul pentru următoarele calcule

                    validationResults.add(Map.of(
                            "action", "CREATED",
                            "abbreviation", pref.getCompulsoryCourseAbbr(),
                            "weight", pref.getWeightPercentage(),
                            "message", String.format("Created new preference for %s with %.2f%% weight",
                                    pref.getCompulsoryCourseAbbr(), pref.getWeightPercentage())
                    ));
                }
            }
        }

        // Dacă există erori de validare, returnează-le
        if (!errors.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Validation failed for some preferences");
            errorResponse.put("errors", errors);
            errorResponse.put("totalErrors", errors.size());
            errorResponse.put("validPreferencesCount", validationResults.size());
            errorResponse.put("validationResults", validationResults);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Salvează toate preferințele valide
        List<InstructorPreference> savedPreferences;
        try {
            savedPreferences = preferenceRepository.saveAll(allPreferencesToSave);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to save preferences",
                    "message", e.getMessage(),
                    "preferencesCount", allPreferencesToSave.size()
            ));
        }

        // Calculează statistici finale după salvare
        Map<String, Object> finalStats = new HashMap<>();
        for (Map.Entry<String, List<InstructorPreference>> entry : preferencesByInstructorAndCourse.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            Long instructorId = Long.parseLong(keyParts[0]);
            String courseCode = keyParts[1];

            List<InstructorPreference> finalPrefs = preferenceRepository
                    .findByCourseCodeAndInstructorId(courseCode, instructorId);

            double finalTotalWeight = finalPrefs.stream()
                    .mapToDouble(InstructorPreference::getWeightPercentage)
                    .sum();

            Map<String, Object> stats = Map.of(
                    "instructorId", instructorId,
                    "courseCode", courseCode,
                    "totalWeight", finalTotalWeight,
                    "remainingCapacity", 100.0 - finalTotalWeight,
                    "isComplete", Math.abs(finalTotalWeight - 100.0) < 0.001,
                    "preferencesCount", finalPrefs.size(),
                    "preferences", finalPrefs.stream()
                            .map(p -> Map.of(
                                    "abbreviation", p.getCompulsoryCourseAbbr(),
                                    "weight", p.getWeightPercentage()
                            ))
                            .collect(Collectors.toList())
            );

            finalStats.put(instructorId + "|" + courseCode, stats);
        }

        // Pregătește răspunsul final
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch operation completed successfully");
        response.put("totalProcessed", preferences.size());
        response.put("successfullySaved", savedPreferences.size());
        response.put("validationResults", validationResults);
        response.put("statistics", finalStats);
        response.put("timestamp", LocalDateTime.now());

        // Adaugă un mesaj de recomandare dacă totalul este aproape de 100%
        finalStats.forEach((key, statsMap) -> {
            Map<String, Object> stats = (Map<String, Object>) statsMap;
            double totalWeight = (Double) stats.get("totalWeight");
            if (Math.abs(totalWeight - 100.0) < 0.001) {
                response.put("recommendation",
                        "Some groups have reached 100% total weight - no more preferences can be added");
            }
        });

        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<InstructorPreference>> getPreferencesForCourse(
            @PathVariable String courseCode) {
        return ResponseEntity.ok(preferenceRepository.findByCourseCode(courseCode));
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<InstructorPreference>> getPreferencesForInstructor(
            @PathVariable Long instructorId) {
        return ResponseEntity.ok(preferenceRepository.findByInstructorId(instructorId));
    }

    @GetMapping("/weights/{courseCode}")
    public ResponseEntity<Map<String, Object>> getWeightsForCourse(
            @PathVariable String courseCode) {
        List<InstructorPreference> prefs = preferenceRepository.findByCourseCode(courseCode);
        double totalWeight = prefs.stream()
                .mapToDouble(InstructorPreference::getWeightPercentage)
                .sum();

        return ResponseEntity.ok(Map.of(
                "courseCode", courseCode,
                "weights", prefs,
                "totalWeight", totalWeight,
                "normalized", totalWeight == 100
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        preferenceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
