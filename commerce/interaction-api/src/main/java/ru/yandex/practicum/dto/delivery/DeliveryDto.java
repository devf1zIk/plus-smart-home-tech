package ru.yandex.practicum.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.enums.DeliveryState;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {

    private UUID deliveryId;
    private UUID orderId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private DeliveryState deliveryState;
}
