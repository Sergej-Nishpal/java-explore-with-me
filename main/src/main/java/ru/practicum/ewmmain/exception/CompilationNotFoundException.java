package ru.practicum.ewmmain.exception;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(long id) {
        super(String.format("Подборка с id = %d не найдена!", id));
    }
}