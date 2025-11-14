package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;
    @NotBlank(message = "Название продукта не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String productName;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @Size(max = 500, message = "URL изображения слишком длинный")
    private String imageSrc;

    @NotNull(message = "Состояние количества обязательно")
    private QuantityState quantityState;

    @NotNull(message = "Необходимо указать статус товара")
    private ProductState productState;

    @NotNull(message = "Категория продукта обязательна")
    private ProductCategory productCategory;

    @NotNull(message = "Необходимо указать цену товара")
    @DecimalMin(value = "1.00", message = "Минимальная стоимость товара 1 руб 00 коп")
    private BigDecimal price;
}
