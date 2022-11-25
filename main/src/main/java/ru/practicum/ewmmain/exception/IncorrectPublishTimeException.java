package ru.practicum.ewmmain.exception;

public class IncorrectPublishTimeException extends RuntimeException {
    public IncorrectPublishTimeException(int hours) {
        super(String.format("Публикация возможна не позднее, чем за %d час. до даты события!", hours));
    }
}
