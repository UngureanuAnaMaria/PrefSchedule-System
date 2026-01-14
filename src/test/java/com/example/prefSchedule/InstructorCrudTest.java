package com.example.prefSchedule;

import com.example.prefSchedule.entity.Instructor;
import com.example.prefSchedule.service.InstructorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class InstructorCrudTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private InstructorService instructorService;

    @Test
    void testDatabaseSchemaAndCRUD() {
        Instructor instructor = new Instructor("prof_ci_cd", "CI/CD Test", "test@univ.ro", "pass");
        Instructor saved = instructorService.save(instructor);

        assertThat(saved.getId()).isNotNull();
        assertThat(instructorService.findById(saved.getId())).isPresent();
    }
}