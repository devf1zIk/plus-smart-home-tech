package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.WarehouseCheckResponseDto;
import ru.yandex.practicum.dto.WarehouseItemRequestDto;
import ru.yandex.practicum.dto.WarehouseItemResponseDto;
import ru.yandex.practicum.exception.InsufficientStockException;
import ru.yandex.practicum.exception.InvalidQuantityException;
import ru.yandex.practicum.exception.ProductAlreadyExistsException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.repository.WarehouseRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseItemResponseDto addProduct(WarehouseItemRequestDto dto) {
        if (warehouseRepository.existsByProductId(dto.getProductId())) {
            throw new ProductAlreadyExistsException("Product already exists in warehouse");
        }

        WarehouseItem entity = warehouseMapper.toEntity(dto);
        entity.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0L);

        WarehouseItem saved = warehouseRepository.save(entity);
        return warehouseMapper.toDto(saved);
    }

    @Override
    public WarehouseItemResponseDto updateQuantity(UUID productId, Long addQuantity) {
        if (addQuantity == null) {
            throw new IllegalArgumentException("Quantity to add cannot be null");
        }
        WarehouseItem item = warehouseRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in warehouse"));

        long newQuantity = item.getQuantity() + addQuantity;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Resulting quantity cannot be negative");
        }
        item.setQuantity(newQuantity);
        WarehouseItem updated = warehouseRepository.save(item);

        return warehouseMapper.toDto(updated);
    }

    @Override
    public WarehouseCheckResponseDto checkAvailability(UUID productId, Long requestedQuantity) {
        if (requestedQuantity == null) {
            throw new InvalidQuantityException("Requested quantity cannot be null");
        }

        if (requestedQuantity <= 0) {
            throw new InvalidQuantityException("Requested quantity must be positive");
        }

        WarehouseItem item = warehouseRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in warehouse"));

        if (item.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock in warehouse");
        }

        double totalVolume = item.getWidth() * item.getHeight() * item.getDepth() * requestedQuantity;
        double totalWeight = item.getWeight() * requestedQuantity;

        return new WarehouseCheckResponseDto(totalWeight, totalVolume, item.getFragile());
    }

    @Override
    public WarehouseItemResponseDto updateProduct(UUID productId, WarehouseItemRequestDto dto) {
        WarehouseItem item = warehouseRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        item.setFragile(dto.getFragile());
        item.setWidth(dto.getWidth());
        item.setHeight(dto.getHeight());
        item.setDepth(dto.getDepth());
        item.setWeight(dto.getWeight());
        if (dto.getQuantity() != null) {
            item.setQuantity(dto.getQuantity());
        }
        WarehouseItem updated = warehouseRepository.save(item);
        return warehouseMapper.toDto(updated);
    }
}
