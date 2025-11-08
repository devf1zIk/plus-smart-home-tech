package ru.yandex.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class CartItemRequestDto {

    @NotNull(message = "ID товара не может быть пустым")
    private UUID productId;

    @NotBlank(message = "Название товара не может быть пустым")
    private String productName;

    @Min(value = 1, message = "Количество должно быть не меньше 1")
    private int quantity;
}
