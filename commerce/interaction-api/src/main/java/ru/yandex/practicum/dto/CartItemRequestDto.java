package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CartItemRequestDto {

    @NotNull
    private UUID productId;

    @NotNull(message = "Необходимо указать количество")
    @Min(value = 1, message = "Минимальное количество равно 1")
    private Long quantity;
}