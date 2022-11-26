package ru.practicum.ewmmain.exception;

public class IncorrectEventDateException extends RuntimeException {
    public IncorrectEventDateException(int hours) {
        super(String.format("Действие возможно не позднее, чем за %d час. до даты события!", hours));
    }
}
