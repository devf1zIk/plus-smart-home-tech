package ru.yandex.practicum.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseItemResponseDto {

    private UUID id;
    private UUID productId;
    private Boolean fragile;
    private Double weight;
    private Long quantity;
    private Double width;
    private Double height;
    private Double depth;
}
