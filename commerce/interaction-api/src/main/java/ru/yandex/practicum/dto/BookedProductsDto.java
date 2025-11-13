package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedProductsDto {

    @Positive
    double deliveryWeight;
    @Positive
    double deliveryVolume;
    boolean fragile;
}
