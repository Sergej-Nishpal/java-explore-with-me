package ru.practicum.ewmmain.exception;

public class ParticipationRequestNotFoundException extends RuntimeException {
    public ParticipationRequestNotFoundException(long id) {
        super(String.format("Запрос с id = %d не найдена!", id));
    }
}