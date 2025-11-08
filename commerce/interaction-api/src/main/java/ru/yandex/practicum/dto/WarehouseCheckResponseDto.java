package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseCheckResponseDto {

    private Double totalWeight;
    private Double totalVolume;
    private Boolean fragile;
}
