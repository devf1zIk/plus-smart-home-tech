package ru.yandex.practicum.dto;

import lombok.Data;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.enums.ProductState;
import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private QuantityState availability;
    private ProductState status;
    private Double price;
}
