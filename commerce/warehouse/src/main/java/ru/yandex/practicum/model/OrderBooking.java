package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderBooking {

    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    private UUID bookingId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(name = "order_booking_products",
            joinColumns = @JoinColumn(name = "order_booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    @Column(name = "state")
    private String state;

    @Column(name = "total_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeight;

    @Column(name = "total_volume", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVolume;

    @Column(name = "fragile", nullable = false)
    private Boolean fragile;
}