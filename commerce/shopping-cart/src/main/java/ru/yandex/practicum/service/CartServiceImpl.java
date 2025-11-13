package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.CartState;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.exception.CartNotFoundException;
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
    public ShoppingCartDto getCart(String username) {
        Cart cart = getOrCreateCart(username);
        return cartMapper.toCartItemRequestDto(cart);
    }

    @Override
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        Cart cart = getOrCreateCart(username);

        if (cart.getStatus() == CartState.DEACTIVATE) {
            throw new CartDeactivatedException();
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            cart.getProducts().merge(productId, quantity, Long::sum);
        }

        cartRepository.save(cart);
        return cartMapper.toCartItemRequestDto(cart);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        Cart cart = getOrCreateCart(username);

        for (UUID productId : productIds) {
            cart.getProducts().remove(productId);
        }

        cartRepository.save(cart);
        return cartMapper.toCartItemRequestDto(cart);
    }

    @Override
    public ShoppingCartDto deactivate(String username) {
        Cart cart = getOrCreateCart(username);
        cart.setStatus(CartState.DEACTIVATE);
        cartRepository.save(cart);
        return cartMapper.toCartItemRequestDto(cart);
    }

    @Override
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest requestDto) {
        Cart cart = getOrCreateCart(username);

        UUID productId = requestDto.getProductId();
        Long newQuantity = requestDto.getNewQuantity();

        if (!cart.getProducts().containsKey(productId)) {
            throw new CartNotFoundException("Товар не найден в корзине: " + productId);
        }

        if (newQuantity == null || newQuantity <= 0) {
            cart.getProducts().remove(productId);
        } else {
            cart.getProducts().put(productId, newQuantity);
        }

        cartRepository.save(cart);
        return cartMapper.toCartItemRequestDto(cart);
    }
}
