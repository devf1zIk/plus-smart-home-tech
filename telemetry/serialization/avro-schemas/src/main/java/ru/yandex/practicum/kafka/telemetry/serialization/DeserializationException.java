package ru.yandex.practicum.kafka.telemetry.serialization;

public class DeserializationException extends RuntimeException {
    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
