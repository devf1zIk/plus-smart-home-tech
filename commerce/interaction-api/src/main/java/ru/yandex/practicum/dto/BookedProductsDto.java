package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedProductsDto {

    @NotNull(message = "Общий вес доставки обязателен")
    Double deliveryWeight;

    @NotNull(message = "Общий объём доставки обязателен")
    Double deliveryVolume;

    @NotNull(message = "Наличие хрупких вещей в доставке обязательно к указанию")
    Boolean fragile;
}
