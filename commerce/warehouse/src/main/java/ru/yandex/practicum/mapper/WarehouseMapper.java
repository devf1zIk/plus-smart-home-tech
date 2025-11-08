package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.WarehouseItemRequestDto;
import ru.yandex.practicum.dto.WarehouseItemResponseDto;
import ru.yandex.practicum.model.WarehouseItem;

@Component
public class WarehouseMapper {

    public WarehouseItem toEntity(WarehouseItemRequestDto dto) {
        WarehouseItem item = new WarehouseItem();
        item.setProductId(dto.getProductId());
        item.setFragile(dto.getFragile());
        item.setWeight(dto.getWeight());
        item.setWidth(dto.getWidth());
        item.setHeight(dto.getHeight());
        item.setDepth(dto.getDepth());
        item.setQuantity(dto.getQuantity());
        return item;
    }

    public WarehouseItemResponseDto toDto(WarehouseItem entity) {
        WarehouseItemResponseDto dto = new WarehouseItemResponseDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setFragile(entity.getFragile());
        dto.setWeight(entity.getWeight());
        dto.setQuantity(entity.getQuantity());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setDepth(entity.getDepth());
        return dto;
    }
}
