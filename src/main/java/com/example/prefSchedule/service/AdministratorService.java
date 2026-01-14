package com.example.prefSchedule.service;

import com.example.prefSchedule.entity.Administrator;
import com.example.prefSchedule.repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdministratorService {
    @Autowired
    private AdministratorRepository repo;

    public Optional<Administrator> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}