package ru.yandex.practicum.dto.payment;

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
public class PaymentDto {

    @NotNull
    private UUID paymentId;

    Double totalPayment;
    Double deliveryTotal;
    Double feeTotal;
}
