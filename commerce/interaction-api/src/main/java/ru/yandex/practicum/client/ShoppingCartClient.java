package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    CartResponseDto getCart(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    CartResponseDto addProduct(@RequestParam String username,
                               @RequestBody Map<UUID, Long> products);

    @DeleteMapping("/api/v1/shopping-cart")
    void deactivateCart(@RequestParam String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    CartResponseDto deleteProduct(@RequestParam String username,
                                  @RequestBody Set<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    CartResponseDto updateProductQuantity(@RequestParam String username,
                                          @RequestBody CartItemRequestDto requestDto);
}
