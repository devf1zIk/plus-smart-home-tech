package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartService {

    CartResponseDto getCart(String username);

    CartResponseDto addProduct(String username, Map<UUID, Long> products);

    CartResponseDto deleteProduct(String username, Set<UUID> productIds);

    CartResponseDto deactivateCart(String username);

    CartResponseDto updateProductQuantity(String username, CartItemRequestDto requestDto);
}