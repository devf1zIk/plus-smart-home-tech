package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.PaymentState;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    private UUID paymentId;
    private UUID orderId;
    private Double productTotal;
    private Double deliveryTotal;
    private Double feeTotal;
    private Double TotalPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentState state;

}
