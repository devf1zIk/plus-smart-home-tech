package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "warehouse_items")
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(nullable = false)
    private Boolean fragile;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double depth;
}
