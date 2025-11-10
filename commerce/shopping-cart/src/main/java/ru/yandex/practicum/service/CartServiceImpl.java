package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.enums.CartState;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.repository.CartRepository;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    private Cart getOrCreateCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUsername(username);
                    cart.setStatus(CartState.ACTIVE);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartResponseDto getCart(String username) {
        Cart cart = getOrCreateCart(username);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto addProduct(String username, Map<UUID, Long> products) {
        Cart cart = getOrCreateCart(username);

        if (cart.getStatus() == CartState.DEACTIVATED) {
            throw new CartDeactivatedException();
        }

        products.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum)
        );

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto deleteProduct(String username, Set<UUID> productIds) {
        Cart cart = getOrCreateCart(username);

        for (UUID productId : productIds) {
            cart.getProducts().remove(productId);
        }

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto deactivateCart(String username) {
        Cart cart = getOrCreateCart(username);
        cart.setStatus(CartState.DEACTIVATED);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto updateProductQuantity(String username, CartItemRequestDto requestDto) {
        Cart cart = getOrCreateCart(username);

        if (!cart.getProducts().containsKey(requestDto.getProductId())) {
            throw new IllegalArgumentException("Товар с id " + requestDto.getProductId() + " не найден в корзине");
        }

        if (requestDto.getQuantity() == 0) {
            cart.getProducts().remove(requestDto.getProductId());
        } else {
            cart.getProducts().put(requestDto.getProductId(), requestDto.getQuantity());
        }

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }
}
