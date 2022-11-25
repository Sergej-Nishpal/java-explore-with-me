package ru.practicum.ewmmain.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(long id) {
        super(String.format("Событие с id = %d не найдено!", id));
    }
}