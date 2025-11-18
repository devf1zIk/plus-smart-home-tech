package ru.yandex.practicum.client;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import java.util.Map;
import java.util.UUID;

public interface WarehousOperations {

    void addNewProductToWarehouse(NewProductWarehouseRequestDto newProductWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void updateProductToWarehouse(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void returnProduct(Map<UUID, Integer> products);
}
