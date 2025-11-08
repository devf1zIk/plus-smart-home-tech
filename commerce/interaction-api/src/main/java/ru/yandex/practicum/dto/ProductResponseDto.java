package ru.yandex.practicum.dto;

import lombok.Data;
import ru.yandex.practicum.enums.AvailabilityStatus;
import ru.yandex.practicum.enums.ProductStatus;
import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private AvailabilityStatus availability;
    private ProductStatus status;
    private Double price;
}
