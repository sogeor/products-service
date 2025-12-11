package com.sogeor.service.products.service;

import com.sogeor.service.products.domain.Product;
import com.sogeor.service.products.dto.ProductEvent;
import com.sogeor.service.products.dto.ProductRequest;
import com.sogeor.service.products.dto.ProductResponse;
import com.sogeor.service.products.event.ProductEventProducer;
import com.sogeor.service.products.mapper.ProductMapper;
import com.sogeor.service.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductEventProducer productEventProducer;

    public Flux<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll() // Note: Reactive Mongo doesn't support Pageable directly efficiently like
                                // paging repo, but typically findAll(Sort) or just find all and skip/limit.
                                // Ideal for Mongo Reactive:
                                // .skip(pageable.getOffset()).take(pageable.getPageSize())
                                .skip(pageable.getOffset()).take(pageable.getPageSize()).map(productMapper::toResponse);
    }

    public Mono<ProductResponse> getProductById(String id) {
        return productRepository.findById(id).map(productMapper::toResponse);
    }

    public Flux<ProductResponse> searchProducts(String query) {
        // Simple search by name or category
        return productRepository.findByNameContainingIgnoreCase(query).map(productMapper::toResponse);
    }

    @Transactional
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productRepository.save(product).flatMap(savedProduct -> {
            ProductEvent event = ProductEvent.builder()
                                             .eventType("PRODUCT_CREATED")
                                             .productId(savedProduct.getId())
                                             .name(savedProduct.getName())
                                             .price(savedProduct.getPrice())
                                             .category(savedProduct.getCategory())
                                             .timestamp(Instant.now())
                                             .build();
            return productEventProducer.sendEvent(event).thenReturn(productMapper.toResponse(savedProduct));
        });
    }

    @Transactional
    public Mono<ProductResponse> updateProduct(String id, ProductRequest request) {
        return productRepository.findById(id).flatMap(existingProduct -> {
            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            existingProduct.setCategory(request.getCategory());
            return productRepository.save(existingProduct);
        }).flatMap(updatedProduct -> {
            ProductEvent event = ProductEvent.builder()
                                             .eventType("PRODUCT_UPDATED")
                                             .productId(updatedProduct.getId())
                                             .name(updatedProduct.getName())
                                             .price(updatedProduct.getPrice())
                                             .category(updatedProduct.getCategory())
                                             .timestamp(Instant.now())
                                             .build();
            return productEventProducer.sendEvent(event).thenReturn(productMapper.toResponse(updatedProduct));
        });
    }

    @Transactional
    public Mono<Void> deleteProduct(String id) {
        return productRepository.findById(id)
                                .flatMap(product -> productRepository.delete(product).then(Mono.defer(() -> {
                                    ProductEvent event = ProductEvent.builder()
                                                                     .eventType("PRODUCT_DELETED")
                                                                     .productId(id)
                                                                     .timestamp(Instant.now())
                                                                     .build();
                                    return productEventProducer.sendEvent(event);
                                })));
    }

}
