package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartItemRequestDto;
import ru.yandex.practicum.dto.CartResponseDto;
import ru.yandex.practicum.enums.CartState;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.exception.CartItemNotFoundException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.repository.CartRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    private Cart getOrCreateCart(String username) {
        Optional<Cart> existing = cartRepository.findByUsername(username);
        if (existing.isPresent()) {
            return existing.get();
        }

        Cart cart = new Cart();
        cart.setUsername(username);
        cart.setStatus(CartState.ACTIVE);
        return cartRepository.save(cart);
    }

    @Override
    public CartResponseDto getCart(String username) {
        Cart cart = getOrCreateCart(username);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto addItem(String username, CartItemRequestDto dto) {
        Cart cart = getOrCreateCart(username);

        if (cart.getStatus() == CartState.DEACTIVATE) {
            throw new CartDeactivatedException();
        }

        CartItem item = new CartItem();
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setQuantity(dto.getQuantity());
        item.setCart(cart);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        cart.getItems().add(item);
        cartItemRepository.save(item);
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto removeItem(String username, UUID itemId) {
        Cart cart = getOrCreateCart(username);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartItemNotFoundException(itemId);
        }

        CartItem target = null;
        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(itemId)) {
                target = item;
                break;
            }
        }

        if (target == null) {
            throw new CartItemNotFoundException(itemId);
        }

        cart.getItems().remove(target);
        cartItemRepository.deleteById(itemId);
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto deactivateCart(String username) {
        Cart cart = getOrCreateCart(username);
        cart.setStatus(CartState.DEACTIVATE);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
        }

        cartRepository.save(cart);
    }
}
