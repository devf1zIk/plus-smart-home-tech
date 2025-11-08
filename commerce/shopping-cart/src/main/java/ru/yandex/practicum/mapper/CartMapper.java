package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.dto.CartItemResponseDto;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartResponseDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUsername(cart.getUsername());
        dto.setStatus(cart.getStatus());

        List<CartItemResponseDto> itemsDto = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponseDto itemDto = cartItemMapper.toDto(item);
                itemsDto.add(itemDto);
            }
        }
        dto.setItems(itemsDto);

        return dto;
    }

}
