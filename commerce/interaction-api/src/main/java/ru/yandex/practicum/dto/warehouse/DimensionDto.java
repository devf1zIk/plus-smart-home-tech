package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DimensionDto {

    @NotNull(message = "Необходимо указать ширину")
    @Min(value = 1, message = "минимальное значение 1")
    BigDecimal width;

    @NotNull(message = "Необходимо указать высоту")
    @Min(value = 1, message = "минимальное значение 1")
    BigDecimal height;

    @NotNull(message = "Необходимо указать глубину")
    @Min(value = 1, message = "минимальное значение 1")
    BigDecimal depth;
}
