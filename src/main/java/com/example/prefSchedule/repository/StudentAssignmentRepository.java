package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Long> {

    /**
     * Recuperează toate asignările finale pentru un anumit student.
     */
    List<StudentAssignment> findByStudentCode(String studentCode);

    /**
     * Recuperează asignările pentru un pachet specific (util pentru rapoarte de pachet).
     */
    List<StudentAssignment> findByPackId(Long packId);

    List<StudentAssignment> findByCourseCode(String courseCode);

    /**
     * Șterge asignările vechi pentru un pachet înainte de a salva o nouă rulare a algoritmului.
     * Transactional este necesar pentru operațiunile de ștergere personalizate.
     */
    @Transactional
    void deleteByPackId(Long packId);
}