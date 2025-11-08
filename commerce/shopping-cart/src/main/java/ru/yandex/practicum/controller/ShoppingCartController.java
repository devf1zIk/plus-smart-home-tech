package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.service.CartService;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final CartService cartService;

    @GetMapping("/{username}")
    public CartResponseDto getCart(@PathVariable String username) {
        return cartService.getCart(username);
    }

    @PostMapping("/{username}/items")
    public CartResponseDto addItem(@PathVariable String username,
                                   @Valid @RequestBody CartItemRequestDto dto) {
        return cartService.addItem(username, dto);
    }

    @DeleteMapping("/{username}/items/{itemId}")
    public CartResponseDto removeItem(@PathVariable String username, @PathVariable UUID itemId) {
        return cartService.removeItem(username, itemId);
    }

    @PostMapping("/{username}/deactivate")
    public CartResponseDto deactivateCart(@PathVariable String username) {
        return cartService.deactivateCart(username);
    }

    @DeleteMapping("/{username}/clear")
    public void clearCart(@PathVariable String username) {
        cartService.clearCart(username);
    }
}
