package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.WarehouseCheckResponseDto;
import ru.yandex.practicum.dto.WarehouseItemRequestDto;
import ru.yandex.practicum.dto.WarehouseItemResponseDto;
import java.util.UUID;

public interface WarehouseService {

    WarehouseItemResponseDto addProduct(WarehouseItemRequestDto dto);

    WarehouseItemResponseDto updateQuantity(UUID productId, Long addQuantity);

    WarehouseCheckResponseDto checkAvailability(UUID productId, Long requestedQuantity);

    WarehouseItemResponseDto updateProduct(UUID productId, WarehouseItemRequestDto dto);
}
