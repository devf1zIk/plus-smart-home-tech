package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.CartState;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {

    @NotNull
    UUID cartId;

    @NotNull
    String username;

    @NotNull
    CartState status;

    @NotNull(message = "Необходимо указать товары и их количество")
    Map<UUID, Long> products;
}
