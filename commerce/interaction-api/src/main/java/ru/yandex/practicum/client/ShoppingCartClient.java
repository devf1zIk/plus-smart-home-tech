package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart/{username}")
    CartResponseDto getCart(@PathVariable String username);

    @PostMapping("/api/v1/shopping-cart/{username}/items")
    CartResponseDto addItem(@PathVariable String username, @RequestBody CartItemRequestDto dto);

    @DeleteMapping("/api/v1/shopping-cart/{username}/items/{itemId}")
    CartResponseDto removeItem(@PathVariable String username, @PathVariable UUID itemId);

    @PostMapping("/api/v1/shopping-cart/{username}/deactivate")
    CartResponseDto deactivateCart(@PathVariable String username);

    @DeleteMapping("/api/v1/shopping-cart/{username}/clear")
    void clearCart(@PathVariable String username);
}
