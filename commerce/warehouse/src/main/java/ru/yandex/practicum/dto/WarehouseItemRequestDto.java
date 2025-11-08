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
    @NotNull
    private Double weight;

    @Positive
    @NotNull
    private Double width;

    @Positive
    @NotNull
    private Double height;

    @Positive
    @NotNull
    private Double depth;

    @PositiveOrZero
    private Long quantity;
}
