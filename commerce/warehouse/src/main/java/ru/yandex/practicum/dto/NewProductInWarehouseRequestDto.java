package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewProductInWarehouseRequestDto {

    @NotBlank(message = "Название продукта обязательно")
    String name;

    @NotNull(message = "Ширина обязательна")
    @Positive(message = "Ширина должна быть положительной")
    Double width;

    @NotNull(message = "Высота обязательна")
    @Positive(message = "Высота должна быть положительной")
    Double height;

    @NotNull(message = "Длина обязательна")
    @Positive(message = "Длина должна быть положительной")
    Double length;

    @NotNull(message = "Вес обязателен")
    @Positive(message = "Вес должен быть положительным")
    Double weight;

    @NotNull(message = "Признак хрупкости обязателен")
    Boolean fragile;
}
