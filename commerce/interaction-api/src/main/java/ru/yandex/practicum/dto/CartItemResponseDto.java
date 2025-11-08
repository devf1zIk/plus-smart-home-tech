package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItemResponseDto {

    private UUID id;
    private UUID productId;
    private String productName;
    private int quantity;
}
