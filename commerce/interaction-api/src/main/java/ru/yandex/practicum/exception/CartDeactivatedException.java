package ru.yandex.practicum.exception;

public class CartDeactivatedException extends RuntimeException {
    public CartDeactivatedException() {
        super("Cart is deactivated and cannot be modified");
    }
}
