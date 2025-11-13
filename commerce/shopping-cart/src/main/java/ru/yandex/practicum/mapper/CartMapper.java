package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.Cart;

@Component
@RequiredArgsConstructor
public class CartMapper {

    public ShoppingCartDto toCartItemRequestDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getId());
        dto.setProducts(cart.getProducts());
        return dto;
    }
}
