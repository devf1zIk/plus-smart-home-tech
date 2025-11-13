package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(HttpStatus status, String error, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", error,
                "message", message
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public Map<String, Object> handleProductAlreadyExists(ProductAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Product Already Exists", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public Map<String, Object> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Product Not Found", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public Map<String, Object> handleInsufficientStock(InsufficientStockException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Insufficient Stock", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CartDeactivatedException.class)
    public Map<String, Object> handleCartDeactivated(CartDeactivatedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Cart Deactivated", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CartNotFoundException.class)
    public Map<String, Object> handleCartNotFound(CartNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Cart Not Found", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ProductOperationException.class)
    public Map<String, Object> handleProductOperationException(ProductOperationException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Product Operation Error", ex.getMessage());
    }
}
