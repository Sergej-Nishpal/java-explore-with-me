package ru.practicum.ewmmain.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(long id) {
        super(String.format("Категория с id = %d не найдена!", id));
    }
}