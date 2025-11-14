package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewProductWarehouseRequestDto {

    @NotNull(message = "ID продукта обязателен")
    UUID productId;

    @NotNull(message = "Вес обязателен")
    @Positive(message = "Вес должен быть положительным")
    Double weight;

    @NotNull(message = "Размеры товара обязательны")
    DimensionDto dimension;

    @PositiveOrZero
    private Long quantity;

    @NotNull(message = "Необходимо указать хрупкость товара")
    Boolean fragile;
}
