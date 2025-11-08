package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.WarehouseCheckResponseDto;
import ru.yandex.practicum.dto.WarehouseItemRequestDto;
import ru.yandex.practicum.dto.WarehouseItemResponseDto;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WareHouseClient {

    @PostMapping("/api/v1/warehouse/add")
    WarehouseItemResponseDto addProduct(@RequestBody WarehouseItemRequestDto dto);

    @PatchMapping("/api/v1/warehouse/{productId}/quantity")
    WarehouseItemResponseDto updateQuantity(@PathVariable UUID productId, @RequestParam Long quantity);

    @GetMapping("/api/v1/warehouse/{productId}/check")
    WarehouseCheckResponseDto checkAvailability(@PathVariable UUID productId, @RequestParam Long requestedQuantity);
}
