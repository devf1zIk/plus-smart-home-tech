package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ProductOperationException;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.repository.WarehouseRepository;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void addNewProductToWarehouse(NewProductWarehouseRequestDto newProductDto) {

        WarehouseItem product = WarehouseItem.builder()
                .productId(newProductDto.getProductId())
                .weight(newProductDto.getWeight())
                .fragile(newProductDto.getFragile())
                .build();

        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        if (shoppingCartDto == null || shoppingCartDto.getProducts() == null) {
            throw new ProductNotFoundException("Корзина покупателя не может быть пустой");
        }

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (var entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            if (requestedQuantity == null || requestedQuantity <= 0) {
                throw new ProductOperationException("Количество товара должно быть положительным: " + productId);
            }
            var warehouseItemOpt = warehouseRepository.findByProductId(productId);
            if (warehouseItemOpt.isEmpty() || warehouseItemOpt.get().getQuantity() < requestedQuantity) {
                throw new ProductNotFoundException("Недостаточно товара на складе: " + productId);
            }

            WarehouseItem warehouseItem = warehouseItemOpt.get();
            totalWeight += warehouseItem.getWeight() * requestedQuantity;
            totalVolume += warehouseItem.getWidth() * warehouseItem.getHeight() * warehouseItem.getDepth() * requestedQuantity;
            hasFragile = hasFragile || warehouseItem.getFragile();
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    public void updateProductToWarehouse(ProductWarehouseRequestDto addDto) {
        if (addDto == null) {
            throw new ProductNotFoundException("Данные для обновления товара не могут быть пустыми");
        }

        if (addDto.getQuantity() == null) {
            throw new ProductNotFoundException("Количество товара обязательно к указанию");
        }
        var warehouseItemOpt = warehouseRepository.findByProductId(addDto.getProductId());
        if (warehouseItemOpt.isEmpty()) {
            throw new ProductNotFoundException("Товар не найден на складе: " + addDto.getProductId());
        }

        var warehouseItem = warehouseItemOpt.get();
        long newQuantity = warehouseItem.getQuantity() + addDto.getQuantity();
        if (newQuantity < 0) {
            throw new ProductNotFoundException("Результирующее количество не может быть отрицательным: " + addDto.getProductId());
        }
        warehouseItem.setQuantity(newQuantity);
        warehouseRepository.save(warehouseItem);

    }

    @Override
    public AddressDto getWarehouseAddress() {
        return switch (CURRENT_ADDRESS) {
            case "ADDRESS_1" -> AddressDto.builder()
                    .country("Россия")
                    .city("Москва")
                    .street("Ленинский проспект")
                    .building("123")
                    .postalCode("119991")
                    .build();
            case "ADDRESS_2" -> AddressDto.builder()
                    .country("Россия")
                    .city("Санкт-Петербург")
                    .street("Невский проспект")
                    .building("456")
                    .postalCode("191025")
                    .build();
            default -> throw new IllegalStateException("Unknown address: " + CURRENT_ADDRESS);
        };
    }
}