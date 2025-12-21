package com.sogeor.service.products.service;

import com.sogeor.service.products.dto.Product;
import com.sogeor.service.products.dto.event.EventType;
import com.sogeor.service.products.dto.event.ProductEvent;
import com.sogeor.service.products.dto.web.ProductRequest;
import com.sogeor.service.products.dto.web.ProductResponse;
import com.sogeor.service.products.event.EventProducer;
import com.sogeor.service.products.mapper.ProductMapper;
import com.sogeor.service.products.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @Mock
    private EventProducer eventProducer;

    @InjectMocks
    private ProductService service;

    @Test
    @DisplayName("create: should save product, send event, and return response")
    void create_shouldSaveProductAndSendEvent() {
        // Given
        ProductRequest request = ProductRequest.builder().name("Product 1").build();
        Product product = Product.builder().name("Product 1").build();
        Product savedProduct = Product.builder().uuid(UUID.randomUUID()).name("Product 1").build();
        ProductResponse response = ProductResponse.builder()
                                                  .uuid(savedProduct.getUuid())
                                                  .category(UUID.randomUUID())
                                                  .name("Product 1")
                                                  .price(BigDecimal.ONE)
                                                  .build();

        when(mapper.toEntity(request)).thenReturn(product);
        when(repository.save(product)).thenReturn(Mono.just(savedProduct));
        when(eventProducer.send(any(ProductEvent.class))).thenReturn(Mono.empty());
        when(mapper.toResponse(savedProduct)).thenReturn(response);

        // When
        Mono<ProductResponse> result = service.create(request);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();

        verify(repository).save(product);
        verify(eventProducer).send(argThat((ProductEvent event) -> event.getType() == EventType.PRODUCT_CREATED &&
                                                                   event.getProduct().equals(savedProduct)));
    }

    @Test
    @DisplayName("get: should return product response when found by id")
    void get_shouldReturnResponse_whenFoundById() {
        // Given
        UUID uuid = UUID.randomUUID();
        Product product = Product.builder().uuid(uuid).name("Product 1").build();
        ProductResponse response = ProductResponse.builder()
                                                  .uuid(uuid)
                                                  .category(UUID.randomUUID())
                                                  .name("Product 1")
                                                  .price(BigDecimal.ONE)
                                                  .build();

        when(repository.findById(uuid)).thenReturn(Mono.just(product));
        when(mapper.toResponse(product)).thenReturn(response);

        // When
        Mono<ProductResponse> result = service.get(uuid);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();
    }

    @Test
    @DisplayName("get: should return product responses when found by category with pagination")
    void get_shouldReturnResponses_whenFoundByCategory() {
        // Given
        UUID categoryId = UUID.randomUUID();
        int page = 0;
        int count = 10;
        Product product1 = Product.builder().uuid(UUID.randomUUID()).category(categoryId).build();
        Product product2 = Product.builder().uuid(UUID.randomUUID()).category(categoryId).build();
        ProductResponse response1 = ProductResponse.builder()
                                                   .uuid(product1.getUuid())
                                                   .category(UUID.randomUUID())
                                                   .name("1")
                                                   .price(BigDecimal.ONE)
                                                   .build();
        ProductResponse response2 = ProductResponse.builder()
                                                   .uuid(product2.getUuid())
                                                   .category(UUID.randomUUID())
                                                   .name("2")
                                                   .price(BigDecimal.ONE)
                                                   .build();

        // Note: Pageable equality might be strict, so use any(Pageable.class) or
        // eq(Pageable.ofSize(count).withPage(page)) if equals logic works.
        // It's safer to rely on any() or ArgumentCaptor if Pageable equals isn't
        // standard, but typically it works.
        when(repository.findProductsByCategory(eq(categoryId), any(Pageable.class))).thenReturn(
                Flux.just(product1, product2));
        when(mapper.toResponse(product1)).thenReturn(response1);
        when(mapper.toResponse(product2)).thenReturn(response2);

        // When
        Flux<ProductResponse> result = service.get(categoryId, page, count);

        // Then
        StepVerifier.create(result).expectNext(response1).expectNext(response2).verifyComplete();

        verify(repository).findProductsByCategory(eq(categoryId), any(Pageable.class));
    }

    @Test
    @DisplayName("createOrUpdate: should update product, send event, and return response")
    void createOrUpdate_shouldUpdateProductAndSendEvent() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProductRequest request = ProductRequest.builder().name("Updated Product").build();
        Product product = Product.builder().name("Updated Product").build();
        Product savedProduct = Product.builder().uuid(uuid).name("Updated Product").build();
        ProductResponse response = ProductResponse.builder()
                                                  .uuid(uuid)
                                                  .category(UUID.randomUUID())
                                                  .name("Updated Product")
                                                  .price(BigDecimal.ONE)
                                                  .build();

        when(mapper.toEntity(request)).thenReturn(product);
        when(repository.save(product)).thenReturn(Mono.just(savedProduct));
        when(eventProducer.send(any(ProductEvent.class))).thenReturn(Mono.empty());
        when(mapper.toResponse(savedProduct)).thenReturn(response);

        // When
        Mono<ProductResponse> result = service.createOrUpdate(uuid, request);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();

        assert product.getUuid().equals(uuid);

        verify(repository).save(product);

        // Note: The service sends PRODUCT_CREATED even for createOrUpdate in the
        // provided code.
        // I should stick to what the code does, which is PRODUCT_CREATED.
        // Wait, checking the view_file for ProductService line 88:
        // .type(EventType.PRODUCT_CREATED).
        // It seems the service code uses PRODUCT_CREATED for update too?
        // ProductCategoryService uses PRODUCT_CATEGORY_CREATED_OR_UPDATED.
        // ProductService uses PRODUCT_CREATED. I will assert PRODUCT_CREATED as per
        // code.
        verify(eventProducer).send(argThat((ProductEvent event) -> event.getType() == EventType.PRODUCT_CREATED &&
                                                                   event.getProduct().equals(savedProduct)));
    }

    @Test
    @DisplayName("delete: should delete product and send event")
    void delete_shouldDeleteProductAndSendEvent() {
        // Given
        UUID uuid = UUID.randomUUID();
        when(repository.deleteById(uuid)).thenReturn(Mono.empty());
        when(eventProducer.send(any(ProductEvent.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = service.delete(uuid);

        // Then
        StepVerifier.create(result).verifyComplete();

        verify(repository).deleteById(uuid);
        verify(eventProducer).send(argThat((ProductEvent event) -> event.getType() == EventType.PRODUCT_DELETED &&
                                                                   event.getProduct().getUuid().equals(uuid)));
    }

    @Test
    @DisplayName("search: should return flux of products")
    void search_shouldReturnProducts() {
        // Given
        String name = "test";
        int limit = 10;
        Product product1 = Product.builder().uuid(UUID.randomUUID()).name("Test 1").build();
        Product product2 = Product.builder().uuid(UUID.randomUUID()).name("Test 2").build();
        ProductResponse response1 = ProductResponse.builder()
                                                   .uuid(product1.getUuid())
                                                   .category(UUID.randomUUID())
                                                   .name("Test 1")
                                                   .price(BigDecimal.ONE)
                                                   .build();
        ProductResponse response2 = ProductResponse.builder()
                                                   .uuid(product2.getUuid())
                                                   .category(UUID.randomUUID())
                                                   .name("Test 2")
                                                   .price(BigDecimal.ONE)
                                                   .build();

        when(repository.findProductsByNameContainsIgnoreCase(eq(name), any(Limit.class))).thenReturn(
                Flux.just(product1, product2));
        when(mapper.toResponse(product1)).thenReturn(response1);
        when(mapper.toResponse(product2)).thenReturn(response2);

        // When
        Flux<ProductResponse> result = service.search(name, limit);

        // Then
        StepVerifier.create(result).expectNext(response1).expectNext(response2).verifyComplete();

        verify(repository).findProductsByNameContainsIgnoreCase(eq(name), any(Limit.class));
    }

}
