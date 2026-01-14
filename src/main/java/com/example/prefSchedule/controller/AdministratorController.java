package com.example.prefSchedule.controller;

import com.example.prefSchedule.entity.Administrator;
import com.example.prefSchedule.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    @Autowired
    private AdministratorService service;

    @GetMapping("/username/{username}")
    public ResponseEntity<Administrator> getByUsername(@PathVariable String username) {
        return service.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
