package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProducts(@RequestParam String username,
                                @RequestBody Map<UUID, Long> products);

    @DeleteMapping("/api/v1/shopping-cart")
    ShoppingCartDto deactivate(@RequestParam("username") String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeProducts(@RequestParam("username") String username,
                                   @RequestBody List<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam("username") String username,
                                   @RequestBody ChangeProductQuantityRequest requestDto);
}
