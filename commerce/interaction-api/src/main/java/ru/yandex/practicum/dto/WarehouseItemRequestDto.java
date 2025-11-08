package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseItemRequestDto {

    @NotNull
    private UUID productId;

    @NotNull
    private Boolean fragile;

    @Positive
    private Double weight;

    @Positive
    private Double width;

    @Positive
    private Double height;

    @Positive
    private Double depth;

    @PositiveOrZero
    private Long quantity;
}
