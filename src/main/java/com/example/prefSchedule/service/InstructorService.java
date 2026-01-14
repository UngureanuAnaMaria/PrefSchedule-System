package com.example.prefSchedule.service;

import com.example.prefSchedule.entity.Instructor;
import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {
    @Autowired
    private InstructorRepository repo;

    public Instructor save(Instructor instructor) {
        return repo.save(instructor);
    }

    public List<Instructor> findAll() {
        return repo.findAll();
    }

    public Optional<Instructor> findById(Long id) {
        return repo.findById(id);
    }

    public List<Instructor> findByName(String name) {
        return repo.findByName(name);
    }

    public Optional<Instructor> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Optional<Instructor> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void updateName(Long id, String newName) {
        int result = repo.updateName(id, newName);
        if (result == 0) {
            throw new RuntimeException("Instructor not found with id: " + id);
        }
    }

    public void updateEmail(Long id, String newEmail) {
        Optional<Instructor> instructor = repo.findByEmail(newEmail);
        if(instructor.isPresent() && !instructor.get().getId().equals(id)) {
            throw new RuntimeException("Instructor with email '" + newEmail + "' already exists");
        }

        int result = repo.updateEmail(id, newEmail);
        if (result == 0) {
            throw new RuntimeException("Instructor not found with id: " + id);
        }
    }

    public void deleteById(Long id) {
        Optional<Instructor> instructor = repo.findById(id);
        if (!instructor.isPresent()) {
            throw new RuntimeException("Instructor not found with id: " + id);
        }
        repo.deleteById(id);
    }

    public void deleteByName(String name) {
        List<Instructor> instructors = repo.findByName(name);
        if (instructors.isEmpty()) {
            throw new RuntimeException("Instructor not found with name: " + name);
        }
        repo.deleteByName(name);
    }

    public void deleteByEmail(String email) {
        Optional<Instructor> instructor = repo.findByEmail(email);
        if (!instructor.isPresent()) {
            throw new RuntimeException("Instructor not found with email: " + email);
        }
        repo.deleteByEmail(email);
    }
}