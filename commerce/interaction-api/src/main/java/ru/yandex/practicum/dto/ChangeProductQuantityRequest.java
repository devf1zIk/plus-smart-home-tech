package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @Min(1)
    @Min(value = 1, message = "Минимальное количество товара — 1")
    private Long quantity;
}
