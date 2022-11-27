package ru.practicum.ewmmain.exception;

public class IncorrectEventStateException extends RuntimeException {
    public IncorrectEventStateException(String message) {
        super(message);
    }
}