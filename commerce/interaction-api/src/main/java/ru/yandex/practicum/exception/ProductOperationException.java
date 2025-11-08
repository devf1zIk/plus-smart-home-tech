package ru.yandex.practicum.exception;

public class ProductOperationException extends RuntimeException {
    public ProductOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}