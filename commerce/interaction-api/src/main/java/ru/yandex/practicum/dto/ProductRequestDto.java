package ru.yandex.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.enums.QuantityState;

@Data
public class ProductRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private QuantityState availability;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double price;
}
