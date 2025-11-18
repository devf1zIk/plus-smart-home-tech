package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseItem;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void addNewProductToWarehouse(NewProductWarehouseRequestDto newProductDto) {
        if (newProductDto == null) {
            throw new ProductOperationException("Данные о новом товаре не могут быть пустыми");
        }
        Long quantity = (newProductDto.getQuantity() != null) ? newProductDto.getQuantity() : 0L;

        WarehouseItem product = WarehouseItem.builder()
                .productId(newProductDto.getProductId())
                .weight(newProductDto.getWeight())
                .fragile(newProductDto.getFragile())
                .depth(newProductDto.getDimension().getDepth())
                .height(newProductDto.getDimension().getHeight())
                .width(newProductDto.getDimension().getWidth())
                .quantity(quantity)
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
            if (warehouseItem.getWidth() == null || warehouseItem.getHeight() == null || warehouseItem.getDepth() == null) {
                throw new ProductOperationException("Товар не имеет размеров: " + productId);
            }
            totalWeight += warehouseItem.getWeight() * requestedQuantity;
            totalVolume += warehouseItem.getWidth() * warehouseItem.getHeight() * warehouseItem.getDepth() * requestedQuantity;
            hasFragile = hasFragile || Boolean.TRUE.equals(warehouseItem.getFragile());
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    public void updateProductToWarehouse(AddProductToWarehouseRequestDto addDto) {
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
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> products = request.getProducts();
        if (products == null || products.isEmpty()) {
            throw new NoProductsInShoppingCartException("Корзина пуста");
        }

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;

        for (var entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseItem item = warehouseRepository.findByProductId(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден на складе: " + productId));

            if (item.getQuantity() < quantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Недостаточно товара на складе: " + productId);
            }

            item.setQuantity(item.getQuantity() - quantity);
            warehouseRepository.save(item);

            totalWeight += item.getWeight() * quantity;
            totalVolume += item.getWidth() * item.getHeight() * item.getDepth() * quantity;
            fragile = fragile || Boolean.TRUE.equals(item.getFragile());
        }

        OrderBooking booking = OrderBooking.builder()
                .bookingId(UUID.randomUUID())
                .orderId(request.getOrderId())
                .totalWeight(totalWeight)
                .totalVolume(totalVolume)
                .fragile(fragile)
                .products(products)
                .build();

        orderBookingRepository.save(booking);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {

        OrderBooking booking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow();

        booking.setDeliveryId(request.getDeliveryId());

        orderBookingRepository.save(booking);
    }

    @Transactional
    public void returnProduct(Map<UUID, Integer> products) {

        if (products == null || products.isEmpty()) {
            throw new NoProductsInShoppingCartException("Список товаров для возврата пуст");
        }

        for (var entry : products.entrySet()) {
            UUID productId = entry.getKey();
            long quantity = entry.getValue();

            WarehouseItem warehouseItem = warehouseRepository.findByProductId(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден на складе: " + productId));

            warehouseItem.setQuantity(warehouseItem.getQuantity() + quantity);
            warehouseRepository.save(warehouseItem);
        }
    }

}
