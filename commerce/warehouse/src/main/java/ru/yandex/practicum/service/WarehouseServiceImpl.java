package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequestDto newProductDto) {

        WarehouseItem product = WarehouseItem.builder()
                .width(newProductDto.getWidth())
                .height(newProductDto.getHeight())
                .weight(newProductDto.getWeight())
                .fragile(newProductDto.getFragile())
                .build();

        productRepository.save(product);
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (var entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

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
    public void updateProductToWarehouse(AddProductToWarehouseRequestDto addDto) {

        var warehouseItemOpt = warehouseRepository.findByProductId(addDto.getProductId());
        if (warehouseItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Товар не найден на складе: " + addDto.getProductId());
        }

        var warehouseItem = warehouseItemOpt.get();
        warehouseItem.setQuantity(warehouseItem.getQuantity() + addDto.getQuantity());
        warehouseRepository.save(warehouseItem);

    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .city("Москва")
                .street("Ленинградский проспект")
                .building("12А")
                .postalCode("125040")
                .build();
    }
}