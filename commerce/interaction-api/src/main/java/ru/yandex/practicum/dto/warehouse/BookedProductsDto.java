package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedProductsDto {

    @Positive
    BigDecimal deliveryWeight;
    @Positive
    BigDecimal deliveryVolume;
    boolean fragile;
}
