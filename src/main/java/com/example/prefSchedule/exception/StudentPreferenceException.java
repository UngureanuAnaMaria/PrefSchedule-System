package com.example.prefSchedule.exception;

public class StudentPreferenceException extends RuntimeException{
    public StudentPreferenceException(String message) {
        super(message);
    }

    public StudentPreferenceException(String message, Throwable cause) {
        super(message,cause);
    }
}
