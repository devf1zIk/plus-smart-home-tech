package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final CartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> products) {
        return cartService.addProducts(username, products);
    }

    @Override
    @DeleteMapping
    public ShoppingCartDto deactivate(@RequestParam String username) {
        return cartService.deactivate(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username,
                                         @RequestBody List<UUID> productIds) {
        return cartService.removeProducts(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody @Valid ChangeProductQuantityRequest requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }
}
