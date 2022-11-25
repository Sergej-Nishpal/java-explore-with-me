package ru.practicum.ewmmain.exception;

public class NotPendingStateException extends RuntimeException {
    public NotPendingStateException(String eventState) {
        super(String.format("Событие должно находиться в состоянии \"PENDING\", а не %s!", eventState));
    }
}
