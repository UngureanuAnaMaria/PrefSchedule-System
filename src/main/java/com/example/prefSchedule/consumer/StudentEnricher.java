package com.example.prefSchedule.consumer;

import com.example.prefSchedule.event.EnrichedGradeEvent;
import com.example.prefSchedule.event.GradeEvent;
import com.example.prefSchedule.entity.Student;
import com.example.prefSchedule.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentEnricher {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private StudentService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "raw-grade-events", groupId = "group-student-enricher")
    public void enrichWithStudentData(ConsumerRecord<String, String> record) {
        try {
            GradeEvent event = mapper.readValue(record.value(), GradeEvent.class);

            Optional<Student> student = service.findByCode(event.getStudentCode());
            if(student.isEmpty()) {
                throw new IllegalArgumentException("Student not found: " + event.getStudentCode());
            }

            EnrichedGradeEvent enriched = new EnrichedGradeEvent(
                    event.getStudentCode(),
                    event.getCourseCode(),
                    student.get().getName(),
                    student.get().getYear(),
                    event.getGrade()
            );

            kafkaTemplate.send("enriched-events",
                    event.getStudentCode(),
                    mapper.writeValueAsString(enriched));

            System.out.println("Enriched event: " + enriched);
        } catch (Exception e) {
            System.err.println("Failed to enrich event: " + record.value());

            String dlqMessage = String.format(
                    "{\"original\": %s, \"error\": \"%s\", \"timestamp\": \"%s\"}",
                    record.value(), e.getMessage(), System.currentTimeMillis()
            );

            kafkaTemplate.send("grade-events.DLQ", record.key(), dlqMessage);
        }
    }
}
