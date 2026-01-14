package com.example.prefSchedule.service;

import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository repo;

    public Student save(Student student) {
        return repo.save(student);
    }

    public List<Student> findAll() {
        return repo.findAll();
    }

    public Optional<Student> findById(Long id) {
        return repo.findById(id);
    }

    public Optional<Student> findByCode(String code) {
        return repo.findByCode(code);
    }

    public List<Student> findByName(String name) {
        return repo.findByName(name);
    }

    public Optional<Student> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public List<Student> findByYear(int year) {
        return repo.findByYear(year);
    }

    public Optional<Student> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void updateCode(Long id, String newCode) {
        Optional<Student> student = repo.findByCode(newCode);
        if(student.isPresent() && !student.get().getId().equals(id)) {
            throw new RuntimeException("Student with code '" + newCode + "' already exists");
        }

        int result = repo.updateCode(id, newCode);
        if (result == 0) {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }

    public void updateName(Long id, String newName) {
        int result = repo.updateName(id, newName);
        if (result == 0) {
            throw new RuntimeException("Students not found with id: " + id);
        }
    }

    public void updateEmail(Long id, String newEmail) {
        Optional<Student> student = repo.findByEmail(newEmail);
        if(student.isPresent() && !student.get().getId().equals(id)) {
            throw new RuntimeException("Student with email '" + newEmail + "' already exists");
        }

        int result = repo.updateEmail(id, newEmail);
        if (result == 0) {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }

    public void updateYear(Long id, int newYear) {
        int result = repo.updateYear(id, newYear);
        if (result == 0) {
            throw new RuntimeException("Students not found with id: " + id);
        }
    }

    public void deleteById(Long id) {
        Optional<Student> student = repo.findById(id);
        if (!student.isPresent()) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        repo.deleteById(id);
    }

    public void deleteByCode(String code) {
        Optional<Student> student = repo.findByCode(code);
        if (!student.isPresent()) {
            throw new RuntimeException("Student not found with code: " +code);
        }
        repo.deleteByCode(code);
    }

    public void deleteByEmail(String email) {
        Optional<Student> student = repo.findByEmail(email);
        if (!student.isPresent()) {
            throw new RuntimeException("Student not found with email: " + email);
        }
        repo.deleteByEmail(email);
    }

    public void deleteByName(String name) {
        List<Student> students = repo.findByName(name);
        if (students.isEmpty()) {
            throw new RuntimeException("Students not found with name: " + name);
        }
        repo.deleteByName(name);
    }

    public void deleteByYear(int year) {
        List<Student> students = repo.findByYear(year);
        if (students.isEmpty()) {
            throw new RuntimeException("Students not found with year: " + year);
        }
        repo.deleteByYear(year);
    }
}
