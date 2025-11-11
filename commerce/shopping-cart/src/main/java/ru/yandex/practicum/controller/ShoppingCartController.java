package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.service.CartService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final CartService cartService;

    @Override
    @GetMapping
    public CartResponseDto getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @Override
    @PutMapping
    public CartResponseDto addProduct(@RequestParam String username,
                                      @RequestBody @NotNull Map<UUID, Long> products) {
        return cartService.addProduct(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @Override
    @PostMapping("/remove")
    public CartResponseDto deleteProduct(@RequestParam String username,
                                         @RequestBody Set<UUID> productIds) {
        return cartService.deleteProduct(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public CartResponseDto updateProductQuantity(@RequestParam String username,
                                                 @RequestBody @Valid CartItemRequestDto requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }
}
