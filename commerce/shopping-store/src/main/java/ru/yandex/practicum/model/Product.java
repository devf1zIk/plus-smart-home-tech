package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.enums.ProductState;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private QuantityState availability;

    @Enumerated(EnumType.STRING)
    private ProductState status;

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal price;
}
