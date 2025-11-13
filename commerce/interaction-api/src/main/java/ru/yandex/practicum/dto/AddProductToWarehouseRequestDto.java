package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductToWarehouseRequestDto {

    @NotNull(message = "ID продукта обязателен")
    UUID productId;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Минимальное количество равно 1")
    Long quantity;
}
