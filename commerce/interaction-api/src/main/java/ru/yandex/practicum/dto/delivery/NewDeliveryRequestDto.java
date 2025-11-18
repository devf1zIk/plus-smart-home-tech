package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDeliveryRequestDto {

    @NotNull
    UUID orderId;

    @NotNull
    AddressDto toAddress;
    AddressDto fromAddress;

    Double totalWeight;
    Double totalVolume;
    Boolean fragile;
}
