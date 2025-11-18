package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "deliveries")
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    private UUID deliveryId;

    @Column(nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryState deliveryState;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "from_address_id")
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "to_address_id")
    private Address toAddress;

    @Column(nullable = false)
    private BigDecimal totalWeight;

    @Column(nullable = false)
    private BigDecimal totalVolume;

    @Column(nullable = false)
    private Boolean fragile;
}
