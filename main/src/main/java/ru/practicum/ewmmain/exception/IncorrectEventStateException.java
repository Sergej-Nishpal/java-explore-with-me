package ru.practicum.ewmmain.exception;

public class IncorrectEventStateException extends RuntimeException {
    public IncorrectEventStateException(String state) {
        super(String.format("Действие невозможно, если текущий статус события: %s.", state));
    }
}
