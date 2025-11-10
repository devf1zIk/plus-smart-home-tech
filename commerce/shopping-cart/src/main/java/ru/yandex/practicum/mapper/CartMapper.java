package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.model.Cart;

@Component
@RequiredArgsConstructor
public class CartMapper {

    public CartResponseDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponseDto dto = new CartResponseDto();
        dto.setProductId(cart.getId());
        dto.setUsername(cart.getUsername());
        dto.setStatus(cart.getStatus());
        dto.setProducts(cart.getProducts());
        return dto;
    }
}
