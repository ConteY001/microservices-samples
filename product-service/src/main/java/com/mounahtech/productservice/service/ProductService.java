package com.mounahtech.productservice.service;

import com.mounahtech.productservice.dto.ProductRequest;
import com.mounahtech.productservice.dto.ProductResponse;
import com.mounahtech.productservice.model.Product;
import com.mounahtech.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void creatProduct(ProductRequest productRequest) {
        Product product = mapToProduct(productRequest);
        productRepository.save(product);
        log.info("Product with ID {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProcts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    private Product mapToProduct(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }
}
