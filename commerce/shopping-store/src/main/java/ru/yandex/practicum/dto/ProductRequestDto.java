package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.enums.AvailabilityStatus;

@Data
public class ProductRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private AvailabilityStatus availability;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double price;
}
