package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/students")
@Deprecated
public class StudentControllerV1 {
    @Autowired
    private StudentService service;

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        try {
            Student savedStudent = service.save(student);
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        List<Student> students = service.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        Optional<Student> student = service.findById(id);
        //map apartine lui Optional
        //s->ResponseEntity.ok(s) <-> ResponseEntity::ok
        return student.map(s -> ResponseEntity.ok(s))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Student> getByCode(@PathVariable String code) {
        Optional<Student> student = service.findByCode(code);
        return student.map(s -> ResponseEntity.ok(s))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getByEmail(@PathVariable String email) {
        Optional<Student> student = service.findByEmail(email);
        return student.map(s -> ResponseEntity.ok(s))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Student>> getByName(@PathVariable String name) {
        List<Student> students = service.findByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Student>> getByYear(@PathVariable int year) {
        List<Student> students = service.findByYear(year);
        return ResponseEntity.ok(students);
    }

    //endpoint pt update cu @RequestBody Student studentPartial!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @PutMapping("/{id}/name/{name}")
    public ResponseEntity<Void> updateName(@PathVariable Long id, @RequestBody String name) {
        try {
            service.updateName(id, name);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/code/{code}")
    public ResponseEntity<Void> updateCode(@PathVariable Long id, @RequestBody String code) {
        try {
            service.updateCode(id, code);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/email/{email}")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id, @RequestBody String email) {
        try {
            service.updateEmail(id, email);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/year/{year}")
    public ResponseEntity<Void> updateYear(@PathVariable Long id, @RequestBody int year) {
        try {
            service.updateYear(id, year);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    //DE CE NU SIMPLU   @DeleteMapping("/{name}") ???????? -> pt ca SPRING nu poate sa distinga
    //intre DeleteMapping("/{name}")  si   @DeleteMapping("/{id}")
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        try {
            service.deleteByName(name);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteByEmail(@PathVariable String email) {
        try {
            service.deleteByEmail(email);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/code/{code}")
    public ResponseEntity<Void> deleteByCode(@PathVariable String code) {
        try {
            service.deleteByCode(code);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/year/{year}")
    public ResponseEntity<Void> deleteByYear(@PathVariable int year) {
        try {
            service.deleteByYear(year);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if(e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
