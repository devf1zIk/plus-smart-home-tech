package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProducts(String username, Map<UUID, Long> products);

    ShoppingCartDto removeProducts(String username, List<UUID> productIds);

    ShoppingCartDto deactivate(String username);

    ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest requestDto);
}