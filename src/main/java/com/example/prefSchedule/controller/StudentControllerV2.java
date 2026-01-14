package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/students")
@Validated
@Tag(name="Students", description = "APIs for managing students with HATEOAS support")
public class StudentControllerV2 {
    @Autowired
    private StudentService service;

    //EntityModel permite adaugarea de links
    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<EntityModel<Student>> create(@RequestBody @Valid Student student) {
        try {
            if (student.getName() == null || student.getEmail() == null || student.getCode() == null) {
                return ResponseEntity.badRequest().build();
            }

            Student savedStudent = service.save(student);
            //model care wrap savedStudent
            EntityModel<Student> resource = EntityModel.of(savedStudent);
            addStudentLinks(resource, savedStudent.getId());
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all students")
    public ResponseEntity<CollectionModel<EntityModel<Student>>> getAll() {
        List<Student> students = service.findAll();

        List<EntityModel<Student>> resources = students.stream()
                .map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Student>> collection = CollectionModel.of(resources);
        collection.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withSelfRel());
        collection.add(linkTo(methodOn(StudentControllerV2.class).create(null)).withRel("create-student"));

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<EntityModel<Student>> getById(@PathVariable Long id) {
        Optional<Student> student = service.findById(id);
        //map apartine lui Optional
        //s->ResponseEntity.ok(s) <-> ResponseEntity::ok
        return student.map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get student by unique code")
    public ResponseEntity<EntityModel<Student>> getByCode(@PathVariable String code) {
        Optional<Student> student = service.findByCode(code);
        return student.map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get student by email")
    public ResponseEntity<EntityModel<Student>> getByEmail(@PathVariable String email) {
        Optional<Student> student = service.findByEmail(email);
        return student.map(s ->  {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get students by name")
    public ResponseEntity<CollectionModel<EntityModel<Student>>> getByName(@PathVariable String name) {
        List<Student> students = service.findByName(name);
        List<EntityModel<Student>> resources = students.stream()
                .map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Student>> collection = CollectionModel.of(resources);
        collection.add(linkTo(methodOn(StudentControllerV2.class).getByName(name)).withSelfRel());
        collection.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Get students by study year")
    public ResponseEntity<CollectionModel<EntityModel<Student>>> getByYear(@PathVariable int year) {
        List<Student> students = service.findByYear(year);
        List<EntityModel<Student>> resources = students.stream()
                .map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Student>> collection = CollectionModel.of(resources);
        collection.add(linkTo(methodOn(StudentControllerV2.class).getByYear(year)).withSelfRel());
        collection.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get student profile by username (used after login)")
    public ResponseEntity<EntityModel<Student>> getByUsername(@PathVariable String username) {
        return service.findByUsername(username) // Asigură-te că StudentService are findByUsername
                .map(s -> {
                    EntityModel<Student> resource = EntityModel.of(s);
                    addStudentLinks(resource, s.getId());
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //endpoint pt update cu @RequestBody Student studentPartial!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @PutMapping("/{id}/name")
    @Operation(summary = "Update student name")
    public ResponseEntity<EntityModel<Map<String, Object>>> updateName(
            @PathVariable Long id,
            @RequestParam @NotBlank(message = "Name must not be empty") String name) {

        service.updateName(id, name);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getById(id)).withRel("student"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}/code")
    @Operation(summary = "Update student code")
    public ResponseEntity<EntityModel<Map<String, Object>>> updateCode(
            @PathVariable Long id,
            @RequestParam @NotBlank(message = "Code must not be empty") String code) {

        service.updateCode(id, code);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getById(id)).withRel("student"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}/email")
    @Operation(summary = "Update student email")
    public ResponseEntity<EntityModel<Map<String, Object>>> updateEmail(
            @PathVariable Long id,
            @RequestParam
            @NotBlank(message = "Email must not be empty")
            @Email(message = "Email should have format name@domain.com") String email) {

        service.updateEmail(id, email);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getById(id)).withRel("student"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}/year")
    @Operation(summary = "Update student year")
    public ResponseEntity<EntityModel<Map<String, Object>>> updateYear(
            @PathVariable Long id,
            @RequestParam @Min(value = 1, message = "Year should be greater than 0.") int year) {

        service.updateYear(id, year);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getById(id)).withRel("student"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }


    @DeleteMapping("/id/{id}")
    @Operation(summary = "Delete student by ID")
    public ResponseEntity<EntityModel<Map<String, Object>>> deleteById(@PathVariable Long id) {
        service.deleteById(id);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).create(null)).withRel("create-student"));

        return ResponseEntity.ok(resource);
    }

    //DE CE NU SIMPLU   @DeleteMapping("/{name}") ???????? -> pt ca SPRING nu poate sa distinga
    //intre DeleteMapping("/{name}")  si   @DeleteMapping("/{id}")
    @DeleteMapping("/name/{name}")
    @Operation(summary = "Delete students by name")
    public ResponseEntity<EntityModel<Map<String, Object>>> deleteByName(@PathVariable String name) {
        service.deleteByName(name);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/email/{email}")
    @Operation(summary = "Delete student by email")
    public ResponseEntity<EntityModel<Map<String, Object>>> deleteByEmail(@PathVariable String email) {
        service.deleteByEmail(email);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/code/{code}")
    @Operation(summary = "Delete student by code")
    public ResponseEntity<EntityModel<Map<String, Object>>> deleteByCode(@PathVariable String code) {
        service.deleteByCode(code);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/year/{year}")
    @Operation(summary = "Delete students by year")
    public ResponseEntity<EntityModel<Map<String, Object>>> deleteByYear(@PathVariable int year) {
        service.deleteByYear(year);

        EntityModel<Map<String, Object>> resource = EntityModel.of(Collections.emptyMap());
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));

        return ResponseEntity.ok(resource);
    }


    private void addStudentLinks(EntityModel<Student> resource, Long id) {
        //self link
        resource.add(linkTo(methodOn(StudentControllerV2.class).getById(id)).withSelfRel());

        //related resources -> link catre resurse
        resource.add(linkTo(methodOn(StudentPreferenceController.class).getByStudentId(id)).withRel("preferences"));

        //update -> link actiune
        resource.add(linkTo(methodOn(StudentControllerV2.class).updateName(id, null)).withRel("update-name"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).updateCode(id, null)).withRel("update-code"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).updateEmail(id, null)).withRel("update-email"));
        resource.add(linkTo(methodOn(StudentControllerV2.class).updateYear(id, 0)).withRel("update-year"));

        //delete -> link actiune
        resource.add(linkTo(methodOn(StudentControllerV2.class).deleteById(id)).withRel("delete"));

        //collection links -> link navigatie
        resource.add(linkTo(methodOn(StudentControllerV2.class).getAll()).withRel("all-students"));
    }
}