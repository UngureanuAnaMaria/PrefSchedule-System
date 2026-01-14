package com.example.prefSchedule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name="users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "email"})})
@Inheritance(strategy = InheritanceType.JOINED) // tabela pt parinte, tabela pt copil legate prin join uri
//users (tabela parinte)
//├── id, name, email, username, password
//
//students (tabela copil)
//├── user_id (FK către users), code, year
//
//instructors (tabela copil)
//├── user_id (FK către users)
//
//administrators (tabela copil)
//├── user_id (FK către users)
public abstract class User extends Base{
    @NotBlank(message = "Username must not be empty")
    @Size(max = 20)
    @Column(name="username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Size(max = 150)
    @Column(name="password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) // JPA va crea o tabela secundara, EAGER -> incarcare imediata
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id")) // def tabela
    @Column(name="role")
    @Enumerated(EnumType.STRING)
    @NotEmpty(message = "Roles must not be empty")
    private List<Role> roles = new ArrayList<>();

    public User() {}

    public User(String username, String name, String email, String password) {
        super(name, email);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", name='" + getName() +'\'' +
                ", email='" + getEmail() + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
