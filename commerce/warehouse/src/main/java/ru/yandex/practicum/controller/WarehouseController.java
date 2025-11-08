package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.WarehouseService;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @PostMapping("/add")
    public WarehouseItemResponseDto addProduct(@Valid @RequestBody WarehouseItemRequestDto dto) {
        return warehouseService.addProduct(dto);
    }

    @PatchMapping("/{productId}/quantity")
    public WarehouseItemResponseDto updateQuantity(
            @PathVariable UUID productId,
            @RequestParam @Min(value = 1, message = "Количество должно быть положительным") Long quantity) {
        return warehouseService.updateQuantity(productId, quantity);
    }

    @GetMapping("/{productId}/check")
    public WarehouseCheckResponseDto checkAvailability(
            @PathVariable UUID productId,
            @RequestParam @Min(value = 1, message = "Запрашиваемое количество должно быть положительным") Long requestedQuantity) {
        return warehouseService.checkAvailability(productId, requestedQuantity);
    }
}
