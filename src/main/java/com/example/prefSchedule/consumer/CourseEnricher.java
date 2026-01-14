package com.example.prefSchedule.consumer;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.event.EnrichedGradeEvent;
import com.example.prefSchedule.event.FinalGradeEvent;
import com.example.prefSchedule.event.GradeEvent;
import com.example.prefSchedule.entity.Pack;
import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.service.CourseService;
import com.example.prefSchedule.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CourseEnricher {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CourseService service;

    @Autowired
    private StudentService studentService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional // pt a putea aduce pack - el este incarcat lazy
    @KafkaListener(topics = "enriched-events", groupId = "group-course-enricher")
    public void enrichWithCourseData(ConsumerRecord<String, String> record) {
        try {
            EnrichedGradeEvent event = mapper.readValue(record.value(), EnrichedGradeEvent.class);

            Optional<Student> student = studentService.findByCode(event.getStudentCode());
            if(student.isEmpty()) {
                throw new IllegalArgumentException("Student not found: " + event.getStudentCode());
            }

            Optional<Course> course = service.findByCode(event.getCourseCode());
            if(course.isEmpty()) {
                throw new IllegalArgumentException("Course not found: " + event.getCourseCode());
            }

            Pack pack = course.get().getPack();
            String packName = (pack != null) ? pack.getName() : "No Pack Assigned";

            System.out.println(packName);

            FinalGradeEvent finalEvent = new FinalGradeEvent(
                    event.getStudentCode(),
                    event.getCourseCode(),
                    student.get().getName(),
                    student.get().getYear(),
                    course.get().getName(),
                    pack.getSemester(),
                    event.getGrade()
            );

            kafkaTemplate.send("final-events",
                    event.getStudentCode(),
                    mapper.writeValueAsString(finalEvent));

            System.out.println("Final event: " + finalEvent);
        } catch (Exception e) {
            System.err.println("Failed to enrich final event: " + record.value());

            String dlqMessage = String.format(
                    "{\"original\": %s, \"error\": \"%s\", \"timestamp\": \"%s\"}",
                    record.value(), e.getMessage(), System.currentTimeMillis()
            );

            kafkaTemplate.send("grade-events.DLQ", record.key(), dlqMessage);
        }
    }
}
