package ru.practicum.ewmmain.exception;

public class ParticipationRequestNotFoundException extends RuntimeException {
    public ParticipationRequestNotFoundException(long id) {
        super(String.format("Заявка с id = %d не найдена!", id));
    }
}