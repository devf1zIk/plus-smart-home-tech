package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.AvailabilityStatus;
import ru.yandex.practicum.enums.ProductStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availability;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(nullable = false)
    private Double price;
}
