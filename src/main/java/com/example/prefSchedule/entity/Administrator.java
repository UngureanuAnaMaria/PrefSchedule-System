package com.example.prefSchedule.entity;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "administrators")
@PrimaryKeyJoinColumn(name = "user_id")
public class Administrator extends User {
    public Administrator() {}

    public Administrator(String username, String name, String email, String password) {
        super(username, name, email, password);
        this.setRoles(List.of(Role.ADMIN));
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}