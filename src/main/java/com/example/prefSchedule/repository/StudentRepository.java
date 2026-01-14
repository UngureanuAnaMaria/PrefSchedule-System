package com.example.prefSchedule.repository;

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
public interface StudentRepository extends JpaRepository<Student, Long> {
    //derived -> sql automat
    Optional<Student> findByCode(String code);
    List<Student> findByName(String name);
    Optional<Student> findByUsername(String username);

    //JPQL -> Java Persistence Query Language
    @Query("SELECT s FROM Student s WHERE s.email LIKE %:email% ")
    Optional<Student> findByEmail(@Param("email") String email);

    @Query("SELECT s FROM Student s WHERE s.year = :year ")
    List<Student> findByYear(@Param("year") int year);

    // modifying query
    //int -> repr numarul de randuri afectate
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.code = :newCode WHERE s.id = :id")
    int updateCode(@Param("id") Long id, @Param("newCode") String newCode);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.email = :newEmail WHERE s.id = :id")
    int updateEmail(@Param("id") Long id, @Param("newEmail") String newEmail);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.name = :newName WHERE s.id = :id")
    int updateName(@Param("id") Long id, @Param("newName") String newName);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.year = :newYear WHERE s.id = :id")
    int updateYear(@Param("id") Long id, @Param("newYear") int newYear);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.code = :code")
    int deleteByCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.name = :name")
    int deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.email = :email")
    int deleteByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.year = :year")
    int deleteByYear(@Param("year") Integer year);
}
