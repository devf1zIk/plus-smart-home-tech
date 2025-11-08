package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public Map<String, Object> handleProductAlreadyExists(ProductAlreadyExistsException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Product Already Exists",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public Map<String, Object> handleProductNotFound(ProductNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Product Not Found",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    public Map<String, Object> handleInsufficientStock(InsufficientStockException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Insufficient Stock",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(CartDeactivatedException.class)
    public Map<String, Object> handleCartDeactivated(CartDeactivatedException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Cart Deactivated",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public Map<String, Object> handleCartItemNotFound(CartItemNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Cart Item Not Found",
                "message", ex.getMessage()
        );
    }
}
