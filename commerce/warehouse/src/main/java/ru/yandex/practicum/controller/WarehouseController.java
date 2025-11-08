package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WareHouseClient;
import ru.yandex.practicum.dto.WarehouseCheckResponseDto;
import ru.yandex.practicum.dto.WarehouseItemRequestDto;
import ru.yandex.practicum.dto.WarehouseItemResponseDto;
import ru.yandex.practicum.service.WarehouseService;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WareHouseClient {

    private final WarehouseService warehouseService;

    @PostMapping("/add")
    public WarehouseItemResponseDto addProduct(@Valid @RequestBody WarehouseItemRequestDto dto) {
        return warehouseService.addProduct(dto);
    }

    @PatchMapping("/{productId}/quantity")
    public WarehouseItemResponseDto updateQuantity(
            @PathVariable UUID productId,
            @RequestParam Long quantity) {
        return warehouseService.updateQuantity(productId, quantity);
    }

    @GetMapping("/{productId}/check")
    public WarehouseCheckResponseDto checkAvailability(
            @PathVariable UUID productId,
            @RequestParam Long requestedQuantity) {
        return warehouseService.checkAvailability(productId, requestedQuantity);
    }
}
