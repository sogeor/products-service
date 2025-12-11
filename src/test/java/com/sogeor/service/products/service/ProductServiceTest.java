package com.sogeor.service.products.service;

import com.sogeor.service.products.domain.Product;
import com.sogeor.service.products.dto.ProductEvent;
import com.sogeor.service.products.dto.ProductRequest;
import com.sogeor.service.products.dto.ProductResponse;
import com.sogeor.service.products.event.ProductEventProducer;
import com.sogeor.service.products.mapper.ProductMapper;
import com.sogeor.service.products.repository.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductEventProducer productEventProducer;

    @InjectMocks
    private ProductService productService;

    private Product product;

    private ProductRequest productRequest;

    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                         .id("1")
                         .name("Test Product")
                         .price(BigDecimal.TEN)
                         .category("Test Category")
                         .build();

        productRequest = ProductRequest.builder()
                                       .name("Test Product")
                                       .price(BigDecimal.TEN)
                                       .category("Test Category")
                                       .build();

        productResponse = ProductResponse.builder()
                                         .id("1")
                                         .name("Test Product")
                                         .price(BigDecimal.TEN)
                                         .category("Test Category")
                                         .build();
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.just(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        Flux<@NotNull ProductResponse> result = productService.getAllProducts(PageRequest.of(0, 10));

        StepVerifier.create(result).expectNext(productResponse).verifyComplete();
    }

    @Test
    void getProductById() {
        when(productRepository.findById("1")).thenReturn(Mono.just(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        Mono<@NotNull ProductResponse> result = productService.getProductById("1");

        StepVerifier.create(result).expectNext(productResponse).verifyComplete();
    }

    @Test
    void createProduct() {
        when(productMapper.toEntity(any(ProductRequest.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
        when(productEventProducer.sendEvent(any(ProductEvent.class))).thenReturn(Mono.empty());

        Mono<@NotNull ProductResponse> result = productService.createProduct(productRequest);

        StepVerifier.create(result).expectNext(productResponse).verifyComplete();

        verify(productEventProducer).sendEvent(any(ProductEvent.class));
    }

    @Test
    void updateProduct() {
        when(productRepository.findById("1")).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
        when(productEventProducer.sendEvent(any(ProductEvent.class))).thenReturn(Mono.empty());

        Mono<@NotNull ProductResponse> result = productService.updateProduct("1", productRequest);

        StepVerifier.create(result).expectNext(productResponse).verifyComplete();

        verify(productEventProducer).sendEvent(any(ProductEvent.class));
    }

    @Test
    void deleteProduct() {
        when(productRepository.findById("1")).thenReturn(Mono.just(product));
        when(productRepository.delete(any(Product.class))).thenReturn(Mono.empty());
        when(productEventProducer.sendEvent(any(ProductEvent.class))).thenReturn(Mono.empty());

        Mono<@NotNull Void> result = productService.deleteProduct("1");

        StepVerifier.create(result).verifyComplete();

        verify(productEventProducer).sendEvent(any(ProductEvent.class));
    }

}
