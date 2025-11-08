package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import java.util.UUID;

public interface CartService {

    CartResponseDto getCart(String username);

    CartResponseDto addItem(String username, CartItemRequestDto dto);

    CartResponseDto removeItem(String username, UUID itemId);

    CartResponseDto deactivateCart(String username);

    void clearCart(String username);
}
