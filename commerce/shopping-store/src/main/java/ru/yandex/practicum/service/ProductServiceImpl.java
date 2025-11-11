package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ProductOperationException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Map<String, Object> getProducts(ProductCategory category, Pageable pageable) {
        try {
            Page<Product> products;
            if (category != null) {
                products = repository.findByProductStateAndProductCategory(ProductState.ACTIVE, category, pageable);
            } else {
                products = repository.findByProductState(ProductState.ACTIVE, pageable);
            }

            List<ProductDto> content = products.getContent().stream()
                    .map(mapper::toDto)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("pageable", Map.of(
                    "pageNumber", products.getNumber(),
                    "pageSize", products.getSize(),
                    "sort", products.getSort().toString()
            ));
            response.put("totalElements", products.getTotalElements());
            response.put("totalPages", products.getTotalPages());
            response.put("last", products.isLast());
            response.put("first", products.isFirst());
            response.put("numberOfElements", products.getNumberOfElements());

            return response;
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при получении списка продуктов");
        }
    }

    public ProductDto getProductById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));
        return mapper.toDto(product);
    }

    public ProductDto create(ProductDto dto) {
        try {
            Product product = mapper.toEntity(dto);
            product.setProductState(ProductState.ACTIVE);
            Product saved = repository.save(product);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при создании продукта: " + dto.getProductName());
        }
    }

    public ProductDto update(ProductDto dto) {
        Product product = repository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + dto.getProductId()));

        mapper.updateEntity(product, dto);
        Product updated = repository.save(product);
        return mapper.toDto(updated);
    }

    public Boolean deactivate(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    public Boolean updateQuantityState(SetQuantityDto setQuantityDto) {
        Product product = repository.findById(setQuantityDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + setQuantityDto.getProductId()));

        product.setQuantityState(setQuantityDto.getQuantityState());
        repository.save(product);
        return true;
    }
}
