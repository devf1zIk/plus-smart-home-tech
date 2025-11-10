package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.enums.CartState;
import java.util.Map;
import java.util.UUID;

@Data
public class CartResponseDto {

    @NotNull
    UUID productId;

    @NotNull
    String username;

    @NotNull
    CartState status;

    @NotNull(message = "Необходимо указать товары и их количество")
    Map<UUID, Long> products;
}