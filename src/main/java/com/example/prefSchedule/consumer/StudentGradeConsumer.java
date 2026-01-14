package com.example.prefSchedule.consumer;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.Course.CourseType;
import com.example.prefSchedule.event.FinalGradeEvent;
import com.example.prefSchedule.event.GradeEvent;
import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.entity.StudentGrade;
import com.example.prefSchedule.repository.StudentGradeRepository;
import com.example.prefSchedule.service.CourseService;
import com.example.prefSchedule.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentGradeConsumer {

    @Autowired
    private StudentGradeRepository repository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objMapper = new ObjectMapper();
    private static final int MAX_RETRY_ATTEMPTS = 3;

    // ack manual -> noi decidem cand mesajul este consumat!!!!!!!!!!!!!
    @KafkaListener(topics = "final-events", groupId = "group-prefchedule")
    public void consumeGradeEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            FinalGradeEvent event = objMapper.readValue(record.value(), FinalGradeEvent.class);

            processWithRetry(event, record.key(), 0);
            ack.acknowledge(); // confirmam msj
            System.out.println("[CONSUMER] Successfully processed and acknowledged: " + event);
        } catch (Exception e) {
            System.err.println("[CONSUMER] Error processing after retries: " + record.value());
            sendToDlq(record.key(), record.value(), e.getMessage());
            ack.acknowledge(); // Confirmăm oricum pentru a nu bloca coada, mesajul fiind deja în DLQ
        }
    }

    // throws Exception -> tratez eroare la un nivel superior, in consumeGradeEvent
    private void processWithRetry(FinalGradeEvent event, String key, int attempt) throws Exception {
        try {
            Optional<Course> courseOpt = courseService.findByCode(event.getCourseCode());
            if (courseOpt.isEmpty()) {
                throw new IllegalArgumentException("Course not found: " + event.getCourseCode());
            }
            Course course = courseOpt.get();

            Optional<Student> student = studentService.findByCode(event.getStudentCode());
            if (student.isEmpty()) {
                throw new IllegalArgumentException("Student not found: " + event.getStudentCode());
            }

            if (course.getType() != CourseType.COMPULSORY) {
                throw new IllegalArgumentException("Course is not compulsory: " + event.getCourseCode());
            }

            // ensure indempotency
            Optional<StudentGrade> existingGrade = repository.findByStudentCodeAndCourseCode(
                    event.getStudentCode(),
                    event.getCourseCode()
            );

            if (existingGrade.isPresent()) {
                System.out.println("[CONSUMER] Grade already exists for student " + event.getStudentCode() +
                        " at course " + event.getCourseCode() + ". Skipping save.");
                return; // Ieșim cu succes, nu mai salvam nimic
            }

            StudentGrade grade = new StudentGrade();
            grade.setStudentCode(event.getStudentCode());
            grade.setCourseCode(event.getCourseCode());
            grade.setGrade(event.getGrade());

            repository.save(grade);
            System.out.println("[CONSUMER] Saved grade to DB: " + event + " (Attempt: " + (attempt + 1) + ")");

        } catch (IllegalArgumentException e) {
            // Erorile de logica (curs negasit, curs neobligatoriu) nu trebuie reincercate
            throw e;
        } catch (Exception e) {
            if (attempt < MAX_RETRY_ATTEMPTS) {
                System.out.println("Retry attempt: " + (attempt + 1) + " for: " + event);
                Thread.sleep(2000 * (attempt + 1));
                processWithRetry(event, key, attempt + 1);
            } else {
                throw new RuntimeException("[CONSUMER] Max retry attempts exceeded!", e);
            }
        }
    }

    private void sendToDlq(String key, String value, String errorMessage) {
        try {
            String dlqMessage = String.format(
                    "{\"original\": %s, \"error\": \"%s\", \"timestamp\": \"%s\"}",
                    value, errorMessage, System.currentTimeMillis()
            );

            kafkaTemplate.send("grade-events.DLQ", key, dlqMessage);
            System.out.println("[CONSUMER] Sent to DLQ: " + dlqMessage);
        } catch (Exception e) {
            System.err.println("[CONSUMER] Failed to send to DLQ: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "grade-events.DLQ", groupId = "group-handler-dql")
    public void processDlQMessages(ConsumerRecord<String, String> record) {
        System.out.println("[DLQ HANDLER] Processing failed message: " + record.value());
        notifyAdminAboutFailedMessage(record.value());
    }

    private void notifyAdminAboutFailedMessage(String message) {
        // aici imbunatit cu un email service
        System.out.println("[ADMIN NOTIFICATION] Failed message needs attention: " + message);
    }
}

