package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(
                "BAD_REQUEST",
                "Переданы некорректные данные.",
                message,
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return new ErrorResponse(
                "BAD_REQUEST",
                "Нарушение ограничений валидации.",
                message,
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Внутренняя ошибка сервера.",
                "Произошла неожиданная ошибка при обработке запроса",
                System.currentTimeMillis()
        );
    }
}
