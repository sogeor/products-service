package com.sogeor.service.products.service;

import com.sogeor.service.products.dto.ProductCategory;
import com.sogeor.service.products.dto.event.EventType;
import com.sogeor.service.products.dto.event.ProductCategoryEvent;
import com.sogeor.service.products.dto.web.ProductCategoryRequest;
import com.sogeor.service.products.dto.web.ProductCategoryResponse;
import com.sogeor.service.products.event.EventProducer;
import com.sogeor.service.products.mapper.ProductCategoryMapper;
import com.sogeor.service.products.repository.ProductCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository repository;

    @Mock
    private ProductCategoryMapper mapper;

    @Mock
    private EventProducer eventProducer;

    @InjectMocks
    private ProductCategoryService service;

    @Test
    @DisplayName("create: should save category, send event, and return response")
    void create_shouldSaveCategoryAndSendEvent() {
        // Given
        ProductCategoryRequest request = ProductCategoryRequest.builder().name("Category 1").build();
        ProductCategory category = ProductCategory.builder().name("Category 1").build();
        ProductCategory savedCategory = ProductCategory.builder()
                                                       .uuid(UUID.randomUUID().toString())
                                                       .name("Category 1")
                                                       .build();
        ProductCategoryResponse response = ProductCategoryResponse.builder()
                                                                  .uuid(savedCategory.getUuid())
                                                                  .name("Category 1")
                                                                  .build();

        when(mapper.toEntity(request)).thenReturn(category);
        when(repository.save(category)).thenReturn(Mono.just(savedCategory));
        when(eventProducer.send(any(ProductCategoryEvent.class))).thenReturn(Mono.empty());
        when(mapper.toResponse(savedCategory)).thenReturn(response);

        // When
        Mono<ProductCategoryResponse> result = service.create(request);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();

        verify(repository).save(category);
        verify(eventProducer).send(
                argThat((ProductCategoryEvent event) -> event.getType() == EventType.PRODUCT_CATEGORY_CREATED &&
                                                        event.getCategory().equals(savedCategory) &&
                                                        event.getInstant() != null));
    }

    @Test
    @DisplayName("get: should return category response when found")
    void get_shouldReturnResponse_whenFound() {
        // Given
        String uuid = UUID.randomUUID().toString();
        ProductCategory category = ProductCategory.builder().uuid(uuid).name("Category 1").build();
        ProductCategoryResponse response = ProductCategoryResponse.builder().uuid(uuid).name("Category 1").build();

        when(repository.findById(uuid)).thenReturn(Mono.just(category));
        when(mapper.toResponse(category)).thenReturn(response);

        // When
        Mono<ProductCategoryResponse> result = service.get(uuid);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();
    }

    @Test
    @DisplayName("get: should return empty when not found")
    void get_shouldReturnEmpty_whenNotFound() {
        // Given
        String uuid = UUID.randomUUID().toString();
        when(repository.findById(uuid)).thenReturn(Mono.empty());

        // When
        Mono<ProductCategoryResponse> result = service.get(uuid);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    @DisplayName("createOrUpdate: should update category, send event, and return response")
    void createOrUpdate_shouldUpdateCategoryAndSendEvent() {
        // Given
        String uuid = UUID.randomUUID().toString();
        ProductCategoryRequest request = ProductCategoryRequest.builder().name("Updated Category").build();
        ProductCategory category = ProductCategory.builder().name("Updated Category").build();
        ProductCategory savedCategory = ProductCategory.builder().uuid(uuid).name("Updated Category").build();
        ProductCategoryResponse response = ProductCategoryResponse.builder()
                                                                  .uuid(uuid)
                                                                  .name("Updated Category")
                                                                  .build();

        when(mapper.toEntity(request)).thenReturn(category);
        when(repository.save(category)).thenReturn(Mono.just(savedCategory));
        when(eventProducer.send(any(ProductCategoryEvent.class))).thenReturn(Mono.empty());
        when(mapper.toResponse(savedCategory)).thenReturn(response);

        // When
        Mono<ProductCategoryResponse> result = service.createOrUpdate(uuid, request);

        // Then
        StepVerifier.create(result).expectNext(response).verifyComplete();

        // Use an ArgumentCaptor or check logic to ensure uuid was set on the category
        // passed to save if strict check is needed.
        // But here `category` object reference is modified in the service
        // `category.setUuid(uuid)`.
        // Since `mapper.toEntity` returns the `category` object which we hold a
        // reference to, we can assert on it.
        assert category.getUuid().equals(uuid);

        verify(repository).save(category);
        verify(eventProducer).send(argThat((ProductCategoryEvent event) ->
                                                   event.getType() == EventType.PRODUCT_CATEGORY_CREATED_OR_UPDATED &&
                                                   event.getCategory().equals(savedCategory)));
    }

    @Test
    @DisplayName("delete: should delete category and send event")
    void delete_shouldDeleteCategoryAndSendEvent() {
        // Given
        String uuid = UUID.randomUUID().toString();
        when(repository.deleteById(uuid)).thenReturn(Mono.empty());
        when(eventProducer.send(any(ProductCategoryEvent.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = service.delete(uuid);

        // Then
        StepVerifier.create(result).verifyComplete();

        verify(repository).deleteById(uuid);
        verify(eventProducer).send(
                argThat((ProductCategoryEvent event) -> event.getType() == EventType.PRODUCT_CATEGORY_DELETED &&
                                                        event.getCategory().getUuid().equals(uuid)));
    }

    @Test
    @DisplayName("search: should return flux of categories")
    void search_shouldReturnCategories() {
        // Given
        String name = "test";
        int limit = 10;
        ProductCategory category1 = ProductCategory.builder().uuid(UUID.randomUUID().toString()).name("Test 1").build();
        ProductCategory category2 = ProductCategory.builder().uuid(UUID.randomUUID().toString()).name("Test 2").build();
        ProductCategoryResponse response1 = ProductCategoryResponse.builder()
                                                                   .uuid(category1.getUuid())
                                                                   .name("Test 1")
                                                                   .build();
        ProductCategoryResponse response2 = ProductCategoryResponse.builder()
                                                                   .uuid(category2.getUuid())
                                                                   .name("Test 2")
                                                                   .build();

        when(repository.findProductCategoriesByNameContainsIgnoreCase(eq(name), any(Limit.class))).thenReturn(
                Flux.just(category1, category2));
        when(mapper.toResponse(category1)).thenReturn(response1);
        when(mapper.toResponse(category2)).thenReturn(response2);

        // When
        Flux<ProductCategoryResponse> result = service.search(name, limit);

        // Then
        StepVerifier.create(result).expectNext(response1).expectNext(response2).verifyComplete();

        verify(repository).findProductCategoriesByNameContainsIgnoreCase(eq(name), any(Limit.class));
    }

}
