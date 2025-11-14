package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartDto {

    @NotNull(message = "Необходимо указать идентификатор корзины")
    UUID shoppingCartId;

    @NotNull(message = "Необходимо указать товары и количество")
    Map<UUID, Long> products;
}
