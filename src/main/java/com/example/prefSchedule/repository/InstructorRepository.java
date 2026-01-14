package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.Instructor;
import com.example.prefSchedule.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    // Derived queries
    List<Instructor> findByName(String name);
    Optional<Instructor> findByUsername(String username);


    // JPQL queries
    @Query("SELECT i FROM Instructor i WHERE i.email LIKE %:email%")
    Optional<Instructor> findByEmail(@Param("email") String email);

    // Modifying queries
    @Modifying
    @Transactional
    @Query("UPDATE Instructor i SET i.name = :newName WHERE i.id = :id")
    int updateName(@Param("id") Long id, @Param("newName") String newName);

    @Modifying
    @Transactional
    @Query("UPDATE Instructor i SET i.email = :newEmail WHERE i.id = :id")
    int updateEmail(@Param("id") Long id, @Param("newEmail") String newEmail);

    @Modifying
    @Transactional
    @Query("DELETE FROM Instructor i WHERE i.name = :name")
    int deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Instructor i WHERE i.email = :email")
    int deleteByEmail(@Param("email") String email);
}