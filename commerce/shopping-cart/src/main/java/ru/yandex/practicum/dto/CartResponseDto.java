package ru.yandex.practicum.dto;

import lombok.Data;
import ru.yandex.practicum.enums.CartStatus;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponseDto {

    private UUID id;
    private String username;
    private CartStatus status;
    private List<CartItemResponseDto> items;
}
