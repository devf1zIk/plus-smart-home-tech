package ru.yandex.practicum.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String itemId) {
        super("Cart item not found: " + itemId);
    }
}
