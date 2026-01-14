package com.example.prefSchedule.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@MappedSuperclass
public class Base {
    //@GeneratedValue(strategy = GenerationType.IDENTITY) -> id generat de DB, cand fac persist/insert nu este inclus
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Name must not be empty")
    @Size(max = 100)
    @Column(name="name", nullable = false)
    private String name;

    @NotBlank(message = "Email must not be empty")
    @Size(max = 30)
    @Email(message = "Format name@domain.com.")
    @Column(name="email", nullable = false, unique = true)
    private String email;

    public Base() {}

    public Base(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Base{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
