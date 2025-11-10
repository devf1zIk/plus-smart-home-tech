package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.WarehouseItem;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<WarehouseItem, UUID> {
}
